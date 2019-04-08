package capstone.bwa.demo.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "ReferencesLink", schema = "dbo", catalog = "BikeWorldDB")
public class ReferencesLinkEntity {
    private int id;
    private String url;
    private Integer categoryId;
    private String status;
//    private CategoryEntity categoryByCategoryId;

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
    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

//    @Basic
//    @Column(name = "categoryId")
//    public Integer getCategoryId() {
//        return categoryId;
//    }
//
//    public void setCategoryId(Integer categoryId) {
//        this.categoryId = categoryId;
//    }

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
        ReferencesLinkEntity that = (ReferencesLinkEntity) o;
        return id == that.id &&
                Objects.equals(url, that.url) &&
                Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, categoryId, status);
    }
//
//    @ManyToOne
//    @JoinColumn(name = "categoryId", referencedColumnName = "id", insertable = false, updatable = false)
//    public CategoryEntity getCategoryByCategoryId() {
//        return categoryByCategoryId;
//    }
//
//    public void setCategoryByCategoryId(CategoryEntity categoryByCategoryId) {
//        this.categoryByCategoryId = categoryByCategoryId;
//    }
}
