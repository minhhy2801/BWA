package capstone.bwa.demo.entities;

import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "Category", schema = "dbo", catalog = "BikeWorldDB")
public class CategoryEntity {
    @JsonView({View.ICategories.class, View.IEventDetail.class, View.INewsDetail.class, View.ISupplyPosts.class,
            View.IEvents.class, View.ISupplyPostDetail.class, View.INews.class})
    private int id;
    @JsonView({View.IEventDetail.class, View.IEvents.class, View.ISupplyPostDetail.class, View.INewsDetail.class,
            View.INews.class, View.ISupplyPosts.class,
            View.IAccessory.class, View.IAccessories.class, View.INews.class,
            View.ICategories.class})
    private String name;
    @JsonView({View.IAccessory.class, View.IAccessories.class})
    private String type;
    private String status;
    private Collection<AccessoryEntity> accessoriesById;
    private Collection<BikeEntity> bikesById;
    private Collection<EventEntity> eventsById;
    private Collection<NewsEntity> newsById;
    //    private Collection<ReferencesLinkEntity> referencesLinksById;
    private Collection<RequestNotificationEntity> requestNotificationsById;
    private Collection<SupplyProductEntity> supplyProductsById;

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
    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        CategoryEntity that = (CategoryEntity) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, status);
    }

    @OneToMany(mappedBy = "categoryByCategoryId")
    public Collection<AccessoryEntity> getAccessoriesById() {
        return accessoriesById;
    }

    public void setAccessoriesById(Collection<AccessoryEntity> accessoriesById) {
        this.accessoriesById = accessoriesById;
    }

    @OneToMany(mappedBy = "categoryByCategoryId")
    public Collection<BikeEntity> getBikesById() {
        return bikesById;
    }

    public void setBikesById(Collection<BikeEntity> bikesById) {
        this.bikesById = bikesById;
    }

    @OneToMany(mappedBy = "categoryByCategoryId")
    public Collection<EventEntity> getEventsById() {
        return eventsById;
    }

    public void setEventsById(Collection<EventEntity> eventsById) {
        this.eventsById = eventsById;
    }

    @OneToMany(mappedBy = "categoryByCategoryId")
    public Collection<NewsEntity> getNewsById() {
        return newsById;
    }

    public void setNewsById(Collection<NewsEntity> newsById) {
        this.newsById = newsById;
    }

//    @OneToMany(mappedBy = "categoryByCategoryId")
//    public Collection<ReferencesLinkEntity> getReferencesLinksById() {
//        return referencesLinksById;
//    }
//
//    public void setReferencesLinksById(Collection<ReferencesLinkEntity> referencesLinksById) {
//        this.referencesLinksById = referencesLinksById;
//    }

    @OneToMany(mappedBy = "categoryByCategoryId")
    public Collection<RequestNotificationEntity> getRequestNotificationsById() {
        return requestNotificationsById;
    }

    public void setRequestNotificationsById(Collection<RequestNotificationEntity> requestNotificationsById) {
        this.requestNotificationsById = requestNotificationsById;
    }

    @OneToMany(mappedBy = "categoryByCategoryId")
    public Collection<SupplyProductEntity> getSupplyProductsById() {
        return supplyProductsById;
    }

    public void setSupplyProductsById(Collection<SupplyProductEntity> supplyProductsById) {
        this.supplyProductsById = supplyProductsById;
    }
}
