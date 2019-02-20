package capstone.bwa.demo.entities;

import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "Account", schema = "dbo", catalog = "BikeWorldDB")
public class AccountEntity {
    @JsonView(View.IAccountProfile.class)
    private int id;
    @JsonView({View.IAccountProfile.class, View.IEventDetail.class, View.IEventRegistered.class})
    private String name;
    @JsonView(View.IAccountProfile.class)
    private String phone;
    private String password;
    @JsonView(View.IAccountProfile.class)
    private String gender;
    @JsonView(View.IAccountProfile.class)
    private String address;
    @JsonView(View.IAccountProfile.class)
    private String avatarUrl;
    private Integer roleId;
    @JsonView(View.IAccountProfile.class)
    private String rate;
    private String status;
    private String createdTime;
    private String editedTime;

    private RoleEntity roleByRoleId;
    private Collection<CommentEntity> commentsById;
    private Collection<EventEntity> eventsById;
    private Collection<EventEntity> eventsById_0;
    private Collection<EventRegisteredEntity> eventRegisteredsById;
    private Collection<ImageEntity> imagesById;
    private Collection<NewsEntity> newsById;
    private Collection<NewsEntity> newsById_0;
    private Collection<ReportEntity> reportsById;
    private Collection<ReportEntity> reportsById_0;
    private Collection<RequestProductEntity> requestProductsById;
    private Collection<SupplyProductEntity> supplyProductsById;
    private Collection<SupplyProductEntity> supplyProductsById_0;
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
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "gender")
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Basic
    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Basic
    @Column(name = "avatarUrl")
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Basic
    @Column(name = "roleId")
    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountEntity that = (AccountEntity) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(password, that.password) &&
                Objects.equals(createdTime, that.createdTime) &&
                Objects.equals(editedTime, that.editedTime) &&
                Objects.equals(gender, that.gender) &&
                Objects.equals(address, that.address) &&
                Objects.equals(avatarUrl, that.avatarUrl) &&
                Objects.equals(roleId, that.roleId) &&
                Objects.equals(rate, that.rate) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, phone, password, createdTime, editedTime, gender, address, avatarUrl, roleId, rate, status);
    }

    @ManyToOne
    @JoinColumn(name = "roleId", referencedColumnName = "id", insertable = false, updatable = false)
    public RoleEntity getRoleByRoleId() {
        return roleByRoleId;
    }

    public void setRoleByRoleId(RoleEntity roleByRoleId) {
        this.roleByRoleId = roleByRoleId;
    }

    @OneToMany(mappedBy = "accountByEditorId")
    public Collection<CommentEntity> getCommentsById() {
        return commentsById;
    }

    public void setCommentsById(Collection<CommentEntity> commentsById) {
        this.commentsById = commentsById;
    }

    @OneToMany(mappedBy = "accountByCreatorId")
    public Collection<EventEntity> getEventsById() {
        return eventsById;
    }

    public void setEventsById(Collection<EventEntity> eventsById) {
        this.eventsById = eventsById;
    }

    @OneToMany(mappedBy = "accountByApprovedId")
    public Collection<EventEntity> getEventsById_0() {
        return eventsById_0;
    }

    public void setEventsById_0(Collection<EventEntity> eventsById_0) {
        this.eventsById_0 = eventsById_0;
    }

    @OneToMany(mappedBy = "accountByRegisteredId")
    public Collection<EventRegisteredEntity> getEventRegisteredsById() {
        return eventRegisteredsById;
    }

    public void setEventRegisteredsById(Collection<EventRegisteredEntity> eventRegisteredsById) {
        this.eventRegisteredsById = eventRegisteredsById;
    }

    @OneToMany(mappedBy = "accountByOwnId")
    public Collection<ImageEntity> getImagesById() {
        return imagesById;
    }

    public void setImagesById(Collection<ImageEntity> imagesById) {
        this.imagesById = imagesById;
    }

    @OneToMany(mappedBy = "accountByCreatorId")
    public Collection<NewsEntity> getNewsById() {
        return newsById;
    }

    public void setNewsById(Collection<NewsEntity> newsById) {
        this.newsById = newsById;
    }

    @OneToMany(mappedBy = "accountByEditorId")
    public Collection<NewsEntity> getNewsById_0() {
        return newsById_0;
    }

    public void setNewsById_0(Collection<NewsEntity> newsById_0) {
        this.newsById_0 = newsById_0;
    }

    @OneToMany(mappedBy = "accountByCreatorId")
    public Collection<ReportEntity> getReportsById() {
        return reportsById;
    }

    public void setReportsById(Collection<ReportEntity> reportsById) {
        this.reportsById = reportsById;
    }

    @OneToMany(mappedBy = "accountByAccusedId")
    public Collection<ReportEntity> getReportsById_0() {
        return reportsById_0;
    }

    public void setReportsById_0(Collection<ReportEntity> reportsById_0) {
        this.reportsById_0 = reportsById_0;
    }

    @OneToMany(mappedBy = "accountByCreatorId")
    public Collection<RequestProductEntity> getRequestProductsById() {
        return requestProductsById;
    }

    public void setRequestProductsById(Collection<RequestProductEntity> requestProductsById) {
        this.requestProductsById = requestProductsById;
    }

    @OneToMany(mappedBy = "accountByCreatorId")
    public Collection<SupplyProductEntity> getSupplyProductsById() {
        return supplyProductsById;
    }

    public void setSupplyProductsById(Collection<SupplyProductEntity> supplyProductsById) {
        this.supplyProductsById = supplyProductsById;
    }

    @OneToMany(mappedBy = "accountByApprovedId")
    public Collection<SupplyProductEntity> getSupplyProductsById_0() {
        return supplyProductsById_0;
    }

    public void setSupplyProductsById_0(Collection<SupplyProductEntity> supplyProductsById_0) {
        this.supplyProductsById_0 = supplyProductsById_0;
    }

    @OneToMany(mappedBy = "accountByInteractiveId")
    public Collection<TransactionDetailEntity> getTransactionDetailsById() {
        return transactionDetailsById;
    }

    public void setTransactionDetailsById(Collection<TransactionDetailEntity> transactionDetailsById) {
        this.transactionDetailsById = transactionDetailsById;
    }
}
