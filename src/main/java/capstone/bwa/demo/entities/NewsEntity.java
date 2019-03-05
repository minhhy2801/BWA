package capstone.bwa.demo.entities;

import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "News", schema = "dbo", catalog = "BikeWorldDB")
public class NewsEntity {
    private int id;
	@JsonView(View.INews.class)
    private String title;
	@JsonView(View.INews.class)
    private String description;
    private Integer creatorId;
	@JsonView(View.INews.class)
    private String imgThumbnailUrl;
    private Integer categoryId;
    private Integer editorId;
    private String createdTime;
    private String editedTime;
    private String status;
    private Collection<CommentEntity> commentsById;
//	@JsonView(View.INews.class)
    private Collection<ImageEntity> imagesById;
    private AccountEntity accountByCreatorId;
	@JsonView(View.INews.class)
    private CategoryEntity categoryByCategoryId;
    private AccountEntity accountByEditorId;

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
    @Column(name = "imgThumbnailUrl")
    public String getImgThumbnailUrl() {
        return imgThumbnailUrl;
    }

    public void setImgThumbnailUrl(String imgThumbnailUrl) {
        this.imgThumbnailUrl = imgThumbnailUrl;
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
    @Column(name = "editorId")
    public Integer getEditorId() {
        return editorId;
    }

    public void setEditorId(Integer editorId) {
        this.editorId = editorId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsEntity that = (NewsEntity) o;
        return id == that.id &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(creatorId, that.creatorId) &&
                Objects.equals(imgThumbnailUrl, that.imgThumbnailUrl) &&
                Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(editorId, that.editorId) &&
                Objects.equals(createdTime, that.createdTime) &&
                Objects.equals(editedTime, that.editedTime) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, creatorId, imgThumbnailUrl, categoryId, editorId, createdTime, editedTime, status);
    }

    @OneToMany(mappedBy = "newsByNewsId")
    public Collection<CommentEntity> getCommentsById() {
        return commentsById;
    }

    public void setCommentsById(Collection<CommentEntity> commentsById) {
        this.commentsById = commentsById;
    }

    @OneToMany(mappedBy = "newsByOwnId")
    public Collection<ImageEntity> getImagesById() {
        return imagesById;
    }

    public void setImagesById(Collection<ImageEntity> imagesById) {
        this.imagesById = imagesById;
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
    @JoinColumn(name = "categoryId", referencedColumnName = "id", insertable = false, updatable = false)
    public CategoryEntity getCategoryByCategoryId() {
        return categoryByCategoryId;
    }

    public void setCategoryByCategoryId(CategoryEntity categoryByCategoryId) {
        this.categoryByCategoryId = categoryByCategoryId;
    }

    @ManyToOne
    @JoinColumn(name = "editorId", referencedColumnName = "id", insertable = false, updatable = false)
    public AccountEntity getAccountByEditorId() {
        return accountByEditorId;
    }

    public void setAccountByEditorId(AccountEntity accountByEditorId) {
        this.accountByEditorId = accountByEditorId;
    }
}
