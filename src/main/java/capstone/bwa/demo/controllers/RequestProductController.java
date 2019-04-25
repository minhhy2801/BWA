package capstone.bwa.demo.controllers;


import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.*;
import capstone.bwa.demo.repositories.*;
import capstone.bwa.demo.services.NotificationService;
import capstone.bwa.demo.utils.DateTimeUtils;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RequestProductController {
    @Autowired
    private RequestProductRepository requestProductRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RequestNotificationRepository requestNotificationRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private AccessoryRepository accessoryRepository;
    @Autowired
    private BikeRepository bikeRepository;


    @JsonView(View.INotification.class)
    @PostMapping("user/{id}/request")
    public ResponseEntity createRequest(@PathVariable int id, @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(id);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        //create request product entity
        Map<String, Object> map = new HashMap<>();
        map.put("name", body.get("name"));
        map.put("type", body.get("type"));
        map.put("state", body.get("state"));
        map.put("brand", body.get("brand"));
        if (body.get("type").equals(MainConstants.STATUS_ACCESSORY)) {
            map.put("bikeCompatible", body.get("bikeCompatible"));
        } else {
            map.put("version", body.get("version"));
        }
        map.put("desc", body.get("description"));

        RequestProductEntity requestProductEntity = new RequestProductEntity();
        requestProductEntity.setCreatedTime(DateTimeUtils.getCurrentTime());
        requestProductEntity.setTitle(body.get("title"));
        requestProductEntity.setDescription(new Gson().toJson(map));
        requestProductEntity.setCreatorId(id);
        requestProductEntity.setStatus(MainConstants.REQUEST_FIND);

        //save request
        requestProductRepository.saveAndFlush(requestProductEntity);

        //Run mapping data
        notificationService.searchAfterActionRequest(requestProductEntity, map);

        List<RequestNotificationEntity> listNotis = requestNotificationRepository.findAllByRequestProductId(requestProductEntity.getId());
        if (listNotis.size() > 0) {
            List<Map<String, Object>> result = new ArrayList<>();

            for (RequestNotificationEntity noti : listNotis) {
                Map<String, Object> resObj = new HashMap<>();
                resObj.put("id", noti.getId());
                if (noti.getType().equals(MainConstants.STATUS_SUPPLY_POST)) {
                    //CREATE/UPDATE Request & Supply post
                    resObj.put("supId", noti.getSupplyProductId());
                    resObj.put("supStatus", noti.getSupplyProductBySupplyProductId().getStatus());
                    resObj.put("name", noti.getSupplyProductBySupplyProductId().getTitle());
                    resObj.put("url", noti.getSupplyProductBySupplyProductId().getImgThumbnailUrl());
                    resObj.put("type", MainConstants.STATUS_SUPPLY_POST);

                } else if (noti.getType().startsWith(MainConstants.STATUS_ACCESSORY)
                        || noti.getType().startsWith(MainConstants.STATUS_BIKE)) {
                    // From Crawl data
                    if (noti.getType().startsWith(MainConstants.STATUS_ACCESSORY)) {
                        int accId = Integer.parseInt(noti.getType().split("-")[1]);
                        AccessoryEntity accessoryEntity = accessoryRepository.findById(accId);
                        resObj.put("type", MainConstants.STATUS_ACCESSORY);
                        resObj.put("name", accessoryEntity.getName());
                        resObj.put("link", accessoryEntity.getUrl());
                    }
                    if (noti.getType().startsWith(MainConstants.STATUS_BIKE)) {
                        int bikeId = Integer.parseInt(noti.getType().split("-")[1]);
                        BikeEntity bikeEntity = bikeRepository.findById(bikeId);
                        resObj.put("type", MainConstants.STATUS_BIKE);
                        resObj.put("name", bikeEntity.getName());
                        resObj.put("link", bikeEntity.getUrl());
                    }
                }
                resObj.put("point", noti.getDescription());
                result.add(resObj);
            }

            listNotis.forEach(i -> i.setStatus(MainConstants.NOTI_READ));

            requestNotificationRepository.saveAll(listNotis);

            return new ResponseEntity(result, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("user/{userId}/request/{requestId}/status")
    public ResponseEntity changeStatusRequest(@PathVariable int userId, @PathVariable int requestId) {
        AccountEntity accountEntity = accountRepository.findById(userId);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        RequestProductEntity requestProductEntity = requestProductRepository.findById(requestId);
        if (requestProductEntity != null) {
            String status = requestProductEntity.getStatus();
            switch (status) {
                case MainConstants.REQUEST_FIND:
                    requestProductEntity.setStatus(MainConstants.REQUEST_CLOSE);
                    break;
                case MainConstants.REQUEST_CLOSE:
                    requestProductEntity.setStatus(MainConstants.REQUEST_FIND);
                    break;
            }
            requestProductRepository.save(requestProductEntity);

            return new ResponseEntity(HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @JsonView(View.IRequestProduct.class)
    @GetMapping("user/{userId}/request")
    public ResponseEntity getAllRequest(@PathVariable int userId) {
        AccountEntity accountEntity = accountRepository.findById(userId);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<RequestProductEntity> requests = requestProductRepository.findAllByCreatorId(userId);
        List<Map<Object, Object>> list = new ArrayList<>();
        for (RequestProductEntity item : requests) {
            Map<Object, Object> map = new HashMap<>();
            map.put("desc", new JSONObject(item.getDescription()).toMap());
            map.put("request", item);
            list.add(map);
        }
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @JsonView(View.IRequestProduct.class)
    @GetMapping("user/{userId}/request/{requestId}")
    public ResponseEntity getSpecificRequestById(@PathVariable int userId, @PathVariable int requestId) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        RequestProductEntity req = requestProductRepository.findById(requestId);

        if (accountEntity == null || req == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (req != null && req.getCreatorId().equals(userId)) {
            return new ResponseEntity(req, HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("user/{userId}/request/{requestId}")
    public ResponseEntity updateRequest(@PathVariable int userId, @PathVariable int requestId,
                                        @RequestBody Map<String, String> body) {

        AccountEntity accountEntity = accountRepository.findById(userId);
        RequestProductEntity requestProductEntity = requestProductRepository.findById(requestId);

        if (accountEntity == null || requestProductEntity == null
                || !requestProductEntity.getStatus().equals(MainConstants.REQUEST_FIND)
                || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        //create request product entity
        Map<String, Object> map = new HashMap<>();
        map.put("name", body.get("name"));
        map.put("type", body.get("type"));
        map.put("state", body.get("state"));
        map.put("brand", body.get("brand"));
        if (body.get("type").equals(MainConstants.STATUS_ACCESSORY)) {
            map.put("bikeCompatible", body.get("bikeCompatible"));
        } else {
            map.put("version", body.get("version"));
        }
        map.put("desc", body.get("description"));

        requestProductEntity.setTitle(body.get("title"));
        requestProductEntity.setDescription(new Gson().toJson(map));
        requestProductEntity.setEditedTime(DateTimeUtils.getCurrentTime());
        requestProductRepository.save(requestProductEntity);
        List<RequestNotificationEntity> notis = requestNotificationRepository.findAllByRequestProductId(requestProductEntity.getId());
        if (notis.size() > 0)
            requestNotificationRepository.deleteAll(notis);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    notificationService.searchAfterActionRequest(requestProductEntity, map);
                    Thread.currentThread().interrupted();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
        return new ResponseEntity(HttpStatus.OK);

    }

}
