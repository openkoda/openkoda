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
public class Assignment extends OpenkodaEntity {
    public static final String descriptionFormula = "(select (coalesce(uu.first_name, '')||' '||coalesce(uu.last_name, '')) || ' - ' || tt.name from users uu, ticket tt where uu.id = user_id and tt.id = ticket_id)";
    public Assignment(Long organizationId) {
        super(organizationId);
    }

    public Assignment() {
        super(null);
    }

    private boolean billable;
    private boolean researchAndDevelopment;
    private boolean creativeWork;

    @Formula(descriptionFormula)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User user;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "ticket_id", updatable = false, insertable = false)
    private Ticket ticket;

    @Column(name = "ticket_id")
    private Long ticketId;

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

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    @Formula("( CASE user_id WHEN " + ModelConstants.USER_ID_PLACEHOLDER + " THEN NULL ELSE '" + PrivilegeNames._canAccessGlobalSettings + "' END )")
    protected String requiredReadPrivilege;
    @Formula("( CASE user_id WHEN " + ModelConstants.USER_ID_PLACEHOLDER + " THEN NULL ELSE '" + PrivilegeNames._canAccessGlobalSettings + "' END )")
    protected String requiredWritePrivilege;

    @Override
    public String getRequiredReadPrivilege() {
        return requiredReadPrivilege;
    }

    @Override
    public String getRequiredWritePrivilege() {
        return requiredWritePrivilege;
    }

    public boolean isBillable() {
        return billable;
    }

    public void setBillable(boolean billable) {
        this.billable = billable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isResearchAndDevelopment() {
        return researchAndDevelopment;
    }

    public void setResearchAndDevelopment(boolean researchAndDevelopment) {
        this.researchAndDevelopment = researchAndDevelopment;
    }

    public boolean isCreativeWork() {
        return creativeWork;
    }

    public void setCreativeWork(boolean creativeWork) {
        this.creativeWork = creativeWork;
    }
}
