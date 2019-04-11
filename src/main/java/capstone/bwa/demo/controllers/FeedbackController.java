package capstone.bwa.demo.controllers;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.*;
import capstone.bwa.demo.repositories.*;
import capstone.bwa.demo.utils.DateTimeUtils;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private SupplyProductRepository supplyProductRepository;
    @Autowired
    private EventRegisteredRepository eventRegisteredRepository;
    @Autowired
    private TransactionDetailRepository transactionDetailRepository;
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
        List<Integer> listRegisterIds = eventRegisteredRepository
                .findAllIdByEventId(MainConstants.REGISTERED_PAID, id);
//        System.out.println(listRegisterIds);
        if (listRegisterIds.size() > 0) {
            List<FeedbackEntity> feedbackEntities = feedbackRepository
                    .findAllByEventRegisteredIdsIn(listRegisterIds, MainConstants.STATUS_EVENT);
            if (feedbackEntities.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);
            return new ResponseEntity(feedbackEntities, HttpStatus.OK);

        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    /**
     * Return new feedback of event status APPROVED
     *
     * @param userId
     * @param id
     * @return 400 if can not create
     * 403 if not user or user not registered event || over 1 week
     * 200 if OK
     */
    @JsonView(View.IFeedback.class)
    @PostMapping("user/{userId}/event/{id}/feedback")
    public ResponseEntity createFeedbackEvent(@PathVariable int userId, @PathVariable int id, @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        EventEntity eventEntity = eventRepository.findById(id);
        if (accountEntity == null || eventEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        EventRegisteredEntity eventRegisteredEntity = eventRegisteredRepository.findDistinctFirstByAccountByRegisteredId_IdAndEventByEventId_Id(userId, id);

        if (eventRegisteredEntity == null ||
                !eventEntity.getStatus().equals(MainConstants.EVENT_FINISHED) ||
                feedbackRepository.existsByOwnIdAndStatus(eventRegisteredEntity.getId(), MainConstants.STATUS_EVENT))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        if (DateTimeUtils.compareWithRejectFeedbackEvent(eventEntity.getEndTime(), Calendar.DATE,
                MainConstants.NUM_OF_DATE_REJECT_FEEDBACK)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        FeedbackEntity feedbackEntity = new FeedbackEntity();

        String description = body.get("description");
        String rate = body.get("rate");
        feedbackEntity.setCreatedTime(DateTimeUtils.getCurrentTime());
        feedbackEntity.setDescription(description);
        feedbackEntity.setRate(rate);
        feedbackEntity.setOwnId(eventRegisteredEntity.getId());
        feedbackEntity.setStatus(MainConstants.STATUS_EVENT);
        feedbackRepository.save(feedbackEntity);
        float numRate = Float.parseFloat(rate) + Float.parseFloat(eventEntity.getTotalRate());
//        System.out.println("Rate: " + numRate);
        eventEntity.setTotalRate(numRate + "");
        eventEntity.setTotalFeedback(eventEntity.getTotalFeedback() + 1);
        eventRepository.save(eventEntity);
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
        EventRegisteredEntity eventRegisteredEntity = eventRegisteredRepository.findById(feedbackEntity.getEventRegisteredByOwnId().getId());
//        System.out.println(eventRegisteredEntity);
        if (!eventRegisteredEntity.getRegisteredId().equals(userId) ||
                !eventRegisteredEntity.getEventId().equals(eventId))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);


        try {
            Date date = new Date(System.currentTimeMillis());
            DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
            Date createTime = dateFormat.parse(feedbackEntity.getCreatedTime());
            //Set created time + 15mins
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createTime);
            calendar.add(Calendar.MINUTE, 15);
            Date afterAdding15Mins = calendar.getTime();
//            System.out.println(afterAdding15Mins);
            if (date.compareTo(afterAdding15Mins) < 0) {

                String description = body.get("description");
                String rate = body.get("rate");

                float numRate = Float.parseFloat(rate)
                        + Float.parseFloat(eventEntity.getTotalRate())
                        - Float.parseFloat(feedbackEntity.getRate());
                eventEntity.setTotalRate(numRate + "");
                eventRepository.save(eventEntity);

                feedbackEntity.setDescription(description);
                feedbackEntity.setRate(rate);
                feedbackRepository.save(feedbackEntity);
                return new ResponseEntity(feedbackEntity, HttpStatus.OK);
            }
//            else System.out.println("hết cho sửa rồi");
        } catch (ParseException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
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
    @JsonView(View.IFeedback.class)
    @GetMapping("supply_post/{id}/feedback")
    public ResponseEntity getFeedbackSupplyPost(@PathVariable int id) {
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(id);
        if (supplyProductEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_CLOSED)) {
            TransactionDetailEntity transactionDetailEntity = transactionDetailRepository.findBySupplyProductIdAndStatus(id, MainConstants.TRANSACTION_SUCCESS);
            if (transactionDetailEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
            FeedbackEntity feedbackEntity = feedbackRepository.findByTransactionDetailByOwnId_IdAndStatus(transactionDetailEntity.getId(), MainConstants.STATUS_TRANS);
            if (feedbackEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
            return new ResponseEntity(feedbackEntity, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
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
    @JsonView(View.IFeedback.class)
    @PostMapping("user/{userId}/supply_post/{id}/feedback")
    public ResponseEntity createFeedbackSupplyPost(@PathVariable int userId, @PathVariable int id, @RequestBody Map<String, String> body) throws ParseException {
        AccountEntity accountEntity = accountRepository.findById(userId);
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(id);
        if (accountEntity == null || supplyProductEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (DateTimeUtils.compareWithRejectFeedbackSupplyPost(supplyProductEntity.getClosedTime(), Calendar.DATE,
                MainConstants.NUM_OF_DATE_REJECT_FEEDBACK)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_CLOSED) &&
                accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)) {
            TransactionDetailEntity transactionDetailEntity =
                    transactionDetailRepository.findBySupplyProductIdAndStatus(id, MainConstants.TRANSACTION_SUCCESS);
            if (transactionDetailEntity.getInteractiveId().equals(userId)) {
                if (!feedbackRepository.existsByOwnIdAndStatus(transactionDetailEntity.getId(), MainConstants.STATUS_TRANS)) {
                    FeedbackEntity feedbackEntity = new FeedbackEntity();

                    String description = body.get("description");
                    String rate = body.get("rate");

                    Date date = new Date(System.currentTimeMillis());
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
                    feedbackEntity.setCreatedTime(dateFormat.format(date));

                    feedbackEntity.setDescription(description);
                    feedbackEntity.setRate(rate);
                    feedbackEntity.setOwnId(transactionDetailEntity.getId());
                    feedbackEntity.setStatus(MainConstants.STATUS_TRANS);
                    feedbackRepository.save(feedbackEntity);
                    supplyProductEntity.setRate(rate);
                    supplyProductRepository.save(supplyProductEntity);
                    return new ResponseEntity(feedbackEntity, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
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
    @JsonView(View.IFeedback.class)
    @PutMapping("user/{userId}/supply_post/{supplyId}/feedback/{id}")
    public ResponseEntity updateFeedbackSupplyPost(@PathVariable int userId, @PathVariable int supplyId,
                                                   @PathVariable int id, @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(supplyId);
        FeedbackEntity feedbackEntity = feedbackRepository.findById(id);
        if (accountEntity == null || supplyProductEntity == null || feedbackEntity == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (feedbackEntity.getTransactionDetailByOwnId().getInteractiveId().equals(userId) &&
                feedbackEntity.getStatus().equals(MainConstants.STATUS_TRANS)) {
            try {
                Date date = new Date(System.currentTimeMillis());
                DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
                Date createTime = dateFormat.parse(feedbackEntity.getCreatedTime());
                //Set created time + 15mins
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(createTime);
                calendar.add(Calendar.MINUTE, 15);
                Date afterAdding15Mins = calendar.getTime();
//            System.out.println(afterAdding15Mins);
                if (date.compareTo(afterAdding15Mins) < 0) {

                    String description = body.get("description");
                    String rate = body.get("rate");

                    supplyProductEntity.setRate(rate);
                    supplyProductRepository.save(supplyProductEntity);
                    feedbackEntity.setDescription(description);
                    feedbackEntity.setRate(rate);
                    feedbackRepository.save(feedbackEntity);
                    return new ResponseEntity(feedbackEntity, HttpStatus.OK);
                }
            } catch (ParseException e) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @JsonView(View.IFeedback.class)
    @GetMapping("user/profile/{userId}/success_feedback")
    public ResponseEntity getListFeedbackInViewProfile(@PathVariable int userId) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        List<Integer> listIds = supplyProductRepository.findAllIdsByStatusAnAndCreatorId(MainConstants.SUPPLY_POST_CLOSED, userId);
        if (listIds.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);
        List<Integer> list = transactionDetailRepository.findAllIdsByStatusAnAndSupplyPostIdIn(MainConstants.TRANSACTION_SUCCESS, listIds);
        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);
        List<FeedbackEntity> feedbackEntities = feedbackRepository.findAllByEventRegisteredIdsIn(list, MainConstants.STATUS_TRANS);
        if (feedbackEntities.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(feedbackEntities, HttpStatus.OK);
    }

}
