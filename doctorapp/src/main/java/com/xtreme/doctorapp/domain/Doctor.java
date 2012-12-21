package com.xtreme.doctorapp.domain;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.mail.MailSender;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Configurable
@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(finders = { "findDoctorsByNameEquals", "findDoctorsByGenderAndServiceTypeAndSpecialist", "findDoctorsByGenderAndServiceTypeAndSpecialistAndPincode" })
public class Doctor {

    @NotNull
    private String name;

    @NotNull
    private String qualification;

    @NotNull
    private String mobileno;

    @NotNull
    private String email;

    @Enumerated
    private Gender gender;

    @ManyToOne
    private Specialist specialist;

    private String area;

    private String city;

    private String stateName;

    private String pincode;

    @Enumerated
    private ServiceType serviceType;

    @Autowired
    private transient MailSender mailTemplate;

    public void sendMessage(String mailFrom, String subject, String mailTo, String message) {
        org.springframework.mail.SimpleMailMessage mailMessage = new org.springframework.mail.SimpleMailMessage();
        mailMessage.setFrom(mailFrom);
        mailMessage.setSubject(subject);
        mailMessage.setTo(mailTo);
        mailMessage.setText(message);
        mailTemplate.send(mailMessage);
    }

	public static TypedQuery<Doctor> findDoctorsByGenderAndServiceTypeAndSpecialistAndPincode(Gender gender, ServiceType serviceType, Specialist specialist, String pincode) {
        if (gender == null) throw new IllegalArgumentException("The gender argument is required");
        if (serviceType == null) throw new IllegalArgumentException("The serviceType argument is required");
        if (specialist == null) throw new IllegalArgumentException("The specialist argument is required");
        if (pincode == null || pincode.length() == 0) throw new IllegalArgumentException("The pincode argument is required");
        pincode=pincode.substring(0,pincode.length()-1);
        EntityManager em = Doctor.entityManager();
        TypedQuery<Doctor> q = em.createQuery("SELECT o FROM Doctor AS o WHERE o.gender = :gender AND o.serviceType = :serviceType AND o.specialist = :specialist AND o.pincode like '"+pincode+"_'", Doctor.class);
        q.setParameter("gender", gender);
        q.setParameter("serviceType", serviceType);
        q.setParameter("specialist", specialist);
        //q.setParameter("pincode", pincode.substring(0, pincode.length())+"%s");
        return q;
    }

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;

	public Long getId() {
        return this.id;
    }

	public void setId(Long id) {
        this.id = id;
    }

	public Integer getVersion() {
        return this.version;
    }

	public void setVersion(Integer version) {
        this.version = version;
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	@PersistenceContext
    transient EntityManager entityManager;

	public static final EntityManager entityManager() {
        EntityManager em = new Doctor().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long countDoctors() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Doctor o", Long.class).getSingleResult();
    }

	public static List<Doctor> findAllDoctors() {
        return entityManager().createQuery("SELECT o FROM Doctor o", Doctor.class).getResultList();
    }

	public static Doctor findDoctor(Long id) {
        if (id == null) return null;
        return entityManager().find(Doctor.class, id);
    }

	public static List<Doctor> findDoctorEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Doctor o", Doctor.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

	@Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Doctor attached = Doctor.findDoctor(this.id);
            this.entityManager.remove(attached);
        }
    }

	@Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

	@Transactional
    public void clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }

	@Transactional
    public Doctor merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Doctor merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public static Doctor fromJsonToDoctor(String json) {
        return new JSONDeserializer<Doctor>().use(null, Doctor.class).deserialize(json);
    }

	public static String toJsonArray(Collection<Doctor> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static Collection<Doctor> fromJsonArrayToDoctors(String json) {
        return new JSONDeserializer<List<Doctor>>().use(null, ArrayList.class).use("values", Doctor.class).deserialize(json);
    }

	public String getName() {
        return this.name;
    }

	public void setName(String name) {
        this.name = name;
    }

	public String getQualification() {
        return this.qualification;
    }

	public void setQualification(String qualification) {
        this.qualification = qualification;
    }

	public String getMobileno() {
        return this.mobileno;
    }

	public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

	public String getEmail() {
        return this.email;
    }

	public void setEmail(String email) {
        this.email = email;
    }

	public Gender getGender() {
        return this.gender;
    }

	public void setGender(Gender gender) {
        this.gender = gender;
    }

	public Specialist getSpecialist() {
        return this.specialist;
    }

	public void setSpecialist(Specialist specialist) {
        this.specialist = specialist;
    }

	public String getArea() {
        return this.area;
    }

	public void setArea(String area) {
        this.area = area;
    }

	public String getCity() {
        return this.city;
    }

	public void setCity(String city) {
        this.city = city;
    }

	public String getStateName() {
        return this.stateName;
    }

	public void setStateName(String stateName) {
        this.stateName = stateName;
    }

	public String getPincode() {
        return this.pincode;
    }

	public void setPincode(String pincode) {
        this.pincode = pincode;
    }

	public ServiceType getServiceType() {
        return this.serviceType;
    }

	public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

	public static TypedQuery<Doctor> findDoctorsByGenderAndServiceTypeAndSpecialist(Gender gender, ServiceType serviceType, Specialist specialist) {
        if (gender == null) throw new IllegalArgumentException("The gender argument is required");
        if (serviceType == null) throw new IllegalArgumentException("The serviceType argument is required");
        if (specialist == null) throw new IllegalArgumentException("The specialist argument is required");
        EntityManager em = Doctor.entityManager();
        TypedQuery<Doctor> q = em.createQuery("SELECT o FROM Doctor AS o WHERE o.gender = :gender AND o.serviceType = :serviceType AND o.specialist = :specialist", Doctor.class);
        q.setParameter("gender", gender);
        q.setParameter("serviceType", serviceType);
        q.setParameter("specialist", specialist);
        return q;
    }

	public static TypedQuery<Doctor> findDoctorsByNameEquals(String name) {
        if (name == null || name.length() == 0) throw new IllegalArgumentException("The name argument is required");
        EntityManager em = Doctor.entityManager();
        TypedQuery<Doctor> q = em.createQuery("SELECT o FROM Doctor AS o WHERE o.name = :name", Doctor.class);
        q.setParameter("name", name);
        return q;
    }
}
