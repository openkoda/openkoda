package timelog.model;

import com.openkoda.model.PrivilegeNames;
import com.openkoda.model.common.ModelConstants;
import com.openkoda.model.common.OpenkodaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Formula;
import timelog.repository.TimelogRepository;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private LocalDate startedOn = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "assignment_id", insertable = false, updatable = false)
    private Assignment assignment;
    @Column(name = "assignment_id")
    private Long assignmentId;

    @Formula("( CASE (select aaa.user_id from assignment aaa where aaa.id = assignment_id) WHEN " + ModelConstants.USER_ID_PLACEHOLDER + " THEN NULL ELSE '" + PrivilegeNames._canAccessGlobalSettings + "' END )")
    protected String requiredReadPrivilege;
    @Formula("(extract('week' from started_on))")
    private Integer weekOfYear;
    @Formula("(extract('dow' from started_on))")
    private Integer dayOfWeek;
    @Formula("(extract('month' from started_on))")
    private Integer monthOfYear;
    @Formula("(date_trunc('month', started_on))")
    private LocalDate startedOnMonth;

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
    public String getDurationInHoursString() {
        return TimelogRepository.convertToHoursStringStatic(duration);
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

    public Integer getWeekOfYear() {
        return weekOfYear;
    }

    public void setWeekOfYear(Integer weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getMonthOfYear() {
        return monthOfYear;
    }

    public void setMonthOfYear(Integer monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    public LocalDate getStartedOnMonth() {
        return startedOnMonth;
    }

    public void setStartedOnMonth(LocalDate startedOnMonth) {
        this.startedOnMonth = startedOnMonth;
    }
}
