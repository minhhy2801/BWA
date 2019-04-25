package capstone.bwa.demo.entities;

import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "EventRegistered", schema = "dbo", catalog = "BikeWorldDB")
public class EventRegisteredEntity {
    @JsonView({View.IEventRegistered.class})
    private int id;
    private Integer eventId;
    private Integer registeredId;
    @JsonView({View.IEventRegistered.class})
    private String registeredTime;
    @JsonView({View.IEventRegistered.class})
    private Integer purchasedTicket;
    private String status;
    @JsonView({View.IEventRegistered.class})
    private String ticketCode;
    @JsonView({View.IEventRegistered.class})
    private EventEntity eventByEventId;
    @JsonView({View.IEventRegistered.class, View.IFeedback.class})
    private AccountEntity accountByRegisteredId;
    private Collection<FeedbackEntity> feedbacksById;

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
    @Column(name = "eventId")
    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    @Basic
    @Column(name = "registeredId")
    public Integer getRegisteredId() {
        return registeredId;
    }

    public void setRegisteredId(Integer registeredId) {
        this.registeredId = registeredId;
    }

    @Basic
    @Column(name = "registeredTime")
    public String getRegisteredTime() {
        return registeredTime;
    }

    public void setRegisteredTime(String registeredTime) {
        this.registeredTime = registeredTime;
    }

    @Basic
    @Column(name = "purchasedTicket")
    public Integer getPurchasedTicket() {
        return purchasedTicket;
    }

    public void setPurchasedTicket(Integer purchasedTicket) {
        this.purchasedTicket = purchasedTicket;
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
    @Column(name = "ticketCode")
    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventRegisteredEntity that = (EventRegisteredEntity) o;
        return id == that.id &&
                Objects.equals(eventId, that.eventId) &&
                Objects.equals(registeredId, that.registeredId) &&
                Objects.equals(registeredTime, that.registeredTime) &&
                Objects.equals(purchasedTicket, that.purchasedTicket) &&
                Objects.equals(status, that.status) &&
                Objects.equals(ticketCode, that.ticketCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventId, registeredId, registeredTime, purchasedTicket, status, ticketCode);
    }

    @ManyToOne
    @JoinColumn(name = "eventId", referencedColumnName = "id", insertable = false, updatable = false)
    public EventEntity getEventByEventId() {
        return eventByEventId;
    }

    public void setEventByEventId(EventEntity eventByEventId) {
        this.eventByEventId = eventByEventId;
    }

    @ManyToOne
    @JoinColumn(name = "registeredId", referencedColumnName = "id", insertable = false, updatable = false)
    public AccountEntity getAccountByRegisteredId() {
        return accountByRegisteredId;
    }

    public void setAccountByRegisteredId(AccountEntity accountByRegisteredId) {
        this.accountByRegisteredId = accountByRegisteredId;
    }

    @OneToMany(mappedBy = "eventRegisteredByOwnId")
    public Collection<FeedbackEntity> getFeedbacksById() {
        return feedbacksById;
    }

    public void setFeedbacksById(Collection<FeedbackEntity> feedbacksById) {
        this.feedbacksById = feedbacksById;
    }
}
