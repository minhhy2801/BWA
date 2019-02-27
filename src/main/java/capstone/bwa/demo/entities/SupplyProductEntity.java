package capstone.bwa.demo.entities;

import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "SupplyProduct", schema = "dbo", catalog = "BikeWorldDB")
public class SupplyProductEntity {
    @JsonView(View.ISupplyPosts.class)
    private int id;
    @JsonView(View.ISupplyPosts.class)
    private String title;
    private String description;
    private Integer creatorId;
    private Integer approvedId;
    @JsonView(View.ISupplyPosts.class)
    private String imgThumbnailUrl;
    @JsonView(View.ISupplyPosts.class)
    private String createdTime;
    private String approvedTime;
    private String closedTime;
    private String location;
    private String rate;
    private Integer categoryId;
    private Integer itemId;
    @JsonView(View.ISupplyPosts.class)
    private String status;
    private String typeItem;
    private Collection<ImageEntity> imagesById;
    private Collection<RequestNotificationEntity> requestNotificationsById;
    @JsonView(View.ISupplyPosts.class)
    private AccountEntity accountByCreatorId;
    private AccountEntity accountByApprovedId;
    @JsonView(View.ISupplyPosts.class)
    private CategoryEntity categoryByCategoryId;
    private Collection<TransactionDetailEntity> transactionDetailsById;

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
    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
    @Column(name = "creatorId")
    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    @Basic
    @Column(name = "approvedId")
    public Integer getApprovedId() {
        return approvedId;
    }

    public void setApprovedId(Integer approvedId) {
        this.approvedId = approvedId;
    }

    @Basic
    @Column(name = "imgThumbnailUrl")
    public String getImgThumbnailUrl() {
        return imgThumbnailUrl;
    }

    public void setImgThumbnailUrl(String imgThumbnailUrl) {
        this.imgThumbnailUrl = imgThumbnailUrl;
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
    @Column(name = "approvedTime")
    public String getApprovedTime() {
        return approvedTime;
    }

    public void setApprovedTime(String approvedTime) {
        this.approvedTime = approvedTime;
    }

    @Basic
    @Column(name = "closedTime")
    public String getClosedTime() {
        return closedTime;
    }

    public void setClosedTime(String closedTime) {
        this.closedTime = closedTime;
    }

    @Basic
    @Column(name = "location")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
    @Column(name = "categoryId")
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Basic
    @Column(name = "itemId")
    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
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
    @Column(name = "typeItem")
    public String getTypeItem() {
        return typeItem;
    }

    public void setTypeItem(String priority) {
        this.typeItem = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplyProductEntity that = (SupplyProductEntity) o;
        return id == that.id &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(creatorId, that.creatorId) &&
                Objects.equals(approvedId, that.approvedId) &&
                Objects.equals(imgThumbnailUrl, that.imgThumbnailUrl) &&
                Objects.equals(createdTime, that.createdTime) &&
                Objects.equals(approvedTime, that.approvedTime) &&
                Objects.equals(closedTime, that.closedTime) &&
                Objects.equals(location, that.location) &&
                Objects.equals(rate, that.rate) &&
                Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(itemId, that.itemId) &&
                Objects.equals(status, that.status) &&
                Objects.equals(typeItem, that.typeItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, creatorId, approvedId, imgThumbnailUrl, createdTime, approvedTime, closedTime, location, rate, categoryId, itemId, status, typeItem);
    }

    @OneToMany(mappedBy = "supplyProductByOwnId")
    public Collection<ImageEntity> getImagesById() {
        return imagesById;
    }

    public void setImagesById(Collection<ImageEntity> imagesById) {
        this.imagesById = imagesById;
    }

    @OneToMany(mappedBy = "supplyProductBySupplyProductId")
    public Collection<RequestNotificationEntity> getRequestNotificationsById() {
        return requestNotificationsById;
    }

    public void setRequestNotificationsById(Collection<RequestNotificationEntity> requestNotificationsById) {
        this.requestNotificationsById = requestNotificationsById;
    }

    @ManyToOne
    @JoinColumn(name = "creatorId", referencedColumnName = "id", insertable = false, updatable = false)
    public AccountEntity getAccountByCreatorId() {
        return accountByCreatorId;
    }

    public void setAccountByCreatorId(AccountEntity accountByCreatorId) {
        this.accountByCreatorId = accountByCreatorId;
    }

    @ManyToOne
    @JoinColumn(name = "approvedId", referencedColumnName = "id", insertable = false, updatable = false)
    public AccountEntity getAccountByApprovedId() {
        return accountByApprovedId;
    }

    public void setAccountByApprovedId(AccountEntity accountByApprovedId) {
        this.accountByApprovedId = accountByApprovedId;
    }

    @ManyToOne
    @JoinColumn(name = "categoryId", referencedColumnName = "id", insertable = false, updatable = false)
    public CategoryEntity getCategoryByCategoryId() {
        return categoryByCategoryId;
    }

    public void setCategoryByCategoryId(CategoryEntity categoryByCategoryId) {
        this.categoryByCategoryId = categoryByCategoryId;
    }

    @OneToMany(mappedBy = "supplyProductBySupplyProductId")
    public Collection<TransactionDetailEntity> getTransactionDetailsById() {
        return transactionDetailsById;
    }

    public void setTransactionDetailsById(Collection<TransactionDetailEntity> transactionDetailsById) {
        this.transactionDetailsById = transactionDetailsById;
    }
}
