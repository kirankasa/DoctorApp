package com.xtreme.doctorapp.domain;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Configurable
@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJson
public class Specialist {

    @NotNull
    private String name;

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public static Specialist fromJsonToSpecialist(String json) {
        return new JSONDeserializer<Specialist>().use(null, Specialist.class).deserialize(json);
    }

	public static String toJsonArray(Collection<Specialist> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static Collection<Specialist> fromJsonArrayToSpecialists(String json) {
        return new JSONDeserializer<List<Specialist>>().use(null, ArrayList.class).use("values", Specialist.class).deserialize(json);
    }

	@PersistenceContext
    transient EntityManager entityManager;

	public static final EntityManager entityManager() {
        EntityManager em = new Specialist().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long countSpecialists() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Specialist o", Long.class).getSingleResult();
    }

	public static List<Specialist> findAllSpecialists() {
        return entityManager().createQuery("SELECT o FROM Specialist o", Specialist.class).getResultList();
    }

	public static Specialist findSpecialist(Long id) {
        if (id == null) return null;
        return entityManager().find(Specialist.class, id);
    }

	public static List<Specialist> findSpecialistEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Specialist o", Specialist.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            Specialist attached = Specialist.findSpecialist(this.id);
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
    public Specialist merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Specialist merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
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

	public String getName() {
        return this.name;
    }

	public void setName(String name) {
        this.name = name;
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
