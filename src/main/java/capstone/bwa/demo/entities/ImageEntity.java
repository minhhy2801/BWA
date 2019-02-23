package capstone.bwa.demo.entities;

import capstone.bwa.demo.View.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Image", schema = "dbo", catalog = "BikeWorldDB")
public class ImageEntity {
    private int id;
    @JsonView({Views.IAccessory.class, Views.IListAccessories.class, Views.IBike.class})
    private String url;
    private String type;
    private String status;
    private int ownId;
    private AccessoryEntity accessoryByOwnId;
    private AccountEntity accountByOwnId;
    private BikeEntity bikeByOwnId;
    private EventEntity eventByOwnId;
    private NewsEntity newsByOwnId;
    private SupplyProductEntity supplyProductByOwnId;

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
        ImageEntity that = (ImageEntity) o;
        return id == that.id &&
                Objects.equals(url, that.url) &&
                Objects.equals(type, that.type) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, type, status);
    }

    @ManyToOne
    @JoinColumn(name = "ownId", referencedColumnName = "id", insertable = false, updatable = false)
    public AccessoryEntity getAccessoryByOwnId() {
        return accessoryByOwnId;
    }

    public void setAccessoryByOwnId(AccessoryEntity accessoryByOwnId) {
        this.accessoryByOwnId = accessoryByOwnId;
    }

    @Basic
    @Column(name = "ownId")
    public int getOwnId() {
        return ownId;
    }

    public void setOwnId(int ownId) {
        this.ownId = ownId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "ownId", referencedColumnName = "id", insertable = false, updatable = false)
    public AccountEntity getAccountByOwnId() {
        return accountByOwnId;
    }

    public void setAccountByOwnId(AccountEntity accountByOwnId) {
        this.accountByOwnId = accountByOwnId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "ownId", referencedColumnName = "id", insertable = false, updatable = false)
    public BikeEntity getBikeByOwnId() {
        return bikeByOwnId;
    }

    public void setBikeByOwnId(BikeEntity bikeByOwnId) {
        this.bikeByOwnId = bikeByOwnId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "ownId", referencedColumnName = "id", insertable = false, updatable = false)
    public EventEntity getEventByOwnId() {
        return eventByOwnId;
    }

    public void setEventByOwnId(EventEntity eventByOwnId) {
        this.eventByOwnId = eventByOwnId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "ownId", referencedColumnName = "id", insertable = false, updatable = false)
    public NewsEntity getNewsByOwnId() {
        return newsByOwnId;
    }

    public void setNewsByOwnId(NewsEntity newsByOwnId) {
        this.newsByOwnId = newsByOwnId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "ownId", referencedColumnName = "id", insertable = false, updatable = false)
    public SupplyProductEntity getSupplyProductByOwnId() {
        return supplyProductByOwnId;
    }

    public void setSupplyProductByOwnId(SupplyProductEntity supplyProductByOwnId) {
        this.supplyProductByOwnId = supplyProductByOwnId;
    }

    @Override
    public String toString() {
        return "ImageEntity{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", accessoryByOwnId=" + accessoryByOwnId +
                ", accountByOwnId=" + accountByOwnId +
                ", bikeByOwnId=" + bikeByOwnId +
                ", eventByOwnId=" + eventByOwnId +
                ", newsByOwnId=" + newsByOwnId +
                ", supplyProductByOwnId=" + supplyProductByOwnId +
                '}';
    }
}

