package capstone.bwa.demo.controllers;

import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.entities.EventRegisteredEntity;
import capstone.bwa.demo.entities.FeedbackEntity;
import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.repositories.EventRegisteredRepository;
import capstone.bwa.demo.repositories.EventRepository;
import capstone.bwa.demo.repositories.FeedbackRepository;
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
public class FeedbackController {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventRegisteredRepository eventRegisteredRepository;
    private final String statusPaid = "PAID";
    private final String eventFeedback = "EVENT";
    private final String transFeedback = "TRANS";

    /*************************************
     * FEEDBACK EVENT
     * ***********************************/

    /**
     * Return list feedback of event status FINISHED
     *
     * @param id
     * @return 404 if not found
     * 200 if OK
     * @apiNote {
     * userName, createTime, avatar, description, rate
     * }
     */
    @JsonView(View.IFeedback.class)
    @GetMapping("event/{id}/feedback")
    public ResponseEntity getListFeedbackEvent(@PathVariable int id) {
        List<Integer> listRegisterIds = eventRegisteredRepository.findAllIdByEventId(statusPaid, id);
        System.out.println(listRegisterIds);
        List<FeedbackEntity> feedbackEntities = feedbackRepository.findAllByEventRegisteredIdsIn(listRegisterIds, eventFeedback);
        if (feedbackEntities.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(feedbackEntities, HttpStatus.OK);
    }

    /**
     * Return new feedback of event status APPROVED
     *
     * @param userId
     * @param id
     * @return 400 if can not create
     * 403 if not user or user not registered event
     * 200 if OK
     */
    @JsonView(View.IFeedback.class)
    @PostMapping("user/{userId}/event/{id}/feedback")
    public ResponseEntity createFeedbackEvent(@PathVariable int userId, @PathVariable int id, @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        EventEntity eventEntity = eventRepository.findById(id);
        if (accountEntity == null || eventEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        EventRegisteredEntity eventRegisteredEntity = eventRegisteredRepository.findDistinctFirstByAccountByRegisteredId_IdAndEventByEventId_Id(userId, id);
//        System.out.println(eventRegisteredEntity);
        if (eventRegisteredEntity == null || feedbackRepository.existsByOwnId(eventRegisteredEntity.getId()))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);


        String description = body.get("description");
        String rate = body.get("rate");
        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        FeedbackEntity feedbackEntity = new FeedbackEntity();
        feedbackEntity.setCreatedTime(dateFormat.format(date));
        feedbackEntity.setDescription(description);
        feedbackEntity.setRate(rate);
        feedbackEntity.setOwnId(eventRegisteredEntity.getId());
        feedbackEntity.setStatus(eventFeedback);
        feedbackRepository.save(feedbackEntity);
        return new ResponseEntity(feedbackEntity, HttpStatus.OK);
    }

    /**
     * Return new feedback of event status UPDATED
     * allow update feedback in 1 hours
     *
     * @param userId
     * @param id
     * @return 400 if can not update
     * 403 if not user who feedback
     * 200 if OK
     */
    @JsonView(View.IFeedback.class)
    @PutMapping("user/{userId}/event/{eventId}/feedback/{id}")
    public ResponseEntity updateFeedbackEvent(@PathVariable int userId, @PathVariable int eventId,
                                              @PathVariable int id, @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        EventEntity eventEntity = eventRepository.findById(eventId);
        FeedbackEntity feedbackEntity = feedbackRepository.findById(id);

        if (accountEntity == null || eventEntity == null || feedbackEntity == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);


        String description = body.get("description");
        String rate = body.get("rate");
        feedbackEntity.setDescription(description);
        feedbackEntity.setRate(rate);
        feedbackRepository.save(feedbackEntity);

        return new ResponseEntity(feedbackEntity, HttpStatus.OK);
    }

    /*************************************
     * FEEDBACK SUPPLY POST
     * ***********************************/

    /**
     * Return feedback of event status FINISHED
     *
     * @param id
     * @return 404 if not found
     * 200 if OK
     * @apiNote {
     * userName, createTime, avatar, description, rate
     * }
     */
    @GetMapping("supply_post/{id}/feedback")
    public ResponseEntity getFeedbackSupplyPost(@PathVariable int id) {
        return null;
    }

    /**
     * Return feedback of event status FINISHED
     * Remember check TrasactionDetail table to know who success exchange
     *
     * @param userId
     * @param id
     * @param body
     * @return 403 if not user sucess exchange
     * 400 if can not create
     * 200 if OK
     */
    @PostMapping("user/{userId}/supply_post/{id}/feedback")
    public ResponseEntity createFeedbackSupplyPost(@PathVariable int userId, @PathVariable int id, @RequestBody Map<String, String> body) {
        return null;
    }

    /**
     * Return new feedback of supply post status UPDATED allow update feedback in 1 hours
     *
     * @param userId
     * @param id
     * @param body
     * @return 400 if can not update
     * 403 if not user who feedback
     * 200 if OK
     */
    @PutMapping("user/{userId}/supply_post/{id}/feedback")
    public ResponseEntity updateFeedbackSupplyPost(@PathVariable int userId, @PathVariable int id, @RequestBody Map<String, String> body) {
        return null;
    }


}
