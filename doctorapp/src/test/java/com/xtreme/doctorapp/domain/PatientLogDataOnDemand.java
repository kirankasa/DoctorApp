package com.xtreme.doctorapp.domain;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dod.RooDataOnDemand;
import org.springframework.stereotype.Component;

@Configurable
@Component
@RooDataOnDemand(entity = PatientLog.class)
public class PatientLogDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<PatientLog> data;

	@Autowired
    private DoctorDataOnDemand doctorDataOnDemand;

	public PatientLog getNewTransientPatientLog(int index) {
        PatientLog obj = new PatientLog();
        setAppointmentDate(obj, index);
        setAppointmentTime(obj, index);
        setEmail(obj, index);
        setName(obj, index);
        setStatus(obj, index);
        return obj;
    }

	public void setAppointmentDate(PatientLog obj, int index) {
        Date appointmentDate = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setAppointmentDate(appointmentDate);
    }

	public void setAppointmentTime(PatientLog obj, int index) {
        String appointmentTime = "appointmentTime_" + index;
        obj.setAppointmentTime(appointmentTime);
    }

	public void setEmail(PatientLog obj, int index) {
        String email = "foo" + index + "@bar.com";
        obj.setEmail(email);
    }

	public void setName(PatientLog obj, int index) {
        String name = "name_" + index;
        obj.setName(name);
    }

	public void setStatus(PatientLog obj, int index) {
        String status = "status_" + index;
        obj.setStatus(status);
    }

	public PatientLog getSpecificPatientLog(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        PatientLog obj = data.get(index);
        Long id = obj.getId();
        return PatientLog.findPatientLog(id);
    }

	public PatientLog getRandomPatientLog() {
        init();
        PatientLog obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return PatientLog.findPatientLog(id);
    }

	public boolean modifyPatientLog(PatientLog obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = PatientLog.findPatientLogEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'PatientLog' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<PatientLog>();
        for (int i = 0; i < 10; i++) {
            PatientLog obj = getNewTransientPatientLog(i);
            try {
                obj.persist();
            } catch (ConstraintViolationException e) {
                StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getConstraintDescriptor()).append(":").append(cv.getMessage()).append("=").append(cv.getInvalidValue()).append("]");
                }
                throw new RuntimeException(msg.toString(), e);
            }
            obj.flush();
            data.add(obj);
        }
    }
}
