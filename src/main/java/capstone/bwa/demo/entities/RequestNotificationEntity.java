package capstone.bwa.demo.entities;

import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "RequestNotification", schema = "dbo", catalog = "BikeWorldDB")
public class RequestNotificationEntity {
    @JsonView({View.INotification.class})
    private int id;
    @JsonView({View.INotification.class})
    private Integer supplyProductId;

    @JsonView({View.INotification.class})
    private Integer requestProductId;

    @JsonView({View.INotification.class})
    private String description;

    @JsonView({View.INotification.class})
    private String status;

    @JsonView({View.INotification.class})
    private Integer categoryId;

    @JsonView({View.INotification.class})

    private SupplyProductEntity supplyProductBySupplyProductId;

    @JsonView({View.INotification.class})
    private RequestProductEntity requestProductByRequestProductId;

    private CategoryEntity categoryByCategoryId;

    private String type;

    private Integer transactionId;

    private TransactionDetailEntity transactionEntityByTransactionId;


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
    @Column(name = "supplyProductId")
    public Integer getSupplyProductId() {
        return supplyProductId;
    }

    public void setSupplyProductId(Integer supplyProductId) {
        this.supplyProductId = supplyProductId;
    }

    @Basic
    @Column(name = "requestProductId")
    public Integer getRequestProductId() {
        return requestProductId;
    }

    public void setRequestProductId(Integer requestProductId) {
        this.requestProductId = requestProductId;
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
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "categoryId")
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Basic
    @Column(name = "transactionId")
    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    @Basic
    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestNotificationEntity that = (RequestNotificationEntity) o;
        return id == that.id &&
                Objects.equals(supplyProductId, that.supplyProductId) &&
                Objects.equals(requestProductId, that.requestProductId) &&
                Objects.equals(description, that.description) &&
                Objects.equals(status, that.status) &&
                Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(transactionId, that.transactionId) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, supplyProductId, requestProductId, description, status, categoryId, type, transactionId);
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
    @JoinColumn(name = "requestProductId", referencedColumnName = "id", insertable = false, updatable = false)
    public RequestProductEntity getRequestProductByRequestProductId() {
        return requestProductByRequestProductId;
    }

    public void setRequestProductByRequestProductId(RequestProductEntity requestProductByRequestProductId) {
        this.requestProductByRequestProductId = requestProductByRequestProductId;
    }

    @ManyToOne
    @JoinColumn(name = "categoryId", referencedColumnName = "id", insertable = false, updatable = false)
    public CategoryEntity getCategoryByCategoryId() {
        return categoryByCategoryId;
    }

    public void setCategoryByCategoryId(CategoryEntity categoryByCategoryId) {
        this.categoryByCategoryId = categoryByCategoryId;
    }

    @ManyToOne
    @JoinColumn(name = "transactionId", referencedColumnName = "id", insertable = false, updatable = false)
    public TransactionDetailEntity getTransactionByTransactionId() {
        return transactionEntityByTransactionId;
    }

    public void setTransactionByTransactionId(TransactionDetailEntity transactionDetailEntity) {
        this.transactionEntityByTransactionId = transactionDetailEntity;
    }
}
