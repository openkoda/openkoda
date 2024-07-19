package com.openkoda.model;

import com.openkoda.model.common.AuditableEntity;
import com.openkoda.model.common.ModelConstants;
import com.openkoda.model.common.TimestampedEntity;
import jakarta.persistence.*;

@Entity
public class DbVersion extends TimestampedEntity implements AuditableEntity, Comparable<DbVersion> {

    private static final long serialVersionUID = -5528831881473946144L;

    @Id
    @SequenceGenerator(name = GLOBAL_ID_GENERATOR, sequenceName = GLOBAL_ID_GENERATOR, initialValue = ModelConstants.INITIAL_GLOBAL_VALUE, allocationSize = 10)
    @GeneratedValue(generator = ModelConstants.GLOBAL_ID_GENERATOR, strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Column(nullable = false, updatable = true, name = "major")
    private Integer major; 
    @Column(nullable = false, updatable = true, name = "minor")
    private Integer minor; 
    @Column(nullable = false, updatable = true, name = "build")
    private Integer build; 
    @Column(nullable = false, updatable = true, name = "revision")
    private Integer revision; 
    
    @Column(nullable = true, updatable = true, name = "done")
    private Boolean done;
    
    @Column(nullable = true, updatable = true, name = "note")
    private String note;
    
    @Transient
    private boolean runOnInit = false;
    
    public DbVersion(Integer major, Integer minor, Integer build, Integer revision) {
        super();
        this.major = major;
        this.minor = minor;
        this.build = build;
        this.revision = revision;
    }

    public DbVersion() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toAuditString() {
        // TODO Auto-generated method stub
        return null;
    }

    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public Integer getMinor() {
        return minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }

    public Integer getBuild() {
        return build;
    }

    public void setBuild(Integer build) {
        this.build = build;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
    public boolean isRunOnInit() {
        return runOnInit;
    }

    public void setRunOnInit(boolean runOnInit) {
        this.runOnInit = runOnInit;
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d.%d", major, minor, build, revision);
    }
    
    private int value() {
        return this.major * 10000000 + this.minor * 100000 + this.build * 100 + this.revision;
    }
    
    @Override
    public int hashCode() {
        return value();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj != null && ((DbVersion)obj).major == this.major && 
               ((DbVersion)obj).minor == this.minor &&
               ((DbVersion)obj).build == this.build &&
               ((DbVersion)obj).revision == this.revision &&
               ((DbVersion)obj).done == this.done;
    }

    @Override
    public int compareTo(DbVersion obj) {
        return Integer.compare(this.value(), obj.value());
    }
}
