package capstone.bwa.demo.entities;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "Event", schema = "dbo", catalog = "BikeWorldDB")
public class EventEntity {
    private int id;
    private Integer creatorId;
    private Integer approvedId;
    private Integer categoryId;
    private String imgThumbnailUrl;
    private String title;
    private String description;
    private String location;
    private String priceTicket;
    private Integer minTicket;
    private Integer maxTicket;
    private String createdTime;
    private String approvedTime;
    private String startTime;
    private String endTime;
    private String publicTime;
    private String startRegisterTime;
    private String endRegisterTime;
    private Integer totalSoldTicket;
    private Integer totalFeedback;
    private String totalRate;
    private String status;
    private AccountEntity accountByCreatorId;
    private AccountEntity accountByApprovedId;
    private CategoryEntity categoryByCategoryId;
    private Collection<EventRegisteredEntity> eventRegisteredsById;
    private Collection<ImageEntity> imagesById;

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
    @Column(name = "approvedId")
    public Integer getApprovedId() {
        return approvedId;
    }

    public void setApprovedId(Integer approvedId) {
        this.approvedId = approvedId;
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
    @Column(name = "imgThumbnailUrl")
    public String getImgThumbnailUrl() {
        return imgThumbnailUrl;
    }

    public void setImgThumbnailUrl(String imgThumbnailUrl) {
        this.imgThumbnailUrl = imgThumbnailUrl;
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
    @Column(name = "location")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Basic
    @Column(name = "priceTicket")
    public String getPriceTicket() {
        return priceTicket;
    }

    public void setPriceTicket(String priceTicket) {
        this.priceTicket = priceTicket;
    }

    @Basic
    @Column(name = "minTicket")
    public Integer getMinTicket() {
        return minTicket;
    }

    public void setMinTicket(Integer minTicket) {
        this.minTicket = minTicket;
    }

    @Basic
    @Column(name = "maxTicket")
    public Integer getMaxTicket() {
        return maxTicket;
    }

    public void setMaxTicket(Integer maxTicket) {
        this.maxTicket = maxTicket;
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
    @Column(name = "startTime")
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Basic
    @Column(name = "endTime")
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Basic
    @Column(name = "publicTime")
    public String getPublicTime() {
        return publicTime;
    }

    public void setPublicTime(String publicTime) {
        this.publicTime = publicTime;
    }

    @Basic
    @Column(name = "startRegisterTime")
    public String getStartRegisterTime() {
        return startRegisterTime;
    }

    public void setStartRegisterTime(String startRegisterTime) {
        this.startRegisterTime = startRegisterTime;
    }

    @Basic
    @Column(name = "endRegisterTime")
    public String getEndRegisterTime() {
        return endRegisterTime;
    }

    public void setEndRegisterTime(String endRegisterTime) {
        this.endRegisterTime = endRegisterTime;
    }

    @Basic
    @Column(name = "totalSoldTicket")
    public Integer getTotalSoldTicket() {
        return totalSoldTicket;
    }

    public void setTotalSoldTicket(Integer totalSoldTicket) {
        this.totalSoldTicket = totalSoldTicket;
    }

    @Basic
    @Column(name = "totalFeedback")
    public Integer getTotalFeedback() {
        return totalFeedback;
    }

    public void setTotalFeedback(Integer totalFeedback) {
        this.totalFeedback = totalFeedback;
    }

    @Basic
    @Column(name = "totalRate")
    public String getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(String totalRate) {
        this.totalRate = totalRate;
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
        EventEntity that = (EventEntity) o;
        return id == that.id &&
                Objects.equals(creatorId, that.creatorId) &&
                Objects.equals(approvedId, that.approvedId) &&
                Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(imgThumbnailUrl, that.imgThumbnailUrl) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(location, that.location) &&
                Objects.equals(priceTicket, that.priceTicket) &&
                Objects.equals(minTicket, that.minTicket) &&
                Objects.equals(maxTicket, that.maxTicket) &&
                Objects.equals(createdTime, that.createdTime) &&
                Objects.equals(approvedTime, that.approvedTime) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(publicTime, that.publicTime) &&
                Objects.equals(startRegisterTime, that.startRegisterTime) &&
                Objects.equals(endRegisterTime, that.endRegisterTime) &&
                Objects.equals(totalSoldTicket, that.totalSoldTicket) &&
                Objects.equals(totalFeedback, that.totalFeedback) &&
                Objects.equals(totalRate, that.totalRate) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creatorId, approvedId, categoryId, imgThumbnailUrl, title, description, location, priceTicket, minTicket, maxTicket, createdTime, approvedTime, startTime, endTime, publicTime, startRegisterTime, endRegisterTime, totalSoldTicket, totalFeedback, totalRate, status);
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
    @JoinColumn(name = "approvedId", referencedColumnName = "id",insertable = false,updatable = false)
    public AccountEntity getAccountByApprovedId() {
        return accountByApprovedId;
    }

    public void setAccountByApprovedId(AccountEntity accountByApprovedId) {
        this.accountByApprovedId = accountByApprovedId;
    }

    @ManyToOne
    @JoinColumn(name = "categoryId", referencedColumnName = "id",insertable = false,updatable = false)
    public CategoryEntity getCategoryByCategoryId() {
        return categoryByCategoryId;
    }

    public void setCategoryByCategoryId(CategoryEntity categoryByCategoryId) {
        this.categoryByCategoryId = categoryByCategoryId;
    }

    @OneToMany(mappedBy = "eventByEventId")
    public Collection<EventRegisteredEntity> getEventRegisteredsById() {
        return eventRegisteredsById;
    }

    public void setEventRegisteredsById(Collection<EventRegisteredEntity> eventRegisteredsById) {
        this.eventRegisteredsById = eventRegisteredsById;
    }

    @OneToMany(mappedBy = "eventByOwnId")
    public Collection<ImageEntity> getImagesById() {
        return imagesById;
    }

    public void setImagesById(Collection<ImageEntity> imagesById) {
        this.imagesById = imagesById;
    }
}
