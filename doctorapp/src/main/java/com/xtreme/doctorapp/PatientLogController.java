package com.xtreme.doctorapp;

import com.xtreme.doctorapp.domain.Doctor;
import com.xtreme.doctorapp.domain.PatientLog;
import com.xtreme.doctorapp.service.EmailService;
import com.xtreme.doctorapp.service.Message;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.finder.RooWebFinder;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@RooWebJson(jsonObject = PatientLog.class)
@Controller
@RequestMapping("/patientlogs")
@RooWebScaffold(path = "patientlogs", formBackingObject = PatientLog.class,delete=false)
@RooWebFinder
public class PatientLogController {
	
	@Autowired
	private EmailService emailService;

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid PatientLog patientLog, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, patientLog);
            return "patientlogs/create";
        }
        uiModel.asMap().clear();
        patientLog.persist();

        String text="Dear "+patientLog.getName()+",\n\n\n";
        text=text+"Your request has been logged \n\n\n";
        text=text+" Your Id : "+patientLog.getId();
       
        Message message=new Message();
        message.setBody(text);
        message.setReceiverEmail(patientLog.getEmail());
        message.setReceiverName("kiran");
        message.setSenderEmail("doctorapp@support.com");
        message.setSenderName("Doctorapp Support Team");
        message.setSubject("Appointment");
        
        emailService.send(message);
        // patientLog.sendMessage("doctorapp@support.com", "Appointment", patientLog.getEmail(), message);
        return "redirect:/patientlogs/" + encodeUrlPathSegment(patientLog.getId().toString(), httpServletRequest);
    }

	@RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		String name = auth.getName(); // get logged in username
		
		if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            if("admin".equalsIgnoreCase(name)){
            	uiModel.addAttribute("patientlogs", PatientLog.findPatientLogEntries(firstResult, sizeNo));
            }else{
            	uiModel.addAttribute("patientlogs", PatientLog.findPatientLogEntriesByDoctorName(firstResult, sizeNo,name));
            }
            float nrOfPages = (float) PatientLog.countPatientLogs() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
        	if("admin".equalsIgnoreCase(name)){
        		uiModel.addAttribute("patientlogs", PatientLog.findAllPatientLogs());	
        	}else{
        		uiModel.addAttribute("patientlogs", PatientLog.findPatientLogsByDoctorName(name));
        	}
            
        }
        addDateTimeFormatPatterns(uiModel);
        return "patientlogs/list";
    }

	@RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json) {
        PatientLog patientLog = PatientLog.fromJsonToPatientLog(json);
        patientLog.persist();
        String text="Dear "+patientLog.getName()+",\n\n\n";
        text=text+"Your request has been logged \n\n\n";
        text=text+" Your Id : "+patientLog.getId();
        
        Message message=new Message();
        message.setBody(text);
        message.setReceiverEmail(patientLog.getEmail());
        message.setReceiverName("kiran");
        message.setSenderEmail("doctorapp@support.com");
        message.setSenderName("Doctorapp Support Team");
        message.setSubject("Appointment");
        
        emailService.send(message);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(value = "/{id}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        PatientLog patientLog = PatientLog.findPatientLog(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (patientLog == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(patientLog.toJson(), headers, HttpStatus.OK);
    }

	@RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<PatientLog> result = PatientLog.findAllPatientLogs();
        return new ResponseEntity<String>(PatientLog.toJsonArray(result), headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (PatientLog patientLog: PatientLog.fromJsonArrayToPatientLogs(json)) {
            patientLog.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        PatientLog patientLog = PatientLog.fromJsonToPatientLog(json);
        if (patientLog.merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/jsonArray", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJsonArray(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        for (PatientLog patientLog: PatientLog.fromJsonArrayToPatientLogs(json)) {
            if (patientLog.merge() == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        PatientLog patientLog = PatientLog.findPatientLog(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (patientLog == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        patientLog.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(params = "find=ByAppointmentDateEqualsAndDoctor", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindPatientLogsByAppointmentDateEqualsAndDoctor(@RequestParam("appointmentDate") @DateTimeFormat(style = "M-") Date appointmentDate, @RequestParam("doctor") Doctor doctor) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(PatientLog.toJsonArray(PatientLog.findPatientLogsByAppointmentDateEqualsAndDoctor(appointmentDate, doctor).getResultList()), headers, HttpStatus.OK);
    }

	@RequestMapping(params = "find=ByDoctor", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindPatientLogsByDoctor(@RequestParam("doctor") Doctor doctor) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(PatientLog.toJsonArray(PatientLog.findPatientLogsByDoctor(doctor).getResultList()), headers, HttpStatus.OK);
    }

	@RequestMapping(params = { "find=ByAppointmentDateEqualsAndDoctor", "form" }, method = RequestMethod.GET)
    public String findPatientLogsByAppointmentDateEqualsAndDoctorForm(Model uiModel) {
        uiModel.addAttribute("doctors", Doctor.findAllDoctors());
        addDateTimeFormatPatterns(uiModel);
        return "patientlogs/findPatientLogsByAppointmentDateEqualsAndDoctor";
    }

	@RequestMapping(params = "find=ByAppointmentDateEqualsAndDoctor", method = RequestMethod.GET)
    public String findPatientLogsByAppointmentDateEqualsAndDoctor(@RequestParam("appointmentDate") @DateTimeFormat(style = "M-") Date appointmentDate, @RequestParam("doctor") Doctor doctor, Model uiModel) {
        uiModel.addAttribute("patientlogs", PatientLog.findPatientLogsByAppointmentDateEqualsAndDoctor(appointmentDate, doctor).getResultList());
        addDateTimeFormatPatterns(uiModel);
        return "patientlogs/list";
    }

	@RequestMapping(params = { "find=ByDoctor", "form" }, method = RequestMethod.GET)
    public String findPatientLogsByDoctorForm(Model uiModel) {
        uiModel.addAttribute("doctors", Doctor.findAllDoctors());
        return "patientlogs/findPatientLogsByDoctor";
    }

	@RequestMapping(params = "find=ByDoctor", method = RequestMethod.GET)
    public String findPatientLogsByDoctor(@RequestParam("doctor") Doctor doctor, Model uiModel) {
        uiModel.addAttribute("patientlogs", PatientLog.findPatientLogsByDoctor(doctor).getResultList());
        return "patientlogs/list";
    }

	@RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new PatientLog());
        return "patientlogs/create";
    }

	@RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("patientlog", PatientLog.findPatientLog(id));
        uiModel.addAttribute("itemId", id);
        return "patientlogs/show";
    }

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid PatientLog patientLog, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, patientLog);
            return "patientlogs/update";
        }
        uiModel.asMap().clear();
        patientLog.merge();
        return "redirect:/patientlogs/" + encodeUrlPathSegment(patientLog.getId().toString(), httpServletRequest);
    }

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, PatientLog.findPatientLog(id));
        return "patientlogs/update";
    }

	void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("patientLog_appointmentdate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
    }

	void populateEditForm(Model uiModel, PatientLog patientLog) {
        uiModel.addAttribute("patientLog", patientLog);
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("doctors", Doctor.findAllDoctors());
    }

	String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
}
