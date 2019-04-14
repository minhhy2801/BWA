package capstone.bwa.demo.entities;

import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "TransactionDetail", schema = "dbo", catalog = "BikeWorldDB")
public class TransactionDetailEntity {
    @JsonView({View.ITransactionDetail.class, View.ITransactions.class})
    private int id;
    @JsonView({View.ITransactionDetail.class, View.ITransactions.class})
    private String createdTime;
    @JsonView(View.ITransactionDetail.class)
    private String editedTime;
    @JsonView({View.ITransactionDetail.class, View.ITransactions.class})
    private String status;
    private Integer supplyProductId;
    private Integer interactiveId;
    private Collection<FeedbackEntity> feedbacksById;
    @JsonView({View.ITransactions.class, View.ITransactionDetail.class})
    private SupplyProductEntity supplyProductBySupplyProductId;
    @JsonView({View.ITransactionDetail.class, View.ITransactions.class, View.IFeedbackSupplyPost.class})
    private AccountEntity accountByInteractiveId;

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
    @Column(name = "createdTime")
    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    @Basic
    @Column(name = "editedTime")
    public String getEditedTime() {
        return editedTime;
    }

    public void setEditedTime(String editedTime) {
        this.editedTime = editedTime;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "supplyProductId")
    public Integer getSupplyProductId() {
        return supplyProductId;
    }

    public void setSupplyProductId(Integer supplyProductId) {
        this.supplyProductId = supplyProductId;
    }

    @Basic
    @Column(name = "interactiveId")
    public Integer getInteractiveId() {
        return interactiveId;
    }

    public void setInteractiveId(Integer interactiveId) {
        this.interactiveId = interactiveId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionDetailEntity that = (TransactionDetailEntity) o;
        return id == that.id &&
                Objects.equals(createdTime, that.createdTime) &&
                Objects.equals(editedTime, that.editedTime) &&
                Objects.equals(status, that.status) &&
                Objects.equals(supplyProductId, that.supplyProductId) &&
                Objects.equals(interactiveId, that.interactiveId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdTime, editedTime, status, supplyProductId, interactiveId);
    }

    @OneToMany(mappedBy = "transactionDetailByOwnId")
    public Collection<FeedbackEntity> getFeedbacksById() {
        return feedbacksById;
    }

    public void setFeedbacksById(Collection<FeedbackEntity> feedbacksById) {
        this.feedbacksById = feedbacksById;
    }

    @ManyToOne
    @JoinColumn(name = "supplyProductId", referencedColumnName = "id", insertable = false, updatable = false)
    public SupplyProductEntity getSupplyProductBySupplyProductId() {
        return supplyProductBySupplyProductId;
    }

    public void setSupplyProductBySupplyProductId(SupplyProductEntity supplyProductBySupplyProductId) {
        this.supplyProductBySupplyProductId = supplyProductBySupplyProductId;
    }

    @ManyToOne
    @JoinColumn(name = "interactiveId", referencedColumnName = "id", insertable = false, updatable = false)
    public AccountEntity getAccountByInteractiveId() {
        return accountByInteractiveId;
    }

    public void setAccountByInteractiveId(AccountEntity accountByInteractiveId) {
        this.accountByInteractiveId = accountByInteractiveId;
    }
}
