package capstone.bwa.demo.controllers;


import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.entities.ImageEntity;
import capstone.bwa.demo.exceptions.CustomException;
import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.repositories.EventRepository;
import capstone.bwa.demo.repositories.ImageRepository;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import okhttp3.internal.Internal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private final String getAll = "ALL";
    private final String roleUser = "USER";
    private final String roleAdmin = "ADMIN";
    private final String accountStatus = "ACTIVE";


    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private AccountRepository accountRepository;
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
        EventEntity entity = eventRepository.findById(id);
        if (entity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (entity.getStatus().equals(pending) || entity.getStatus().equals(reject)
                || entity.getStatus().equals(hidden))
            return new ResponseEntity(HttpStatus.FORBIDDEN);

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
    public ResponseEntity getListEvents(@PathVariable int id, @PathVariable int quantity,
                                        @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (body.isEmpty()) return new ResponseEntity(HttpStatus.NO_CONTENT);

        if (status.equals(hidden) || status.equals(reject) || status.equals(pending))
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        List<EventEntity> list;
        // number of page with n elements
        Pageable pageWithElements = PageRequest.of(id, quantity);
        if (status.equals(getAll))
            list = eventRepository.findAllByStatusOrStatusOrderByIdDesc(ongoing, finished, pageWithElements);
        else list = eventRepository.findAllByStatusOrderByIdDesc(status, pageWithElements);

        if (list.size() < 1)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

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

    @JsonView(View.IEventDetail.class)
    @PostMapping("user/{id}/event")
    public ResponseEntity createAnEvent(@PathVariable int id, @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(id);
        if (body.isEmpty()) return new ResponseEntity(HttpStatus.NO_CONTENT);
        if (accountEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (accountEntity.getRoleByRoleId().getName().equals(roleUser) &&
                accountEntity.getStatus().equals(accountStatus)) {
            Date date = new Date(System.currentTimeMillis());

            EventEntity eventEntity = paramEventEntityRequest(body, new EventEntity());
            eventEntity.setCreatorId(id);
            eventEntity.setCreatedTime(date.toString());

            eventRepository.saveAndFlush(eventEntity);
            setEventListImages(body, eventEntity);

            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
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
    @JsonView(View.IEventDetail.class)
    @PutMapping("user/{userId}/event/{id}")
    public ResponseEntity updateAnEvent(@PathVariable int id, @PathVariable int userId, @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        EventEntity eventEntity = eventRepository.findById(id);

        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.NO_CONTENT);
        if (eventEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (accountEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (!eventEntity.getCreatorId().equals(userId)) return new ResponseEntity(HttpStatus.FORBIDDEN);

        if (accountEntity.getRoleByRoleId().getName().equals(roleUser) &&
                accountEntity.getStatus().equals(accountStatus)) {
            eventEntity = paramEventEntityRequest(body, eventEntity);
            eventRepository.saveAndFlush(eventEntity);
            List<ImageEntity> list = imageRepository.findAllByEventByOwnId_IdAndType(id, "EVENT");
            imageRepository.deleteAll(list);

            setEventListImages(body, eventEntity);
        }
        return new ResponseEntity(eventEntity, HttpStatus.OK);
    }

    /**
     * Returns update event object with status
     *
     * @param id
     * @param adminId
     * @param body    (status)
     * @return 403 if not admin
     * 200 if update success
     */

    @PutMapping("admin/{adminId}/event/{id}/status")
    public ResponseEntity changeAnEventStatus(@PathVariable int id, @PathVariable int adminId, @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(adminId);
        EventEntity eventEntity = eventRepository.findById(id);

        if (accountEntity == null || eventEntity == null
                || !accountEntity.getStatus().equals(accountStatus)
                || !accountEntity.getRoleByRoleId().getName().equals(roleAdmin))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (body == null || body.isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Date date = new Date(System.currentTimeMillis());
        String status = body.get("status");
//        SimpleDateFormat format = new SimpleDateFormat("hh:mm dd/MM/yyyy");
//        Date testTime = null;
//        try {
//            testTime = format.parse("12:20 16/02/2019");
//            if (date.compareTo(testTime) <= 0)
//                System.out.println("time test chưa tới");
//            else
//                System.out.println("time test qua rồi");
//        } catch (ParseException e) {
//            throw new CustomException("Time Parse Error", HttpStatus.BAD_REQUEST);
//        }

        eventEntity.setApprovedId(adminId);
        eventEntity.setApprovedTime(date.toString());
        eventEntity.setStatus(status);
        eventRepository.save(eventEntity);

        return new ResponseEntity(HttpStatus.OK);
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
    @JsonView(View.IEvents.class)
    @GetMapping("user/{id}/events/page/{pageId}/limit/{quantity}")
    public ResponseEntity getListEventsByUserIdNStatus(@PathVariable int id, @PathVariable int quantity,
                                                       @PathVariable int pageId, @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(id);
        if (accountEntity == null || !accountEntity.getRoleByRoleId().getName().equals(roleUser)
                || !accountEntity.getStatus().equals(accountStatus))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (body == null || body.isEmpty()) return new ResponseEntity((HttpStatus.BAD_REQUEST));

        Pageable pageWithElements = PageRequest.of(pageId, quantity);
        List<EventEntity> list;
        String status = body.get("status");
        if (status.equals(getAll)) {
            list = eventRepository.findAllByCreatorIdOrderByIdDesc(id, pageWithElements);
        } else {
            list = eventRepository.findAllByCreatorIdAndStatusOrderByIdDesc(id, pageWithElements, status);
        }
        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(list, HttpStatus.OK);
    }


    //==========================
    private void setEventListImages(Map<String, String> body, EventEntity eventEntity) {
        String img = body.get("images").trim();
        String tmp = img.replace("[", "").replace("]", "").trim();
        String[] arr = tmp.split(",");

        for (int i = 0; i < arr.length; i++) {
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setUrl(arr[i].trim());
            imageEntity.setOwnId(eventEntity.getId());
            imageEntity.setType("EVENT");
            imageRepository.save(imageEntity);
//                System.out.println("url " + imageEntity.getUrl());
        }
    }

    private EventEntity paramEventEntityRequest(Map<String, String> body, EventEntity eventEntity) {
        int cateId = Integer.parseInt(body.get("categoryId"));
        String imgThumbnail = body.get("imgThumbnailUrl");
        String title = body.get("title");
        String description = body.get("description");
        String location = body.get("location");
        String priceTicket = body.get("priceTicket");
        int minTicket = Integer.parseInt(body.get("minTicket"));
        int maxTicket = Integer.parseInt(body.get("maxTicket"));
//            String createdTime = body.get("createdTime");
        String startSignUpTime = body.get("startRegisterTime");
        String endSignUpTime = body.get("endRegisterTime");
        String startEventTime = body.get("startTime");
        String endEventTime = body.get("endTime");
        String publicTime = body.get("publicTime");
        //
        eventEntity.setCategoryId(cateId);
        eventEntity.setTitle(title);
        eventEntity.setImgThumbnailUrl(imgThumbnail);
        eventEntity.setDescription(description);
        eventEntity.setLocation(location);
        eventEntity.setPriceTicket(priceTicket);
        eventEntity.setMinTicket(minTicket);
        eventEntity.setMaxTicket(maxTicket);

        eventEntity.setStartRegisterTime(startSignUpTime);
        eventEntity.setEndRegisterTime(endSignUpTime);
        eventEntity.setStartTime(startEventTime);
        eventEntity.setEndTime(endEventTime);
        eventEntity.setPublicTime(publicTime);
        eventEntity.setStatus(pending);
        return eventEntity;
    }

}
