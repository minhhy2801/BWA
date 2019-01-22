package capstone.bwa.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class FeedbackController {

    /*************************************
     * FEEDBACK EVENT
     * ***********************************/

    /**
     * Return list feedback of event status FINISHED
     * @apiNote {
     *     userName, createTime, avatar, description, rate
     * }
     * @param id
     * @return 404 if not found
     * 200 if OK
     */
    @GetMapping("event/{id}/feedback")
    public ResponseEntity getListFeedbackEvent(@PathVariable int id) {
        return null;
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
    @PostMapping("user/{userId}/event/{id}/feedback")
    public ResponseEntity createFeedbackEvent(@PathVariable int userId, @PathVariable int id, @RequestBody Map<String, String> body) {
        return null;
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
    @PutMapping("user/{userId}/event/{id}/feedback")
    public ResponseEntity updateFeedbackEvent(@PathVariable int userId, @PathVariable int id, @RequestBody Map<String, String> body) {
        return null;
    }

    /*************************************
     * FEEDBACK SUPPLY POST
     * ***********************************/

    /**
     * Return feedback of event status FINISHED
     * @apiNote {
     *  userName, createTime, avatar, description, rate
     * }
     * @param id
     * @return 404 if not found
     * 200 if OK
     */
    @GetMapping("supply_post/{id}/feedback")
    public ResponseEntity getFeedbackSupplyPost(@PathVariable int id) {
        return null;
    }

    /**
     * Return feedback of event status FINISHED
     * Remember check TrasactionDetail table to know who success exchange
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
