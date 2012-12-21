package com.xtreme.doctorapp.web;

import com.xtreme.doctorapp.domain.Doctor;
import com.xtreme.doctorapp.domain.PatientLog;
import com.xtreme.doctorapp.domain.Specialist;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.roo.addon.web.mvc.controller.converter.RooConversionService;

@Configurable
/**
 * A central place to register application converters and formatters. 
 */
@RooConversionService
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

	@Override
	protected void installFormatters(FormatterRegistry registry) {
		super.installFormatters(registry);
		// Register application converters and formatters
	}

	public Converter<Doctor, String> getDoctorToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.xtreme.doctorapp.domain.Doctor, java.lang.String>() {
            public String convert(Doctor doctor) {
                return new StringBuilder().append(doctor.getName()).toString();
            }
        };
    }

	public Converter<Long, Doctor> getIdToDoctorConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.xtreme.doctorapp.domain.Doctor>() {
            public com.xtreme.doctorapp.domain.Doctor convert(java.lang.Long id) {
                return Doctor.findDoctor(id);
            }
        };
    }

	public Converter<String, Doctor> getStringToDoctorConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.xtreme.doctorapp.domain.Doctor>() {
            public com.xtreme.doctorapp.domain.Doctor convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Doctor.class);
            }
        };
    }

	public Converter<PatientLog, String> getPatientLogToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.xtreme.doctorapp.domain.PatientLog, java.lang.String>() {
            public String convert(PatientLog patientLog) {
                return new StringBuilder().append(patientLog.getStatus()).append(' ').append(patientLog.getEmail()).append(' ').append(patientLog.getAppointmentDate()).append(' ').append(patientLog.getName()).toString();
            }
        };
    }

	public Converter<Long, PatientLog> getIdToPatientLogConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.xtreme.doctorapp.domain.PatientLog>() {
            public com.xtreme.doctorapp.domain.PatientLog convert(java.lang.Long id) {
                return PatientLog.findPatientLog(id);
            }
        };
    }

	public Converter<String, PatientLog> getStringToPatientLogConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.xtreme.doctorapp.domain.PatientLog>() {
            public com.xtreme.doctorapp.domain.PatientLog convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), PatientLog.class);
            }
        };
    }

	public Converter<Specialist, String> getSpecialistToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.xtreme.doctorapp.domain.Specialist, java.lang.String>() {
            public String convert(Specialist specialist) {
                return new StringBuilder().append(specialist.getName()).toString();
            }
        };
    }

	public Converter<Long, Specialist> getIdToSpecialistConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.xtreme.doctorapp.domain.Specialist>() {
            public com.xtreme.doctorapp.domain.Specialist convert(java.lang.Long id) {
                return Specialist.findSpecialist(id);
            }
        };
    }

	public Converter<String, Specialist> getStringToSpecialistConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.xtreme.doctorapp.domain.Specialist>() {
            public com.xtreme.doctorapp.domain.Specialist convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Specialist.class);
            }
        };
    }

	public void installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(getDoctorToStringConverter());
        registry.addConverter(getIdToDoctorConverter());
        registry.addConverter(getStringToDoctorConverter());
        registry.addConverter(getPatientLogToStringConverter());
        registry.addConverter(getIdToPatientLogConverter());
        registry.addConverter(getStringToPatientLogConverter());
        registry.addConverter(getSpecialistToStringConverter());
        registry.addConverter(getIdToSpecialistConverter());
        registry.addConverter(getStringToSpecialistConverter());
    }

	public void afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
}
