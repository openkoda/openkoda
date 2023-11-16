package timelog.model;

import com.openkoda.model.common.OpenkodaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Formula;

@Entity
public class Project extends OpenkodaEntity {

    public Project(Long organizationId) {
        super(organizationId);
    }

    public Project() {
        super(null);
    }

    private String name;

    @ManyToOne
    @JoinColumn(name = "account_id", updatable = false, insertable = false)
    private Account account;

    @Column(name = "account_id")
    private Long accountId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Formula("( NULL )")
    protected String requiredReadPrivilege;
    @Formula("( NULL )")
    protected String requiredWritePrivilege;
}
