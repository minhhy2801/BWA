package capstone.bwa.demo.entities;

import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "Accessory", schema = "dbo", catalog = "BikeWorldDB")
public class AccessoryEntity {
    @JsonView({View.IAccessory.class, View.IAccessories.class,
            View.ISupplyPostDetail.class})
    private int id;

    @JsonView({View.IAccessory.class, View.IAccessories.class,
            View.ISupplyPostDetail.class})
    private String name;
    private String url;

    @JsonView(View.ISupplyPostDetail.class)
    private String brand;

    @JsonView({View.IAccessory.class, View.IAccessories.class,
            View.ISupplyPostDetail.class})
    private String price;
    private Integer categoryId;

    @JsonView({View.IAccessory.class, View.IAccessories.class,
            View.ISupplyPostDetail.class})
    private String description;
    private String hashAccessoryCode;
    private String status;

    @JsonView({View.IAccessory.class, View.IAccessories.class,
            View.ISupplyPostDetail.class})
    private CategoryEntity categoryByCategoryId;

    @JsonView({View.IAccessory.class, View.IAccessories.class})
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
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Basic
    @Column(name = "brand")
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Basic
    @Column(name = "price")
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "hashAccessoryCode")
    public String getHashAccessoryCode() {
        return hashAccessoryCode;
    }

    public void setHashAccessoryCode(String hashAccessoryCode) {
        this.hashAccessoryCode = hashAccessoryCode;
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
        AccessoryEntity that = (AccessoryEntity) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(url, that.url) &&
                Objects.equals(brand, that.brand) &&
                Objects.equals(price, that.price) &&
                Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(description, that.description) &&
                Objects.equals(hashAccessoryCode, that.hashAccessoryCode) &&
                Objects.equals(status, that.status);
    }

    //change hashCode
    @Override
    public int hashCode() {
        return Objects.hash(url, name, brand);
    }

    @ManyToOne
    @JoinColumn(name = "categoryId", referencedColumnName = "id", insertable = false, updatable = false)
    public CategoryEntity getCategoryByCategoryId() {
        return categoryByCategoryId;
    }

    public void setCategoryByCategoryId(CategoryEntity categoryByCategoryId) {
        this.categoryByCategoryId = categoryByCategoryId;
    }

    @OneToMany(mappedBy = "accessoryByOwnId")
    public Collection<ImageEntity> getImagesById() {
        return imagesById;
    }

    public void setImagesById(Collection<ImageEntity> imagesById) {
        this.imagesById = imagesById;
    }

    @Override
    public String toString() {
        return "AccessoryEntity{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", brand='" + brand + '\'' +
                ", price='" + price + '\'' +
                ", categoryId=" + categoryId +
                ", description='" + description + '\'' +
                '}';
    }
}
