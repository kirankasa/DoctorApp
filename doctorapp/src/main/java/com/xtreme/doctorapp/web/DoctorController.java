package com.xtreme.doctorapp.web;

import com.xtreme.doctorapp.domain.Doctor;
import com.xtreme.doctorapp.domain.Gender;
import com.xtreme.doctorapp.domain.ServiceType;
import com.xtreme.doctorapp.domain.Specialist;
import com.xtreme.doctorapp.service.EmailService;
import com.xtreme.doctorapp.service.Message;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.finder.RooWebFinder;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
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

@RequestMapping("/doctors")
@Controller
@RooWebScaffold(path = "doctors", formBackingObject = Doctor.class,delete=false)
@RooWebJson(jsonObject = Doctor.class)
@RooWebFinder
public class DoctorController {

	@Autowired
	private EmailService emailService;
	
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid Doctor doctor, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, doctor);
            return "doctors/create";
        }
        uiModel.asMap().clear();
       
        doctor.persist();
        
        String body="Dear "+doctor.getName()+",\n\n\n";
        body=body+"Application url :http://doctorapp.cloudfoundry.com";
        body=body+"\n\n\nUsername :"+doctor.getName()+"\n\n\n";
        body=body+"Password :"+doctor.getName()+"\n\n\n";
        
        Message message=new Message();
        message.setBody(body);
        message.setReceiverEmail(doctor.getEmail());
        message.setReceiverName("kiran");
        message.setSenderEmail("doctorapp@support.com");
        message.setSenderName("Doctorapp Support Team");
        message.setSubject("Credentils");
        
        emailService.send(message);
        
       // doctor.sendbody("doctorapp@support.com", "Credentils", doctor.getEmail(), body);
        return "redirect:/doctors/" + encodeUrlPathSegment(doctor.getId().toString(), httpServletRequest);
    }

	@RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json) {
        Doctor doctor = Doctor.fromJsonToDoctor(json);
        doctor.persist();
        String body="Dear "+doctor.getName()+",\n\n\n";
        body=body+"Application url :http://doctorapp.cloudfoundry.com";
        body=body+"\n\n\nUsername :"+doctor.getName()+"\n\n\n";
        body=body+"Password :"+doctor.getName()+"\n\n\n";
        
        Message message=new Message();
        message.setBody(body);
        message.setReceiverEmail(doctor.getEmail());
        message.setReceiverName("kiran");
        message.setSenderEmail("doctorapp@support.com");
        message.setSenderName("Doctorapp Support Team");
        message.setSubject("Credentils");
        
        emailService.send(message);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(value = "/{id}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        Doctor doctor = Doctor.findDoctor(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (doctor == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(doctor.toJson(), headers, HttpStatus.OK);
    }

	@RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<Doctor> result = Doctor.findAllDoctors();
        return new ResponseEntity<String>(Doctor.toJsonArray(result), headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (Doctor doctor: Doctor.fromJsonArrayToDoctors(json)) {
            doctor.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        Doctor doctor = Doctor.fromJsonToDoctor(json);
        if (doctor.merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/jsonArray", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJsonArray(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        for (Doctor doctor: Doctor.fromJsonArrayToDoctors(json)) {
            if (doctor.merge() == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        Doctor doctor = Doctor.findDoctor(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (doctor == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        doctor.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(params = "find=ByGenderAndServiceTypeAndSpecialist", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindDoctorsByGenderAndServiceTypeAndSpecialist(@RequestParam("gender") Gender gender, @RequestParam("serviceType") ServiceType serviceType, @RequestParam("specialist") Specialist specialist) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(Doctor.toJsonArray(Doctor.findDoctorsByGenderAndServiceTypeAndSpecialist(gender, serviceType, specialist).getResultList()), headers, HttpStatus.OK);
    }

	@RequestMapping(params = "find=ByGenderAndServiceTypeAndSpecialistAndPincode", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindDoctorsByGenderAndServiceTypeAndSpecialistAndPincode(@RequestParam("gender") Gender gender, @RequestParam("serviceType") ServiceType serviceType, @RequestParam("specialist") Specialist specialist, @RequestParam("pincode") String pincode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(Doctor.toJsonArray(Doctor.findDoctorsByGenderAndServiceTypeAndSpecialistAndPincode(gender, serviceType, specialist, pincode).getResultList()), headers, HttpStatus.OK);
    }

	@RequestMapping(params = "find=ByNameEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindDoctorsByNameEquals(@RequestParam("name") String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(Doctor.toJsonArray(Doctor.findDoctorsByNameEquals(name).getResultList()), headers, HttpStatus.OK);
    }

	@RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Doctor());
        return "doctors/create";
    }

	@RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("doctor", Doctor.findDoctor(id));
        uiModel.addAttribute("itemId", id);
        return "doctors/show";
    }

	@RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("doctors", Doctor.findDoctorEntries(firstResult, sizeNo));
            float nrOfPages = (float) Doctor.countDoctors() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("doctors", Doctor.findAllDoctors());
        }
        return "doctors/list";
    }

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid Doctor doctor, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, doctor);
            return "doctors/update";
        }
        uiModel.asMap().clear();
        doctor.merge();
        return "redirect:/doctors/" + encodeUrlPathSegment(doctor.getId().toString(), httpServletRequest);
    }

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, Doctor.findDoctor(id));
        return "doctors/update";
    }

	void populateEditForm(Model uiModel, Doctor doctor) {
        uiModel.addAttribute("doctor", doctor);
        uiModel.addAttribute("genders", Arrays.asList(Gender.values()));
        uiModel.addAttribute("servicetypes", Arrays.asList(ServiceType.values()));
        uiModel.addAttribute("specialists", Specialist.findAllSpecialists());
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

	@RequestMapping(params = { "find=ByGenderAndServiceTypeAndSpecialist", "form" }, method = RequestMethod.GET)
    public String findDoctorsByGenderAndServiceTypeAndSpecialistForm(Model uiModel) {
        uiModel.addAttribute("genders", java.util.Arrays.asList(Gender.class.getEnumConstants()));
        uiModel.addAttribute("servicetypes", java.util.Arrays.asList(ServiceType.class.getEnumConstants()));
        uiModel.addAttribute("specialists", Specialist.findAllSpecialists());
        return "doctors/findDoctorsByGenderAndServiceTypeAndSpecialist";
    }

	@RequestMapping(params = "find=ByGenderAndServiceTypeAndSpecialist", method = RequestMethod.GET)
    public String findDoctorsByGenderAndServiceTypeAndSpecialist(@RequestParam("gender") Gender gender, @RequestParam("serviceType") ServiceType serviceType, @RequestParam("specialist") Specialist specialist, Model uiModel) {
        uiModel.addAttribute("doctors", Doctor.findDoctorsByGenderAndServiceTypeAndSpecialist(gender, serviceType, specialist).getResultList());
        return "doctors/list";
    }

	@RequestMapping(params = { "find=ByGenderAndServiceTypeAndSpecialistAndPincode", "form" }, method = RequestMethod.GET)
    public String findDoctorsByGenderAndServiceTypeAndSpecialistAndPincodeForm(Model uiModel) {
        uiModel.addAttribute("genders", java.util.Arrays.asList(Gender.class.getEnumConstants()));
        uiModel.addAttribute("servicetypes", java.util.Arrays.asList(ServiceType.class.getEnumConstants()));
        uiModel.addAttribute("specialists", Specialist.findAllSpecialists());
        return "doctors/findDoctorsByGenderAndServiceTypeAndSpecialistAndPincode";
    }

	@RequestMapping(params = "find=ByGenderAndServiceTypeAndSpecialistAndPincode", method = RequestMethod.GET)
    public String findDoctorsByGenderAndServiceTypeAndSpecialistAndPincode(@RequestParam("gender") Gender gender, @RequestParam("serviceType") ServiceType serviceType, @RequestParam("specialist") Specialist specialist, @RequestParam("pincode") String pincode, Model uiModel) {
        uiModel.addAttribute("doctors", Doctor.findDoctorsByGenderAndServiceTypeAndSpecialistAndPincode(gender, serviceType, specialist, pincode).getResultList());
        return "doctors/list";
    }

	@RequestMapping(params = { "find=ByNameEquals", "form" }, method = RequestMethod.GET)
    public String findDoctorsByNameEqualsForm(Model uiModel) {
        return "doctors/findDoctorsByNameEquals";
    }

	@RequestMapping(params = "find=ByNameEquals", method = RequestMethod.GET)
    public String findDoctorsByNameEquals(@RequestParam("name") String name, Model uiModel) {
        uiModel.addAttribute("doctors", Doctor.findDoctorsByNameEquals(name).getResultList());
        return "doctors/list";
    }
}
