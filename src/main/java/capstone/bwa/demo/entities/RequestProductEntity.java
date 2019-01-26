package capstone.bwa.demo.entities;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "RequestProduct", schema = "dbo", catalog = "BikeWorldDB")
public class RequestProductEntity {
    private int id;
    private Integer creatorId;
    private String createdTime;
    private String editedTime;
    private String title;
    private String description;
    private String status;
    private Collection<RequestNotificationEntity> requestNotificationsById;
    private AccountEntity accountByCreatorId;

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
    @Column(name = "editedTime")
    public String getEditedTime() {
        return editedTime;
    }

    public void setEditedTime(String editedTime) {
        this.editedTime = editedTime;
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
        RequestProductEntity that = (RequestProductEntity) o;
        return id == that.id &&
                Objects.equals(creatorId, that.creatorId) &&
                Objects.equals(createdTime, that.createdTime) &&
                Objects.equals(editedTime, that.editedTime) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creatorId, createdTime, editedTime, title, description, status);
    }

    @OneToMany(mappedBy = "requestProductByRequestProductId")
    public Collection<RequestNotificationEntity> getRequestNotificationsById() {
        return requestNotificationsById;
    }

    public void setRequestNotificationsById(Collection<RequestNotificationEntity> requestNotificationsById) {
        this.requestNotificationsById = requestNotificationsById;
    }

    @ManyToOne
    @JoinColumn(name = "creatorId", referencedColumnName = "id",insertable = false,updatable = false)
    public AccountEntity getAccountByCreatorId() {
        return accountByCreatorId;
    }

    public void setAccountByCreatorId(AccountEntity accountByCreatorId) {
        this.accountByCreatorId = accountByCreatorId;
    }
}
