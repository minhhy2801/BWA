package capstone.bwa.demo.controllers;


import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.*;
import capstone.bwa.demo.repositories.*;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RequestNotificationController {
    @Autowired
    private RequestNotificationRepository requestNotificationRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RequestProductRepository requestProductRepository;

    @Autowired
    private AccessoryRepository accessoryRepository;

    @Autowired
    private BikeRepository bikeRepository;

    @JsonView({View.INotification.class})
    @PutMapping("user/{userId}/notification/{notiId}")
    public ResponseEntity updateStatusNotification(@PathVariable int userId, @PathVariable int notiId) {
        AccountEntity accountEntity = accountRepository.findById(userId);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        RequestNotificationEntity noti = requestNotificationRepository.findById(notiId);

        if (noti != null) {
            noti.setStatus(MainConstants.NOTI_READ);
            requestNotificationRepository.save(noti);
            return new ResponseEntity(noti, HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    //    @JsonView({View.INotification.class})
    @GetMapping("user/{userId}/request/{requestId}/notification")
    public ResponseEntity getAllNotificationsByRequest(@PathVariable int userId, @PathVariable int requestId) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        RequestProductEntity req = requestProductRepository.findById(requestId);

        if (accountEntity == null || req == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (!req.getCreatorId().equals(userId)) return new ResponseEntity(HttpStatus.BAD_REQUEST);

        List<RequestNotificationEntity> notis = requestNotificationRepository.findAllByRequestProductId(requestId);
        if (notis.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        List<Map<String, Object>> listNoti = new ArrayList<>();
        for (RequestNotificationEntity noti : notis) {
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
                    int id = Integer.parseInt(noti.getType().split("-")[1]);
                    AccessoryEntity accessoryEntity = accessoryRepository.findById(id);
                    resObj.put("type", MainConstants.STATUS_ACCESSORY);
                    resObj.put("name", accessoryEntity.getName());
                    resObj.put("link", accessoryEntity.getUrl());
                }
                if (noti.getType().startsWith(MainConstants.STATUS_BIKE)) {
                    int id = Integer.parseInt(noti.getType().split("-")[1]);
                    BikeEntity bikeEntity = bikeRepository.findById(id);
                    resObj.put("type", MainConstants.STATUS_BIKE);
                    resObj.put("name", bikeEntity.getName());
                    resObj.put("link", bikeEntity.getUrl());
                }
            }
            resObj.put("point", noti.getDescription());
            listNoti.add(resObj);
        }
        return new ResponseEntity(listNoti, HttpStatus.OK);
    }
}
