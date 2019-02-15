package capstone.bwa.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
public class EventRegisteredController {

    /**
     * Return list users registered event sort latest time with status PAID
     * Remember check status of Event
     * @param id
     * @param userId
     * @return 403 if not admin or mem own event
     * 200 if OK
     * @apiNote {
     *   registerName, registerTime, quantitySold, avatar
     * }
     */
    @GetMapping("user/{userId}/event/{id}/list_user_registered")
    public ResponseEntity getListRegisteredEventUsers(@PathVariable int id, @PathVariable int userId) {
        return null;
    }

    /**
     * Return an new event registered with status PAID
     * @param userId
     * @param id
     * @param body
     * @return 403 if not user
     * 200 if OK
     */
    @PostMapping("user/{userId}/event/{id}")
    public ResponseEntity registerEvent(@PathVariable int userId, @PathVariable int id, @RequestBody Map<String, String> body) {
        return null;

    }
}
