package com.xtreme.doctorapp.domain;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
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
@RooJpaActiveRecord(finders = { "findPatientLogsByDoctor", "findPatientLogsByAppointmentDateEqualsAndDoctor" })
public class PatientLog {

    @ManyToOne
    private Doctor doctor;

    private String status;

    private String email;

    @Autowired
    private transient MailSender mailTemplate;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date appointmentDate;

    @NotNull
    private String name;

    private String appointmentTime;

    public void sendMessage(String mailFrom, String subject, String mailTo, String message) {
        org.springframework.mail.SimpleMailMessage mailMessage = new org.springframework.mail.SimpleMailMessage();
        mailMessage.setFrom(mailFrom);
        mailMessage.setSubject(subject);
        mailMessage.setTo(mailTo);
        mailMessage.setText(message);
        mailTemplate.send(mailMessage);
    }

    public static TypedQuery<com.xtreme.doctorapp.domain.PatientLog> findPatientLogsByDoctorName(String doctorName) {
        if (doctorName == null) throw new IllegalArgumentException("The doctor argument is required");
        EntityManager em = PatientLog.entityManager();
        TypedQuery<PatientLog> q = em.createQuery("SELECT o FROM PatientLog AS o WHERE o.doctor.name = :doctorName", PatientLog.class);
        q.setParameter("doctorName", doctorName);
        return q;
    }

    public static TypedQuery<com.xtreme.doctorapp.domain.PatientLog> findPatientLogsByDoctorNameAndStatus(String doctorName) {
        if (doctorName == null) throw new IllegalArgumentException("The doctor argument is required");
        EntityManager em = PatientLog.entityManager();
        TypedQuery<PatientLog> q = em.createQuery("SELECT o FROM PatientLog AS o WHERE o.doctor.name = :doctorName and o.status= :status", PatientLog.class);
        q.setParameter("doctorName", doctorName);
        q.setParameter("status", "N");
        return q;
    }

    public static TypedQuery<com.xtreme.doctorapp.domain.PatientLog> findAllPatientLogsByStatus() {
        EntityManager em = PatientLog.entityManager();
        TypedQuery<PatientLog> q = em.createQuery("SELECT o FROM PatientLog AS o WHERE  o.status= :status", PatientLog.class);
        q.setParameter("status", "N");
        return q;
    }

    public static List<com.xtreme.doctorapp.domain.PatientLog> findPatientLogEntriesByDoctorName(int firstResult, int maxResults, String doctorName) {
        return entityManager().createQuery("SELECT o FROM PatientLog o WHERE o.doctor.name = :doctorName", PatientLog.class).setParameter("doctorName", doctorName).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@PersistenceContext
    transient EntityManager entityManager;

	public static final EntityManager entityManager() {
        EntityManager em = new PatientLog().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long countPatientLogs() {
        return entityManager().createQuery("SELECT COUNT(o) FROM PatientLog o", Long.class).getSingleResult();
    }

	public static List<PatientLog> findAllPatientLogs() {
        return entityManager().createQuery("SELECT o FROM PatientLog o", PatientLog.class).getResultList();
    }

	public static PatientLog findPatientLog(Long id) {
        if (id == null) return null;
        return entityManager().find(PatientLog.class, id);
    }

	public static List<PatientLog> findPatientLogEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM PatientLog o", PatientLog.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            PatientLog attached = PatientLog.findPatientLog(this.id);
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
    public PatientLog merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        PatientLog merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	public Doctor getDoctor() {
        return this.doctor;
    }

	public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

	public String getStatus() {
        return this.status;
    }

	public void setStatus(String status) {
        this.status = status;
    }

	public String getEmail() {
        return this.email;
    }

	public void setEmail(String email) {
        this.email = email;
    }

	public Date getAppointmentDate() {
        return this.appointmentDate;
    }

	public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

	public String getName() {
        return this.name;
    }

	public void setName(String name) {
        this.name = name;
    }

	public String getAppointmentTime() {
        return this.appointmentTime;
    }

	public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
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

	public static TypedQuery<PatientLog> findPatientLogsByAppointmentDateEqualsAndDoctor(Date appointmentDate, Doctor doctor) {
        if (appointmentDate == null) throw new IllegalArgumentException("The appointmentDate argument is required");
        if (doctor == null) throw new IllegalArgumentException("The doctor argument is required");
        EntityManager em = PatientLog.entityManager();
        TypedQuery<PatientLog> q = em.createQuery("SELECT o FROM PatientLog AS o WHERE o.appointmentDate = :appointmentDate  AND o.doctor = :doctor", PatientLog.class);
        q.setParameter("appointmentDate", appointmentDate);
        q.setParameter("doctor", doctor);
        return q;
    }

	public static TypedQuery<PatientLog> findPatientLogsByDoctor(Doctor doctor) {
        if (doctor == null) throw new IllegalArgumentException("The doctor argument is required");
        EntityManager em = PatientLog.entityManager();
        TypedQuery<PatientLog> q = em.createQuery("SELECT o FROM PatientLog AS o WHERE o.doctor = :doctor", PatientLog.class);
        q.setParameter("doctor", doctor);
        return q;
    }

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public static PatientLog fromJsonToPatientLog(String json) {
        return new JSONDeserializer<PatientLog>().use(null, PatientLog.class).deserialize(json);
    }

	public static String toJsonArray(Collection<PatientLog> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static Collection<PatientLog> fromJsonArrayToPatientLogs(String json) {
        return new JSONDeserializer<List<PatientLog>>().use(null, ArrayList.class).use("values", PatientLog.class).deserialize(json);
    }
}
