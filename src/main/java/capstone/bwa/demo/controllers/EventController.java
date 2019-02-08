package capstone.bwa.demo.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*******************************************************************************
 * ::STATUS::
 * PENNING: chờ duyệt
 * COMING: chờ ngày public
 * ONGOING: đã duyệt đang trong giai đoạn diễn ra
 * REJECT: bị trả về
 * HIDDEN: không đủ điều kiện diễn ra event, hoặc bị BAN
 * FINISHED: event kết thúc
 *******************************************************************************/

@RestController
public class EventController {

    /**
     * Returns an Event object
     *
     * @param id of event
     * @return 404 if not found in db
     * 200 if found
     * @apiNote example format
     * if not yet public time
     * {
     * creatorName, categoryName, thumbnailUrl, title, description, location,
     * ticketPrice, publicTime, minTicket, maxTicket
     * }
     * if over public time
     * {
     * creatorName, categoryName, thumbnailUrl, title, description, location,
     * ticketPrice, createTime, startEventTime, endEventTime,
     * startRegisterTime, endRegisterTime, numberOfSoldTicket, minTicket, maxTicket
     * }
     * if event status finished
     * @apiNote example format:
     * add more
     * {
     * totalSoldTicket, totalFeedback, totalRate
     * }
     */
    @GetMapping("event/{id}")
    public ResponseEntity getAnEvent(@PathVariable int id) {

        return null;
    }

    /**
     * Returns list Events object sort last by status and quantity
     * Nếu status = ALL -> lấy hết toàn bộ event theo quantity (bỏ qua status HIDDEN, PENNING, REJECT)
     * @return 404 if not found in db
     * 200 if found
     * @apiNote example format:
     * {
     * creatorName, categoryName, thumbnailUrl, title, location, createTime, status, publicTime
     * }
     * if event status finished
     * @apiNote example format:
     * add more
     * {
     * totalSoldTicket, totalFeedback, totalRate
     * }
     */

    @GetMapping("events/limit/{quantity}/status")
    public ResponseEntity getListEvents(@PathVariable int quantity, @RequestBody Map<String, String> status) {

        return null;
    }

    /**
     * Returns new event object with
     * if mem -> status PENNING
     * if admin -> status COMING
     * @return 403 if not admin
     * 200 if create success
     */

    @PostMapping("user/{id}/event")
    public ResponseEntity createAnEvent(@PathVariable int id, @RequestBody Map<String, String> body) {

        return null;
    }

    /**
     * Returns update event object
     * if mem update -> status PENNING
     * if admin update -> status COMING
     * Admin can update event of other admin
     * Mem can only update event of him/herself
     * @param id
     * @param userId
     * @param body
     * @return 403 if not admin
     * 200 if update success
     */

    @PutMapping("user/{userId}/event/{id}")
    public ResponseEntity updateAnEvent(@PathVariable int id, @PathVariable int userId, @RequestBody Map<String, String> body) {

        return null;
    }

    /**
     * Returns update event object with status
     * @param id
     * @param userId
     * @param status
     * @return 403 if not admin
     * 200 if update success
     */

    @PutMapping("user/{userId}/event/{id}/status")
    public ResponseEntity changeAnEventStatus(@PathVariable int id, @PathVariable int userId, @RequestBody Map<String, String> status) {

        return null;
    }

    /**
     * Returns list Events object by user id, quantity and status sort last
     * list event của user đó. Nếu status = ALL -> k xét status
     * @return 404 if not found in db
     * 200 if found
     * @apiNote example format:
     * {
     * creatorName, categoryName, thumbnailUrl, title, location, createTime, status, publicTime
     * }
     * if event status finished
     * @apiNote example format:
     * add more
     * {
     * totalSoldTicket, totalFeedback, totalRate
     * }
     *
     */

    @GetMapping("user/{id}/events/limit/{quantity}/status")
    public ResponseEntity getListEventsByUserIdNStatus(@PathVariable int id, @PathVariable int quantity, @RequestBody Map<String, String> status) {

        return null;
    }

    /**
     * Returns list Events object by status, quantity
     * list event của user đó. Nếu status = ALL -> k xét status
     * @return 404 if not found in db
     * 200 if found
     * @apiNote example format:
     * {
     * creatorName, categoryName, thumbnailUrl, title, location, createTime, status, publicTime
     * }
     * if event status finished
     * @apiNote example format:
     * add more
     * {
     * totalSoldTicket, totalFeedback, totalRate
     * }
     *
     */
    @GetMapping("user/events/limit/{quantity}/status")
    public ResponseEntity getListEventsByStatus(@PathVariable int quantity, @RequestBody Map<String, String> status) {

        return null;
    }
}
