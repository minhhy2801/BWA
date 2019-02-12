package capstone.bwa.demo.controllers;


import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.exceptions.CustomException;
import capstone.bwa.demo.repositories.EventRepository;
import capstone.bwa.demo.repositories.ImageRepository;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*******************************************************************************
 * ::STATUS::
 * PENDING: chờ duyệt
 * ONGOING: đã duyệt đang trong giai đoạn
 * REJECT: bị trả về
 * HIDDEN: không đủ điều kiện diễn ra event, hoặc bị BAN
 * FINISHED: event kết thúc
 *******************************************************************************/

@RestController
public class EventController {
    private final String pending = "PENDING";
    private final String ongoing = "ONGOING";
    private final String finished = "FINISHED";
    private final String reject = "REJECT";
    private final String hidden = "HIDDEN";

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ImageRepository imageRepository;

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
    @JsonView(View.IEventDetail.class)
    @GetMapping("event/{id}")
    public ResponseEntity getAnEvent(@PathVariable int id) {
        EventEntity entity = eventRepository.getOne(id);
        if (entity == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if (entity.getStatus().equals(pending) || entity.getStatus().equals(reject)
                || entity.getStatus().equals(hidden)) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        // compare public time & current time
        try {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date publicTime = format.parse(entity.getPublicTime());
            if (date.compareTo(publicTime) <= 0) { //current earlier than public
                return new ResponseEntity(HttpStatus.LOCKED);
            }

            return new ResponseEntity(entity, HttpStatus.OK);
        } catch (ParseException e) {
            throw new CustomException("Time Parse Error", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Search like by title (Vietnamese)
     *
     * @param body (txtSearch)
     * @return 404 if not found
     * 200 if OK
     */
    @GetMapping("event/search")
    public ResponseEntity searchListEvents(@RequestBody Map<String, String> body) {
        return null;
    }


    /**
     * Returns list Events object sort last by status and quantity
     * Nếu status = ALL -> lấy hết toàn bộ event theo quantity (bỏ qua status HIDDEN, PENNING, REJECT)
     *
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
    @JsonView(View.IEvents.class)
    @GetMapping("events/page/{id}/limit/{quantity}")
    public ResponseEntity getListEvents(@PathVariable int id, @PathVariable int quantity, @RequestBody Map<String, String> body) {
        String status = body.get("status");

        if(status.equals(hidden) || status.equals(reject) || status.equals(pending))
            return new ResponseEntity(HttpStatus.FORBIDDEN);

        // number of page with n elements
        Pageable pageWithElements = PageRequest.of(id, quantity);
        List<EventEntity> list = eventRepository.findAllByStatus(status, pageWithElements);
        if (list.size() < 1) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * Returns new event object with
     * if mem -> status PENNING
     * if admin -> status ONGOING
     *
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
     *
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
     *
     * @param id
     * @param userId
     * @param body   (status)
     * @return 403 if not admin
     * 200 if update success
     */

    @PutMapping("user/{userId}/event/{id}/status")
    public ResponseEntity changeAnEventStatus(@PathVariable int id, @PathVariable int userId, @RequestBody Map<String, String> body) {

        return null;
    }

    /**
     * Returns list Events object by user id, quantity and status sort last
     * list event của user đó. Nếu status = ALL -> k xét status
     *
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

    @GetMapping("user/{id}/events/limit/{quantity}/status")
    public ResponseEntity getListEventsByUserIdNStatus(@PathVariable int id, @PathVariable int quantity, @RequestBody Map<String, String> body) {

        return null;
    }

    /**
     * Returns list Events object by status, quantity
     * list event của user đó. Nếu status = ALL -> k xét status
     *
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
    @GetMapping("user/events/limit/{quantity}/status")
    public ResponseEntity getListEventsByStatus(@PathVariable int quantity, @RequestBody Map<String, String> body) {

        return null;
    }

}
