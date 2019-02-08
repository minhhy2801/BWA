package capstone.bwa.demo.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Report", schema = "dbo", catalog = "BikeWorldDB")
public class ReportEntity {
    private int id;
    private Integer creatorId;
    private String createdTime;
    private String reason;
    private Integer accusedId;
    private String status;
    private AccountEntity accountByCreatorId;
    private AccountEntity accountByAccusedId;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "creatorId")
    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    @Basic
    @Column(name = "createdTime")
    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    @Basic
    @Column(name = "reason")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Basic
    @Column(name = "accusedId")
    public Integer getAccusedId() {
        return accusedId;
    }

    public void setAccusedId(Integer accusedId) {
        this.accusedId = accusedId;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportEntity that = (ReportEntity) o;
        return id == that.id &&
                Objects.equals(creatorId, that.creatorId) &&
                Objects.equals(createdTime, that.createdTime) &&
                Objects.equals(reason, that.reason) &&
                Objects.equals(accusedId, that.accusedId) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creatorId, createdTime, reason, accusedId, status);
    }

    @ManyToOne
    @JoinColumn(name = "creatorId", referencedColumnName = "id",insertable = false,updatable = false)
    public AccountEntity getAccountByCreatorId() {
        return accountByCreatorId;
    }

    public void setAccountByCreatorId(AccountEntity accountByCreatorId) {
        this.accountByCreatorId = accountByCreatorId;
    }

    @ManyToOne
    @JoinColumn(name = "accusedId", referencedColumnName = "id",insertable = false,updatable = false)
    public AccountEntity getAccountByAccusedId() {
        return accountByAccusedId;
    }

    public void setAccountByAccusedId(AccountEntity acco44untByAccusedId) {
        this.accountByAccusedId = accountByAccusedId;
    }
}
