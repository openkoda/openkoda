package timelog.model;

import com.openkoda.model.PrivilegeNames;
import com.openkoda.model.User;
import com.openkoda.model.common.ModelConstants;
import com.openkoda.model.common.OpenkodaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Formula;

@Entity
public class CreativeWorkStatement extends OpenkodaEntity {

    public CreativeWorkStatement(Long organizationId) { super(organizationId); }
    public CreativeWorkStatement() { super(null); }

    private Integer month;
    private Integer year;
    private String totalTimeSpent;
    private String fullName;

    private String assignments;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User user;

    @Column(name = "user_id")
    private Long userId;

    @Formula("( CASE user_id WHEN " + ModelConstants.USER_ID_PLACEHOLDER + " THEN NULL ELSE '" + PrivilegeNames._readUserData + "' END )")
    protected String requiredReadPrivilege;
    @Formula("( CASE user_id WHEN " + ModelConstants.USER_ID_PLACEHOLDER + " THEN '" + PrivilegeNames._canAccessGlobalSettings + "' ELSE NULL END)")
    protected String requiredWritePrivilege;

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(String totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAssignments() {
        return assignments;
    }

    public void setAssignments(String assignments) {
        this.assignments = assignments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String getRequiredReadPrivilege() {
        return requiredReadPrivilege;
    }

    @Override
    public String getRequiredWritePrivilege() {
        return requiredWritePrivilege;
    }
}
