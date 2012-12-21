package com.xtreme.doctorapp;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xtreme.doctorapp.domain.PatientLog;
import com.xtreme.doctorapp.domain.TimingUtil;
import com.xtreme.doctorapp.service.EmailService;
import com.xtreme.doctorapp.service.Message;

@RequestMapping("/verify")
@Controller
public class VerifyController {

	@Autowired
	private EmailService emailService;

	@RequestMapping(method = RequestMethod.POST, value = "{id}")
	public void post(@PathVariable Long id, ModelMap modelMap,
			HttpServletRequest request, HttpServletResponse response) {
	}

	@RequestMapping(method = RequestMethod.GET, value = "/index")
	public String index(Model uiModel) {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		String name = auth.getName(); // get logged in username

		if ("admin".equalsIgnoreCase(name)) {
			uiModel.addAttribute("patientlogs", PatientLog
					.findAllPatientLogsByStatus().getResultList());
		} else {
			uiModel.addAttribute("patientlogs", PatientLog
					.findPatientLogsByDoctorNameAndStatus(name).getResultList());
		}

		return "verify/index";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/confirm/{id}")
	public String verify(@PathVariable("id") String id, Model uiModel) {

		String[] string = id.split(":");

		for (String patientId : string) {

			PatientLog log = PatientLog.findPatientLog(Long
					.parseLong(patientId));
			log.setStatus("Y");
			log.merge();

			String body = "Dear " + log.getName() + ",\n\n\n";
			Date date = log.getAppointmentDate();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
			String appointmentDate = dateFormat.format(date);

			body = body + "Your appointment has been fixed with "
					+ log.getDoctor().getName() + " at " + appointmentDate
					+ "  "
					+ TimingUtil.getLabelbasedOnId(log.getAppointmentTime());

			Message message = new Message();
			message.setBody(body);
			message.setReceiverEmail(log.getEmail());
			message.setReceiverName("kiran");
			message.setSenderEmail("doctorapp@support.com");
			message.setSenderName("Doctorapp Support Team");
			message.setSubject("Appointment");
			emailService.send(message);
		}

		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		String name = auth.getName(); // get logged in username

		if ("admin".equalsIgnoreCase(name)) {
			uiModel.addAttribute("patientlogs", PatientLog
					.findAllPatientLogsByStatus().getResultList());
		} else {
			uiModel.addAttribute("patientlogs", PatientLog
					.findPatientLogsByDoctorNameAndStatus(name).getResultList());
		}

		return "redirect:/verify/index";
	}
}
