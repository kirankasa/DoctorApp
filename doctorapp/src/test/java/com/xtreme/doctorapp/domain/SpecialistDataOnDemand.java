package com.xtreme.doctorapp.domain;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dod.RooDataOnDemand;
import org.springframework.stereotype.Component;

@Component
@Configurable
@RooDataOnDemand(entity = Specialist.class)
public class SpecialistDataOnDemand {

	private Random rnd = new SecureRandom();

	private List<Specialist> data;

	public Specialist getNewTransientSpecialist(int index) {
        Specialist obj = new Specialist();
        setName(obj, index);
        return obj;
    }

	public void setName(Specialist obj, int index) {
        String name = "name_" + index;
        obj.setName(name);
    }

	public Specialist getSpecificSpecialist(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        Specialist obj = data.get(index);
        Long id = obj.getId();
        return Specialist.findSpecialist(id);
    }

	public Specialist getRandomSpecialist() {
        init();
        Specialist obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return Specialist.findSpecialist(id);
    }

	public boolean modifySpecialist(Specialist obj) {
        return false;
    }

	public void init() {
        int from = 0;
        int to = 10;
        data = Specialist.findSpecialistEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'Specialist' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<Specialist>();
        for (int i = 0; i < 10; i++) {
            Specialist obj = getNewTransientSpecialist(i);
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
