package capstone.bwa.demo.controllers;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.entities.EventRegisteredEntity;
import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.repositories.EventRegisteredRepository;
import capstone.bwa.demo.repositories.EventRepository;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
public class EventRegisteredController {
    @Autowired
    private EventRegisteredRepository eventRegisteredRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private EventRepository eventRepository;

    /**
     * Return list users registered event sort latest time with status PAID
     * Remember check status of Event
     *
     * @param id
     * @param userId
     * @return 403 if not admin or mem own event
     * 200 if OK
     * @apiNote {
     * registerName, registerTime, quantitySold, avatar
     * }
     */
    @JsonView(View.IEventRegistered.class)
    @GetMapping("user/{userId}/event/{id}/list_users_registered")
    public ResponseEntity getListRegisteredEventUsers(@PathVariable int id, @PathVariable int userId) {
        EventEntity eventEntity = eventRepository.findById(id);
        AccountEntity accountEntity = accountRepository.findById(userId);

        if (accountEntity == null || eventEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (eventEntity.getAccountByCreatorId().getId() != userId) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        List<EventRegisteredEntity> list = eventRegisteredRepository.findAllByEventByEventId(eventEntity);

        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * Return an new event registered with status PAID
     *
     * @param userId
     * @param id
     * @param body
     * @return 403 if not user
     * 200 if OK
     */
    @PostMapping("user/{userId}/event/{id}/register")
    public ResponseEntity registerEvent(@PathVariable int userId, @PathVariable int id, @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        EventEntity eventEntity = eventRepository.findById(id);
        if (accountEntity == null || eventEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (!eventEntity.getStatus().equals(MainConstants.EVENT_ONGOING) &&
                !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.LOCKED);
        int purchasedTicket = Integer.parseInt(body.get("purchasedTicket"));
        Date today = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        EventRegisteredEntity eventRegisteredEntity = new EventRegisteredEntity();
        eventRegisteredEntity.setRegisteredId(accountEntity.getId());
        eventRegisteredEntity.setEventId(eventEntity.getId());
        eventRegisteredEntity.setPurchasedTicket(purchasedTicket);
        eventRegisteredEntity.setRegisteredTime(dateFormat.format(today));
        eventRegisteredEntity.setStatus(MainConstants.REGISTERED_PAID);
        eventRegisteredRepository.save(eventRegisteredEntity);
        int totalSoldTicket = eventEntity.getTotalSoldTicket() + purchasedTicket;
        eventEntity.setTotalSoldTicket(totalSoldTicket);
        eventRepository.save(eventEntity);

        return new ResponseEntity(eventRegisteredEntity, HttpStatus.OK);
    }

    @JsonView(View.IEventRegistered.class)
    @GetMapping("user/{id}/list_event_registered")
    public ResponseEntity getListEventRegisteredOfUser(@PathVariable int id) {
        AccountEntity accountEntity = accountRepository.findById(id);
        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        List<EventRegisteredEntity> eventRegisteredEntities = eventRegisteredRepository.findAllByAccountByRegisteredId_Id(id);
        if (eventRegisteredEntities.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(eventRegisteredEntities, HttpStatus.OK);
    }

    @GetMapping("user/{id}/event/{eventId}/registered")
    public ResponseEntity isRegistered(@PathVariable int id, @PathVariable int eventId) {
        AccountEntity accountEntity = accountRepository.findById(id);
        EventEntity eventEntity = eventRepository.findById(eventId);
        if (accountEntity == null || eventEntity == null
                || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !eventEntity.getStatus().equals(MainConstants.EVENT_FINISHED))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        boolean check = eventRegisteredRepository.existsDistinctByEventIdAndRegisteredId(eventId, id);
        if (check)
            return new ResponseEntity(HttpStatus.OK);
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

}

