package capstone.bwa.demo.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Feedback", schema = "dbo", catalog = "BikeWorldDB")
public class FeedbackEntity {
    private int id;
    private Integer ownId;
    private String createdTime;
    private String description;
    private String rate;
    private String status;
    private EventRegisteredEntity eventRegisteredByOwnId;
    private TransactionDetailEntity transactionDetailByOwnId;

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
    @Column(name = "ownId")
    public Integer getOwnId() {
        return ownId;
    }

    public void setOwnId(Integer ownId) {
        this.ownId = ownId;
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
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "rate")
    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
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
        FeedbackEntity that = (FeedbackEntity) o;
        return id == that.id &&
                Objects.equals(ownId, that.ownId) &&
                Objects.equals(createdTime, that.createdTime) &&
                Objects.equals(description, that.description) &&
                Objects.equals(rate, that.rate) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ownId, createdTime, description, rate, status);
    }

    @ManyToOne
    @JoinColumn(name = "ownId", referencedColumnName = "id", insertable = false, updatable = false)
    public EventRegisteredEntity getEventRegisteredByOwnId() {
        return eventRegisteredByOwnId;
    }

    public void setEventRegisteredByOwnId(EventRegisteredEntity eventRegisteredByOwnId) {
        this.eventRegisteredByOwnId = eventRegisteredByOwnId;
    }

    @ManyToOne
    @JoinColumn(name = "ownId", referencedColumnName = "id", insertable = false, updatable = false)
    public TransactionDetailEntity getTransactionDetailByOwnId() {
        return transactionDetailByOwnId;
    }

    public void setTransactionDetailByOwnId(TransactionDetailEntity transactionDetailByOwnId) {
        this.transactionDetailByOwnId = transactionDetailByOwnId;
    }
}
