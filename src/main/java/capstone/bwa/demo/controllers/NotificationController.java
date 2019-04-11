package capstone.bwa.demo.controllers;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.AccessoryEntity;
import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.RequestNotificationEntity;
import capstone.bwa.demo.repositories.*;
import capstone.bwa.demo.viewmodels.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

@RestController
@RequestMapping("notification")
public class NotificationController {

    @Autowired
    private RequestNotificationRepository requestNotificationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccessoryRepository accessoryRepository;

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private RequestProductRepository requestProductRepository;

    //    @Async
    @Transactional
    @GetMapping(produces = "text/event-stream", value = "{uid}")
    public Flux<Notification> getNotification(@PathVariable int uid) {
        // Flux<Long> interval = Flux.interval(Duration.ofSeconds(MainConstants.TIME_SSE_NOTI));

        AccountEntity accountEntity = accountRepository.findById(uid);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return null;
        Stream<Notification> stream = Stream.generate(() -> {
//            List<RequestNotificationEntity> notis =requestNotificationRepository.findAllByRequestProductByRequestProductId_CreatorIdAndStatus(uid, MainConstants.NOTI_NEW);
            List<Integer> ids = requestProductRepository.findAllIdsByCreatorIdAndStatus(MainConstants.REQUEST_FIND, uid);
            System.out.println(uid + "     aa   " + ids);
            List<RequestNotificationEntity> notis = requestNotificationRepository.findAllByRequestProductIdInAndStatus(ids, MainConstants.NOTI_NEW);
            List<Map<String, Object>> listNoti = new ArrayList<>();
            if (notis.size() > 0) {
                for (RequestNotificationEntity noti : notis) {
                    Map<String, Object> resObj = new HashMap<>();
                    resObj.put("id", noti.getId());
                    if (noti.getType().equals(MainConstants.STATUS_SUPPLY_POST)) {
                        //CREATE/UPDATE Request & Supply post
                        resObj.put("supId", noti.getSupplyProductId());
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
                            resObj.put("type", MainConstants.STATUS_BIKE);
                            resObj.put("name", bikeRepository.findById(id).getName());
                            resObj.put("link", bikeRepository.findById(id).getUrl());
                        }
                    }
                    listNoti.add(resObj);
                }
            }
            List<RequestNotificationEntity> listNotiTran = requestNotificationRepository
                    .findAllByTypeAndStatusAndTransactionByTransactionId_InteractiveId(
                            MainConstants.STATUS_TRANS, MainConstants.NOTI_NEW, uid);
//            System.out.println("size" + listNotiTran.size());
            if (listNotiTran.size() > 0) {
                for (RequestNotificationEntity noti : listNotiTran) {
                    Map<String, Object> resObj = new HashMap<>();
//                    System.out.println("1111111111");
                    resObj.put("id", noti.getId());
                    resObj.put("supId", noti.getSupplyProductId());
                    resObj.put("type", MainConstants.STATUS_TRANS);
                    resObj.put("name", noti.getSupplyProductBySupplyProductId().getTitle());
                    resObj.put("url", noti.getSupplyProductBySupplyProductId().getImgThumbnailUrl());
                    listNoti.add(resObj);
                }
            }
            return new Notification(uid, new Date(), listNoti);
        });
        Flux<Notification> notificationFlux = Flux.fromStream(stream)
                .delayElements(Duration.ofMillis(MainConstants.TIME_SSE_NOTI))
//                .timeout(Duration.ofHours(2))
                .doFinally(signalType -> {
                    stream.close();
                });
        return notificationFlux;
    }


    @PutMapping("{id}")
    public ResponseEntity updateStatusNotification(@PathVariable int id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        RequestNotificationEntity req = requestNotificationRepository.findById(id);
        if (req != null) {
            req.setStatus(status);
            requestNotificationRepository.saveAndFlush(req);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


}
