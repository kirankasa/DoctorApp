package com.xtreme.doctorapp.domain;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dod.RooDataOnDemand;
import org.springframework.stereotype.Component;

@Component
@Configurable
@RooDataOnDemand(entity = Doctor.class)
public class DoctorDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<Doctor> data;

	@Autowired
    private SpecialistDataOnDemand specialistDataOnDemand;

	public Doctor getNewTransientDoctor(int index) {
        Doctor obj = new Doctor();
        setArea(obj, index);
        setCity(obj, index);
        setEmail(obj, index);
        setGender(obj, index);
        setMobileno(obj, index);
        setName(obj, index);
        setPincode(obj, index);
        setQualification(obj, index);
        setServiceType(obj, index);
        setStateName(obj, index);
        return obj;
    }

	public void setArea(Doctor obj, int index) {
        String area = "area_" + index;
        obj.setArea(area);
    }

	public void setCity(Doctor obj, int index) {
        String city = "city_" + index;
        obj.setCity(city);
    }

	public void setEmail(Doctor obj, int index) {
        String email = "foo" + index + "@bar.com";
        obj.setEmail(email);
    }

	public void setGender(Doctor obj, int index) {
        Gender gender = Gender.class.getEnumConstants()[0];
        obj.setGender(gender);
    }

	public void setMobileno(Doctor obj, int index) {
        String mobileno = "mobileno_" + index;
        obj.setMobileno(mobileno);
    }

	public void setName(Doctor obj, int index) {
        String name = "name_" + index;
        obj.setName(name);
    }

	public void setPincode(Doctor obj, int index) {
        String pincode = "pincode_" + index;
        obj.setPincode(pincode);
    }

	public void setQualification(Doctor obj, int index) {
        String qualification = "qualification_" + index;
        obj.setQualification(qualification);
    }

	public void setServiceType(Doctor obj, int index) {
        ServiceType serviceType = ServiceType.class.getEnumConstants()[0];
        obj.setServiceType(serviceType);
    }

	public void setStateName(Doctor obj, int index) {
        String stateName = "stateName_" + index;
        obj.setStateName(stateName);
    }

	public Doctor getSpecificDoctor(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        Doctor obj = data.get(index);
        Long id = obj.getId();
        return Doctor.findDoctor(id);
    }

	public Doctor getRandomDoctor() {
        init();
        Doctor obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return Doctor.findDoctor(id);
    }

	public boolean modifyDoctor(Doctor obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = Doctor.findDoctorEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'Doctor' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<Doctor>();
        for (int i = 0; i < 10; i++) {
            Doctor obj = getNewTransientDoctor(i);
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
