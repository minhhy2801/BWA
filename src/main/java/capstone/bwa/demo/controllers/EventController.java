package capstone.bwa.demo.controllers;


import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.CategoryEntity;
import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.entities.ImageEntity;
import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.repositories.CategoryRepository;
import capstone.bwa.demo.repositories.EventRepository;
import capstone.bwa.demo.repositories.ImageRepository;
import capstone.bwa.demo.services.DistanceMatrixRequestService;
import capstone.bwa.demo.utils.DateTimeUtils;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
public class EventController {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @JsonView(View.IEventDetail.class)
    @GetMapping("event/{id}")
    public ResponseEntity getAnEvent(@PathVariable int id) {
        EventEntity entity = eventRepository.findById(id);

        if (entity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (!entity.getStatus().equals(MainConstants.EVENT_ONGOING) &&
                !entity.getStatus().equals(MainConstants.EVENT_FINISHED) &&
                !entity.getStatus().equals(MainConstants.EVENT_CLOSED))
            return new ResponseEntity(HttpStatus.FORBIDDEN);

        List<ImageEntity> imageEntities = imageRepository.findAllByOwnIdAndType(id, MainConstants.STATUS_EVENT);

        Map<String, Object> map = new HashMap<>();
        map.put("event", entity);
        map.put("images", imageEntities);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @JsonView(View.IEventsFilter.class)
    @GetMapping("events/filter")
    public ResponseEntity searchFilterEvents() {
        List<EventEntity> list = eventRepository.findTop200ByStatusInOrderByIdDesc(getListStatusShowOfEvents());
        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @JsonView(View.IEvents.class)
    @PostMapping("events/page/{id}/limit/{quantity}")
    public ResponseEntity getListEvents(@PathVariable int id, @PathVariable int quantity,
                                        @RequestBody Map<String, String> body) {
        String status = body.get("status");

        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.NO_CONTENT);
        if (!status.equals(MainConstants.EVENT_ONGOING) && !status.equals(MainConstants.EVENT_CLOSED)
                && !status.equals(MainConstants.EVENT_FINISHED) && !status.equals(MainConstants.GET_ALL))
            return new ResponseEntity(HttpStatus.FORBIDDEN);

        List<EventEntity> list;
        // number of page with n elements
        Pageable pageWithElements = PageRequest.of(id, quantity);

        if (status.equals(MainConstants.GET_ALL)) {
            list = eventRepository.findAllByStatusInOrderByIdDesc(getListStatusShowOfEvents(), pageWithElements);
        } else list = eventRepository.findAllByStatusOrderByIdDesc(status, pageWithElements);

        if (list.size() < 1) return new ResponseEntity(HttpStatus.NOT_FOUND);

        return new ResponseEntity(list, HttpStatus.OK);
    }

    @JsonView(View.IEventDetail.class)
    @PostMapping("user/{id}/event")
    public ResponseEntity createAnEvent(@PathVariable int id, @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(id);

        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.NO_CONTENT);
        if (accountEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_USER) &&
                accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)) {
            EventEntity eventEntity = paramEventEntityRequest(body, new EventEntity());
            eventEntity.setCreatorId(id);
            eventEntity.setCreatedTime(DateTimeUtils.getCurrentTime());
            eventEntity.setTotalRate("0");
            eventEntity.setTotalFeedback(0);
            eventEntity.setTotalSoldTicket(0);
            eventRepository.save(eventEntity);
            return new ResponseEntity(eventEntity.getId(), HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @JsonView(View.IEventDetail.class)
    @PutMapping("user/{userId}/event/{id}")
    public ResponseEntity updateAnEvent(@PathVariable int id, @PathVariable int userId, @RequestBody Map<String, String> body) {
        EventEntity eventEntity = eventRepository.findById(id);
        AccountEntity accountEntity = accountRepository.findById(userId);

        if (accountEntity == null || eventEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (!eventEntity.getCreatorId().equals(userId) || !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_USER))
            return new ResponseEntity(HttpStatus.FORBIDDEN);

        if (eventEntity.getStatus().equals(MainConstants.PENDING) ||
                eventEntity.getStatus().equals(MainConstants.EVENT_WAITING) ||
                eventEntity.getStatus().equals(MainConstants.REJECT)) {
            if (accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)) {
                eventEntity = paramEventEntityRequest(body, eventEntity);
                eventRepository.save(eventEntity);
            }

            return new ResponseEntity(eventEntity, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("admin/{adminId}/event/{id}/status")
    public ResponseEntity changeAnEventStatus(@PathVariable int id, @PathVariable int adminId,
                                              @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(adminId);
        EventEntity eventEntity = eventRepository.findById(id);

        if (accountEntity == null || eventEntity == null
                || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (body == null || body.isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);

        String status = body.get("status");

        if (!status.equals(MainConstants.EVENT_WAITING)) {
            eventEntity.setApprovedId(adminId);
            eventEntity.setApprovedTime(DateTimeUtils.getCurrentTime());
        }

        eventEntity.setStatus(status);
        eventRepository.save(eventEntity);
        return new ResponseEntity(HttpStatus.OK);
    }

    @JsonView(View.IEventsUser.class)
    @PostMapping("user/{id}/events/page/{pageId}/limit/{quantity}")
    public ResponseEntity getListEventsByUserIdNStatus(@PathVariable int id, @PathVariable int quantity,
                                                       @PathVariable int pageId, @RequestBody Map<String, String> body) {
        if (body == null || body.isEmpty()) return new ResponseEntity((HttpStatus.BAD_REQUEST));

        AccountEntity accountEntity = accountRepository.findById(id);
        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        Pageable pageWithElements = PageRequest.of(pageId, quantity);
        List<EventEntity> list;
        String status = body.get("status");
        if (status.equals(MainConstants.GET_ALL))
            list = eventRepository.findAllByCreatorIdOrderByIdDesc(id, pageWithElements);

        else list = eventRepository.findAllByCreatorIdAndStatusOrderByIdDesc(id, pageWithElements, status);

        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        //remove longtitude and latitude
        list.forEach(e -> {
            String location = e.getLocation().split("~")[0];
            e.setLocation(location);
        });

        return new ResponseEntity(list, HttpStatus.OK);
    }


    @JsonView(View.IEventsUser.class)
    @PostMapping("user/profile/{id}/events/page/{pageId}/limit/{quantity}")
    public ResponseEntity getListEventsViewProfile(@PathVariable int id, @PathVariable int quantity,
                                                   @PathVariable int pageId, @RequestBody Map<String, String> body) {
        if (body == null || body.isEmpty()) return new ResponseEntity((HttpStatus.BAD_REQUEST));

        AccountEntity accountEntity = accountRepository.findById(id);
        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        Pageable pageWithElements = PageRequest.of(pageId, quantity);
        List<EventEntity> list;
        String status = body.get("status");

        if (status.equals(MainConstants.GET_ALL)) {
            list = eventRepository.findAllByCreatorIdAndStatusInOrderByIdDesc(id, pageWithElements, getListStatusShowOfEvents());
        } else list = eventRepository.findAllByCreatorIdAndStatusOrderByIdDesc(id, pageWithElements, status);

        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(list, HttpStatus.OK);
    }

    @JsonView(View.IEventDetail.class)
    @PostMapping("admin/{adminId}/event")
    public ResponseEntity createEventByAdmin(@RequestBody Map<String, String> body, @PathVariable int adminId) {
        AccountEntity accountAdminEntity = accountRepository.findById(adminId);

        if (accountAdminEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);


        EventEntity eventEntity = paramEventEntityRequest(body, new EventEntity());

        eventEntity.setCreatedTime(DateTimeUtils.getCurrentTime());
        eventEntity.setStatus(MainConstants.EVENT_WAITING);
        eventEntity.setTotalFeedback(0);
        eventEntity.setTotalSoldTicket(0);

        eventEntity.setTotalRate("0");
        eventEntity.setCreatorId(adminId);
        eventEntity.setApprovedId(adminId);
        eventEntity.setApprovedTime(DateTimeUtils.getCurrentTime());
        eventRepository.save(eventEntity);
        return new ResponseEntity(eventEntity, HttpStatus.OK);
    }

    @JsonView(View.IEventDetail.class)
    @PutMapping("admin/{adminId}/event/{id}")
    public ResponseEntity updateAnEventByAdmin(@PathVariable int id, @PathVariable int adminId,
                                               @RequestBody Map<String, String> body) {
        AccountEntity accountAdminEntity = accountRepository.findById(adminId);
        EventEntity eventEntity = eventRepository.findById(id);

        if (accountAdminEntity == null || eventEntity == null ||
                !eventEntity.getCreatorId().equals(adminId) ||
                !accountAdminEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (!eventEntity.getStatus().equals(MainConstants.PENDING) &&
                !eventEntity.getStatus().equals(MainConstants.REJECT) &&
                !eventEntity.getStatus().equals(MainConstants.EVENT_WAITING))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        eventEntity = paramEventEntityRequest(body, eventEntity);
        eventEntity.setStatus(MainConstants.EVENT_WAITING);
        eventRepository.save(eventEntity);

        return new ResponseEntity(eventEntity, HttpStatus.OK);
    }

    @JsonView(View.IEvents.class)
    @GetMapping("events/category/{cateId}")
    public ResponseEntity searchListEventsName(@PathVariable int cateId) {
        CategoryEntity categoryEntity = categoryRepository.findById(cateId);

        if (categoryEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<EventEntity> events = categoryEntity.getEventsById().stream().collect(Collectors.toList());

        if (events.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(events, HttpStatus.OK);
    }

    @JsonView(View.IEventDetail.class)
    @GetMapping("user/{userId}/event/{id}")
    public ResponseEntity getEventPreview(@PathVariable int userId, @PathVariable int id) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        EventEntity eventEntity = eventRepository.findById(id);

        if (accountEntity == null || eventEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (!accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE) ||
                !eventEntity.getCreatorId().equals(userId))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        Map<String, Object> map = new HashMap<>();
        List<ImageEntity> imageEntities = imageRepository.findAllByOwnIdAndType(id, MainConstants.STATUS_EVENT);

        map.put("event", eventEntity);
        map.put("images", imageEntities);
        return new ResponseEntity(map, HttpStatus.OK);
    }


    @JsonView(View.IEventsAdmin.class)
    @GetMapping("admin/{id}/events/page/{pageId}/limit/{quantity}")
    public ResponseEntity getListEventsOfAdmin(@PathVariable int id, @PathVariable int quantity,
                                               @PathVariable int pageId) {
        AccountEntity accountEntity = accountRepository.findById(id);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        Pageable pageWithElements = PageRequest.of(pageId, quantity);
        List<EventEntity> list = eventRepository.findAllByOrderById(pageWithElements);

        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(list, HttpStatus.OK);
    }

    @JsonView(View.IEvents.class)
    @PostMapping("events/search_distance")
    public ResponseEntity searchDistance5km(@RequestBody Map<String, String> body) {
        double lat = Double.parseDouble(body.get("lat"));
        double lng = Double.parseDouble(body.get("lng"));

        List<EventEntity> eventEntities = eventRepository.findAllByStatusOrderByIdDesc(MainConstants.EVENT_ONGOING);

        if (eventEntities.size() < 1) return new ResponseEntity(HttpStatus.NOT_FOUND);

        double distance = 0;
        List<Map<String, Object>> eventsMatching = new ArrayList<>();
        DistanceMatrixRequestService request = new DistanceMatrixRequestService();

        for (EventEntity item : eventEntities) {
            try {
                double x2 = Double.parseDouble(item.getLocation().split("~")[1]);
                double y2 = Double.parseDouble(item.getLocation().split("~")[2]);
                distance = request.calculateDistanceBetweenPoints(lat, lng, x2, y2);

                if (distance <= MainConstants.COMPARISON_DISTANCE) {//5km
                    Map<String, Object> map = new HashMap<>();
                    String json = request.googleMatrix(lat, lng, x2, y2);
                    JSONObject object = new JSONObject(json);
                    JSONArray dist = (JSONArray) object.get("rows");
                    JSONObject obj = (JSONObject) dist.get(0);
                    dist = (JSONArray) obj.get("elements");
                    obj = (JSONObject) dist.get(0);
                    obj = (JSONObject) obj.get("distance");
                    String tmp = (String) obj.get("text");

                    if (tmp.contains(" km")) {
                        tmp = tmp.replace("km", "")
                                .replace(",", ".");
                        double km = Double.parseDouble(tmp);

                        if ((km / 100) <= MainConstants.COMPARISON_DISTANCE) {
                            map.put("distance", new JSONObject(json).toMap());
                            map.put("event", item);
                            eventsMatching.add(map);
                        }

                    } else if (tmp.contains(" m")) {
                        map.put("distance", new JSONObject(json).toMap());
                        map.put("event", item);
                        eventsMatching.add(map);
                    }
                }
            } catch (Exception e) {
            }
        }
        return new ResponseEntity(eventsMatching, HttpStatus.OK);
    }

    @PostMapping("events/record")
    public ResponseEntity countTotalPage(@RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status.isEmpty() || status == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        int totalRecord = 0;
        if (status.equals(MainConstants.GET_ALL))
            totalRecord = eventRepository.countAllByStatusIn(getListStatusShowOfEvents());
        else totalRecord = eventRepository.countAllByStatus(status);

        return new ResponseEntity(totalRecord, HttpStatus.OK);
    }

    @JsonView(View.IEvents.class)
    @PostMapping("events/search")
    public ResponseEntity searchTitleEvents(@RequestBody Map<String, String> body) {
        String text = body.get("search").trim();
        if (text.isEmpty() || text == "") return new ResponseEntity(HttpStatus.BAD_REQUEST);

        List<EventEntity> list = eventRepository.findAllByStatusInAndTitleContainingIgnoreCase(getListStatusShowOfEvents(), text);
        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(list);
    }

    @JsonView(View.IEventDetail.class)
    @GetMapping("admin/{adminId}/event/{eventId}/preview")
    public ResponseEntity previewEventByAdmin(@PathVariable int adminId, @PathVariable int eventId) {
        AccountEntity accountEntity = accountRepository.findById(adminId);
        EventEntity eventEntity = eventRepository.findById(eventId);
        if (accountEntity == null || eventEntity == null
                || !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        Map<String, Object> map = new HashMap<>();
        List<ImageEntity> imageEntities = imageRepository.findAllByOwnIdAndType(eventId, MainConstants.STATUS_EVENT);
        map.put("event", eventEntity);
        map.put("images", imageEntities);

        return ResponseEntity.ok(map);
    }

    //==========================
    private List<String> getListStatusShowOfEvents() {
        List<String> statusEventShow = new ArrayList<>();
        statusEventShow.add(MainConstants.EVENT_ONGOING);
        statusEventShow.add(MainConstants.EVENT_CLOSED);
        statusEventShow.add(MainConstants.EVENT_FINISHED);
        return statusEventShow;
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
        eventEntity.setStatus(MainConstants.PENDING);
        return eventEntity;
    }

}
