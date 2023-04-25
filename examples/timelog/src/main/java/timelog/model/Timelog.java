package timelog.model;

import com.openkoda.model.common.OpenkodaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Formula;

import java.time.LocalDate;

@Entity
public class Timelog extends OpenkodaEntity {
    public Timelog(Long organizationId) {
        super(organizationId);
    }

    public Timelog() {
        super(null);
    }

    private String description;

    private Integer duration; //in seconds

    private LocalDate startedOn;

    @ManyToOne
    @JoinColumn(name = "assignment_id", insertable = false, updatable = false)
    private Assignment assignment;
    @Column(name = "assignment_id")
    private Long assignmentId;

    @Formula("(null)")
    protected String requiredReadPrivilege;
    @Formula("(null)")
    protected String requiredWritePrivilege;

    @Override
    public String getRequiredReadPrivilege() {
        return requiredReadPrivilege;
    }

    @Override
    public String getRequiredWritePrivilege() {
        return requiredWritePrivilege;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public LocalDate getStartedOn() {
        return startedOn;
    }

    public void setStartedOn(LocalDate startedOn) {
        this.startedOn = startedOn;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }
}
