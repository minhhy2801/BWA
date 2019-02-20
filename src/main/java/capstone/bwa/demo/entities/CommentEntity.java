package capstone.bwa.demo.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Comment", schema = "dbo", catalog = "BikeWorldDB")
public class CommentEntity {
    private int id;
    private Integer newsId;
    private Integer creatorId;
    private String createdTime;
    private String editedTime;
    private Integer editorId;
    private String description;
    private String status;
    private NewsEntity newsByNewsId;
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
    @Column(name = "newsId")
    public Integer getNewsId() {
        return newsId;
    }

    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
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
    @Column(name = "editorId")
    public Integer getEditorId() {
        return editorId;
    }

    public void setEditorId(Integer editorId) {
        this.editorId = editorId;
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
        CommentEntity that = (CommentEntity) o;
        return id == that.id &&
                Objects.equals(newsId, that.newsId) &&
                Objects.equals(creatorId, that.creatorId) &&
                Objects.equals(createdTime, that.createdTime) &&
                Objects.equals(editorId, that.editorId) &&
                Objects.equals(editedTime, that.editedTime) &&
                Objects.equals(description, that.description) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, newsId, creatorId, createdTime, editorId, editedTime, description, status);
    }

    @ManyToOne
    @JoinColumn(name = "newsId", referencedColumnName = "id", insertable = false, updatable = false)
    public NewsEntity getNewsByNewsId() {
        return newsByNewsId;
    }

    public void setNewsByNewsId(NewsEntity newsByNewsId) {
        this.newsByNewsId = newsByNewsId;
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
