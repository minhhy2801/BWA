package capstone.bwa.demo.controllers;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.*;
import capstone.bwa.demo.repositories.*;
import capstone.bwa.demo.services.DistanceMatrixRequestService;
import capstone.bwa.demo.services.NotificationService;
import capstone.bwa.demo.utils.DateTimeUtils;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import sun.applet.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class SupplyProductController {
    @Autowired
    private SupplyProductRepository supplyProductRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccessoryRepository accessoryRepository;
    @Autowired
    private BikeRepository bikeRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private TransactionDetailRepository transactionDetailRepository;
    @Autowired
    private RequestNotificationRepository requestNotificationRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @JsonView(View.ISupplyPosts.class)
    @PostMapping("supply_posts/page/{id}/limit/{quantity}")
    public ResponseEntity getListSupplyPosts(@PathVariable int quantity, @PathVariable int id,
                                             @RequestBody Map<String, String> body) {
        String status = body.get("status");
        String cate = body.get("category");

        if (!status.equals(MainConstants.SUPPLY_POST_PUBLIC)
                && !status.equals(MainConstants.SUPPLY_POST_CLOSED)
                && !status.equals(MainConstants.GET_ALL))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        List<SupplyProductEntity> list = new ArrayList<>();
        // number of page with n elements
        Pageable pageWithElements = PageRequest.of(id, quantity);
        if (status.equals(MainConstants.GET_ALL) && cate.equals(MainConstants.GET_ALL))
            list = supplyProductRepository.findAllByStatusInOrderByIdDesc(getListStatusShowOfSupplyPost(), pageWithElements);

        else if (!status.equals(MainConstants.GET_ALL) && cate.equals(MainConstants.GET_ALL))
            list = supplyProductRepository.findAllByStatusOrderByIdDesc(status, pageWithElements);

        if (!cate.equals(MainConstants.GET_ALL)) {
            CategoryEntity category = categoryRepository.findByNameIgnoreCaseAndType(cate, MainConstants.STATUS_SUPPLY_POST);

            if (category == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

            if (category.getName().equalsIgnoreCase("bán xe")
                    || category.getName().equalsIgnoreCase("bán phụ kiện")) {
                if (status.equals(MainConstants.GET_ALL))
                    list = supplyProductRepository.findAllByCategoryIdAndStatusInOrderByIdDesc(category.getId(), pageWithElements, getListStatusShowOfSupplyPost());
                else if (!status.equals(MainConstants.GET_ALL))
                    list = supplyProductRepository.findAllByStatusAndCategoryIdOrderByIdDesc(status, category.getId(), pageWithElements);
            }
        }

        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(list, HttpStatus.OK);
    }

    @JsonView({View.ISupplyPostDetail.class})
    @GetMapping("supply_post/{id}")
    public ResponseEntity getSupplyPost(@PathVariable int id) {
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(id);

        if (supplyProductEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (!supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_PUBLIC) &&
                !supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_CLOSED))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        List<ImageEntity> imageEntities = imageRepository.findAllBySupplyProductByOwnId_IdAndType(id, MainConstants.STATUS_SUPPLY_POST);

        Map<String, Object> map = new HashMap<>();

        //determine category name
        String cateName = supplyProductEntity.getTypeItem();
        switch (cateName) {
            case MainConstants.STATUS_BIKE:
                int bikeId = supplyProductEntity.getItemId();
                BikeEntity bikeEntity = bikeRepository.findById(bikeId);
                map.put("bike", bikeEntity);
                break;
            case MainConstants.STATUS_ACCESSORY:
                int accessoryId = supplyProductEntity.getItemId();
                AccessoryEntity accessoryEntity = accessoryRepository.findById(accessoryId);
                map.put("accessory", accessoryEntity);
                break;
        }
        map.put("supply_post", supplyProductEntity);
        map.put("images", imageEntities);

        if (supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_CLOSED)) {
            TransactionDetailEntity successTrans = transactionDetailRepository.findBySupplyProductIdAndStatus(id, MainConstants.TRANSACTION_SUCCESS);
            if (successTrans == null) map.put("success_account", null);
            else map.put("success_account", successTrans.getInteractiveId());
        }
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @JsonView(View.ISupplyPostDetail.class)
    @PostMapping("user/{id}/supply_post_bike")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseEntity createSupplyPostBike(@PathVariable int id, @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(id);

        if (accountEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (!accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE) ||
                !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_USER))
            return new ResponseEntity(HttpStatus.LOCKED);

        SupplyProductEntity supplyProductEntity = new SupplyProductEntity();

        BikeEntity bikeEntity = new BikeEntity();
        supplyProductEntity = paramSupplyPostEntityRequest(body, supplyProductEntity, bikeEntity, null, MainConstants.STATUS_BIKE);
        supplyProductEntity.setCreatorId(id);
        supplyProductEntity.setCreatedTime(DateTimeUtils.getCurrentTime());
        supplyProductRepository.save(supplyProductEntity);

        return new ResponseEntity(supplyProductEntity, HttpStatus.OK);
    }

    @JsonView(View.ISupplyPostDetail.class)
    @PostMapping("user/{id}/supply_post_accessory")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseEntity createSupplyPostAccessory(@PathVariable int id, @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(id);
        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        SupplyProductEntity supplyProductEntity = new SupplyProductEntity();
        AccessoryEntity accessoryEntity = new AccessoryEntity();
        supplyProductEntity = paramSupplyPostEntityRequest(body, supplyProductEntity, null,
                accessoryEntity, MainConstants.STATUS_ACCESSORY);

        supplyProductEntity.setCreatorId(id);
        supplyProductEntity.setCreatedTime(DateTimeUtils.getCurrentTime());

        supplyProductRepository.save(supplyProductEntity);

        return new ResponseEntity(supplyProductEntity, HttpStatus.OK);
    }

    @JsonView(View.ISupplyPostDetail.class)
    @PutMapping("user/{userId}/supply_post_accessory/{id}")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseEntity updateSupplyPostAccessoryByUser(@PathVariable int id, @PathVariable int userId,
                                                          @RequestBody Map<String, String> body) {

        AccountEntity accountEntity = accountRepository.findById(userId);
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(id);

        if (accountEntity == null || supplyProductEntity == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (!accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE) ||
                !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_USER) ||
                supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_CLOSED) ||
                !supplyProductEntity.getTypeItem().equals(MainConstants.STATUS_ACCESSORY))
            return new ResponseEntity(HttpStatus.LOCKED);

        if (supplyProductEntity.getCreatorId().equals(userId)) {
            int accessoryId = supplyProductEntity.getItemId();
            AccessoryEntity accessoryEntity = accessoryRepository.findById(accessoryId);
            supplyProductEntity = paramSupplyPostEntityRequest(body, supplyProductEntity, null, accessoryEntity,
                    MainConstants.STATUS_ACCESSORY);
            supplyProductRepository.save(supplyProductEntity);

            //make all transaction to be forzen when users update supply post

            frozenAllTransactionBySupplyProductId(id, MainConstants.TRANSACTION_FROZEN);

            return new ResponseEntity(supplyProductEntity, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


    @JsonView(View.ISupplyPostDetail.class)
    @PutMapping("user/{userId}/supply_post_bike/{id}")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseEntity updateSupplyPostBikeByUser(@PathVariable int id, @PathVariable int userId,
                                                     @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(id);

        if (accountEntity == null || supplyProductEntity == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (!accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE) ||
                !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_USER) ||
                supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_CLOSED) ||
                !supplyProductEntity.getTypeItem().equals(MainConstants.STATUS_BIKE))
            return new ResponseEntity(HttpStatus.LOCKED);

        if (supplyProductEntity.getCreatorId().equals(userId)) {

            int bikeId = supplyProductEntity.getItemId();
            BikeEntity bikeEntity = bikeRepository.findById(bikeId);
            supplyProductEntity = paramSupplyPostEntityRequest(body, supplyProductEntity, bikeEntity,
                    null, MainConstants.STATUS_BIKE);

            supplyProductRepository.save(supplyProductEntity);

            //make all transaction to be forzen when users update supply post
            frozenAllTransactionBySupplyProductId(id, MainConstants.TRANSACTION_FROZEN);

            return new ResponseEntity(supplyProductEntity, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("admin/{adminId}/supply_post/{id}")
    public ResponseEntity changeStatusSupplyPostByAdmin(@PathVariable int id, @PathVariable int adminId,
                                                        @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(adminId);
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(id);

        if (accountEntity == null || supplyProductEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (!accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE) ||
                !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.LOCKED);

        String status = body.get("status");

        //process for notification
        if (status.equalsIgnoreCase(MainConstants.SUPPLY_POST_PUBLIC)) {
            //process for notification
            startSearchingForNotification(supplyProductEntity);

            //send noti to FROZEN transaction when admin approve
            if (transactionDetailRepository.existsDistinctBySupplyProductId(id)) {
                if (supplyProductEntity.getStatus().equalsIgnoreCase(MainConstants.HIDDEN)){
                    frozenAllTransactionBySupplyProductId(id, MainConstants.TRANSACTION_FROZEN);

                }
                sendNotiToFrozenTransaction(supplyProductEntity.getId());
            }
        }

        //send noti to HIDDEN supply
        if (status.equalsIgnoreCase(MainConstants.HIDDEN)) {
            frozenAllTransactionBySupplyProductId(id, MainConstants.HIDDEN);
            sendNotiToFrozenTransaction(id);
        }

        supplyProductEntity.setApprovedId(adminId);
        supplyProductEntity.setApprovedTime(DateTimeUtils.getCurrentTime());
        supplyProductEntity.setStatus(status);
        supplyProductRepository.save(supplyProductEntity);
        return new ResponseEntity(HttpStatus.OK);
    }

    @JsonView(View.ISupplyPosts.class)
    @GetMapping("user/{userId}/supply_posts")
    public ResponseEntity getListSupplyPostsByUser(@PathVariable int userId) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        if (!accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE) || accountEntity == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<SupplyProductEntity> list = supplyProductRepository.findAllByCreatorIdOrderByIdDesc(userId);

        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(list, HttpStatus.OK);
    }

    @JsonView(View.ISupplyPostsAdmin.class)
    @GetMapping("admin/{adminId}/list_supply_posts")
    public ResponseEntity getListSupplyPostsByAdmin(@PathVariable int adminId) {
        AccountEntity accountEntity = accountRepository.findById(adminId);
        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE) ||
                !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.LOCKED);

        List<SupplyProductEntity> supplyProductEntities = supplyProductRepository.findAllByOrderByIdDesc();

        if (supplyProductEntities.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(supplyProductEntities, HttpStatus.OK);
    }

    @JsonView({View.ISupplyPostDetail.class})
    @GetMapping("user/{userId}/supply_post/{id}")
    public ResponseEntity getSupplyPostPreview(@PathVariable int id, @PathVariable int userId) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(id);

        if (accountEntity == null || supplyProductEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (!accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE) ||
                !supplyProductEntity.getCreatorId().equals(userId))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        Map<String, Object> map = new HashMap<>();
        String typeItem = supplyProductEntity.getTypeItem();
        List<ImageEntity> imageEntities = imageRepository.findAllBySupplyProductByOwnId_IdAndType(id, MainConstants.STATUS_SUPPLY_POST);

        switch (typeItem) {
            case MainConstants.STATUS_BIKE:
                int bikeId = supplyProductEntity.getItemId();
                BikeEntity bikeEntity = bikeRepository.findById(bikeId);
                map.put("bike", bikeEntity);
                break;
            case MainConstants.STATUS_ACCESSORY:
                int accessoryId = supplyProductEntity.getItemId();
                AccessoryEntity accessoryEntity = accessoryRepository.findById(accessoryId);
                map.put("accessory", accessoryEntity);
        }

        map.put("supply_post", supplyProductEntity);
        map.put("images", imageEntities);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @PutMapping("user/{userId}/supply_post/{id}/close")
    public ResponseEntity closeSupplyPostByUser(@PathVariable int userId, @PathVariable int id) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(id);
        if (accountEntity == null || supplyProductEntity == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (!accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE) || !supplyProductEntity.getCreatorId().equals(userId))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        supplyProductEntity.setStatus(MainConstants.SUPPLY_POST_CLOSED);
        supplyProductRepository.save(supplyProductEntity);

        return new ResponseEntity((HttpStatus.OK));
    }

    @JsonView(View.ISupplyPosts.class)
    @PostMapping("supply_post/search_distance")
    public ResponseEntity searchDistance5km(@RequestBody Map<String, String> body) {
        double lat = Double.parseDouble(body.get("lat"));
        double lng = Double.parseDouble(body.get("lng"));
        List<SupplyProductEntity> productEntities = supplyProductRepository.findAllByStatusOrderByIdDesc(MainConstants.SUPPLY_POST_PUBLIC);
        if (productEntities.size() < 1) return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<Map<String, Object>> supplyMatching = new ArrayList<>();
        double distance = 0;

        DistanceMatrixRequestService request = new DistanceMatrixRequestService();
        for (SupplyProductEntity item : productEntities) {
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
                            map.put("supply_post", item);
                            map.put("distance", new JSONObject(json).toMap());
                            supplyMatching.add(map);
                        }
                    } else if (tmp.contains(" m")) {
                        map.put("supply_post", item);
                        map.put("distance", new JSONObject(json).toMap());
                        supplyMatching.add(map);
                    }

                }
            } catch (Exception e) {
            }
        }

        return new ResponseEntity(supplyMatching, HttpStatus.OK);
    }

    @JsonView(View.ISupplyPostsFilter.class)
    @GetMapping("supply_posts/search_filter")
    public ResponseEntity searchFilterSupplyPosts() {
        List<SupplyProductEntity> list = supplyProductRepository.findTop200ByStatusInOrderByIdDesc(getListStatusShowOfSupplyPost());
        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(list);
    }

    @PostMapping("supply_posts/record")
    public ResponseEntity countTotalPage(@RequestBody Map<String, String> body) {
        String status = body.get("status");
        String cate = body.get("category");

        if (!status.equals(MainConstants.SUPPLY_POST_PUBLIC) && !status.equals(MainConstants.SUPPLY_POST_CLOSED)
                && !status.equals(MainConstants.GET_ALL))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        int totalRecord = 0;

        if (status.equals(MainConstants.GET_ALL) && cate.equals(MainConstants.GET_ALL))
            totalRecord = supplyProductRepository.countAllByStatusIn(getListStatusShowOfSupplyPost());

        else if (!status.equals(MainConstants.GET_ALL) && cate.equals(MainConstants.GET_ALL))
            totalRecord = supplyProductRepository.countAllByStatus(status);

        if (!cate.equals(MainConstants.GET_ALL)) {
            CategoryEntity category = categoryRepository.findByNameIgnoreCaseAndType(cate, MainConstants.STATUS_SUPPLY_POST);

            if (category == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

            if (category.getName().equalsIgnoreCase("bán xe")
                    || category.getName().equalsIgnoreCase("bán phụ kiện")) {
                if (status.equals(MainConstants.GET_ALL))
                    totalRecord = supplyProductRepository.countAllByCategoryId(category.getId());
                else totalRecord = supplyProductRepository.countAllByCategoryIdAndStatus(category.getId(), status);
            }
        }

        return new ResponseEntity(totalRecord, HttpStatus.OK);
    }

    @JsonView(View.ISupplyPosts.class)
    @PostMapping("supply_posts/search")
    public ResponseEntity searchTitleNews(@RequestBody Map<String, String> body) {
        String text = body.get("search").trim();
        if (text.isEmpty() || text == "") return new ResponseEntity(HttpStatus.BAD_REQUEST);

        List<SupplyProductEntity> list = supplyProductRepository.findAllByStatusInAndTitleContainingIgnoreCase(getListStatusShowOfSupplyPost(), text);

        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return ResponseEntity.ok(list);
    }

    @JsonView(View.ISupplyPostDetail.class)
    @GetMapping("admin/{adminId}/supply_post/{supProId}/preview")
    public ResponseEntity previewSupplyPostByAdmin(@PathVariable int adminId, @PathVariable int supProId) {
        AccountEntity accountEntity = accountRepository.findById(adminId);
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(supProId);

        if (accountEntity == null || supplyProductEntity == null || !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        String typeItem = supplyProductEntity.getTypeItem();
        List<ImageEntity> imageEntities = imageRepository.findAllBySupplyProductByOwnId_IdAndType(supProId, MainConstants.STATUS_SUPPLY_POST);

        Map<String, Object> map = new HashMap<>();
        switch (typeItem) {
            case MainConstants.STATUS_BIKE:
                int bikeId = supplyProductEntity.getItemId();
                BikeEntity bikeEntity = bikeRepository.findById(bikeId);
                map.put("bike", bikeEntity);
                break;
            case MainConstants.STATUS_ACCESSORY:
                int accessoryId = supplyProductEntity.getItemId();
                AccessoryEntity accessoryEntity = accessoryRepository.findById(accessoryId);
                map.put("accessory", accessoryEntity);
        }
        map.put("supply_post", supplyProductEntity);
        map.put("images", imageEntities);

        return ResponseEntity.ok(map);
    }

    //==========================
    private List<String> getListStatusShowOfSupplyPost() {
        List<String> status = new ArrayList<>();
        status.add(MainConstants.SUPPLY_POST_CLOSED);
        status.add(MainConstants.SUPPLY_POST_PUBLIC);
        return status;
    }

    private SupplyProductEntity paramSupplyPostEntityRequest(Map<String, String> body,
                                                             SupplyProductEntity supplyProductEntity,
                                                             BikeEntity bike, AccessoryEntity accessory,
                                                             String type) {
        String title = body.get("title");
        String description = body.get("description");
        String imgThumbnail = body.get("imgThumbnailUrl");
        String location = body.get("location");
        int cateId = Integer.parseInt(body.get("categoryIdSupplyPost"));

        supplyProductEntity.setTitle(title);
        supplyProductEntity.setDescription(description);
        supplyProductEntity.setImgThumbnailUrl(imgThumbnail);
        supplyProductEntity.setLocation(location);
        supplyProductEntity.setCategoryId(cateId);
        supplyProductEntity.setStatus(MainConstants.PENDING);
        supplyProductEntity.setRate("0");
        supplyProductEntity.setTypeItem(type);
        if (type.equals(MainConstants.STATUS_BIKE)) {
            BikeEntity bikeEntity = paramBikeEntityRequest(body, bike);
            bikeRepository.save(bikeEntity);
            supplyProductEntity.setItemId(bikeEntity.getId());
        } else if (type.equals(MainConstants.STATUS_ACCESSORY)) {
            AccessoryEntity accessoryEntity = paramAccessoryEntityRequest(body, accessory);
            accessoryRepository.save(accessoryEntity);
            supplyProductEntity.setItemId(accessoryEntity.getId());
        }

        return supplyProductEntity;
    }

    private BikeEntity paramBikeEntityRequest(Map<String, String> body, BikeEntity bikeEntity) {
        String name = body.get("name");
        String brand = body.get("brand");
        String price = body.get("price");
        int cateId = Integer.parseInt(body.get("categoryIdBike"));
        String version = body.get("version");

        //set description
        Map<String, String> map = new HashMap<>();
        map.put("numOfKms", body.get("numOfKms"));
        map.put("stateVehicle", body.get("stateVehicle"));
        map.put("color", body.get("color"));
//        map.put("yearOfBike", body.get("yearOfBike"));
        map.put("state", body.get("state"));
        //----
        bikeEntity.setName(name);
        bikeEntity.setBrand(brand);
        bikeEntity.setPrice(price);
        bikeEntity.setStatus(MainConstants.STATUS_SUPPLY_POST);
        bikeEntity.setHashBikeCode(bikeEntity.hashCode() + "");
        bikeEntity.setCategoryId(cateId);
        bikeEntity.setVersion(version);
        Gson gson = new Gson();
        String desc = gson.toJson(map);
        bikeEntity.setDescription(desc);

        return bikeEntity;
    }


    private AccessoryEntity paramAccessoryEntityRequest(Map<String, String> body, AccessoryEntity accessoryEntity) {
        String name = body.get("name");
        String brand = body.get("brand");
        String price = body.get("price");
        int cateId = Integer.parseInt(body.get("categoryIdAccessory"));

        Map<String, String> map = new HashMap<>();
        map.put("feature", body.get("feature"));
        map.put("bikeCompatible", body.get("bikeCompatible"));
        map.put("state", body.get("state"));
        //----
        accessoryEntity.setName(name);
        accessoryEntity.setBrand(brand);
        accessoryEntity.setPrice(price);
        accessoryEntity.setStatus(MainConstants.STATUS_SUPPLY_POST);
        accessoryEntity.setHashAccessoryCode(accessoryEntity.hashCode() + "");
        accessoryEntity.setCategoryId(cateId);
        Gson gson = new Gson();
        String desc = gson.toJson(map);
        accessoryEntity.setDescription(desc);

        return accessoryEntity;
    }


    //=========================

    private void frozenAllTransactionBySupplyProductId(int supplyProductId, String status) {

        List<TransactionDetailEntity> transactions = transactionDetailRepository.
                findAllBySupplyProductId((supplyProductId));
        if (transactions.size() > 0) {
            transactions.forEach(t -> t.setStatus(status));
            transactionDetailRepository.saveAll(transactions);
        }
    }

    private void startSearchingForNotification(SupplyProductEntity supplyProductEntity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                notificationService.searchRequestAfterActionSupplyPost(supplyProductEntity);
                Thread.currentThread().interrupted();
            }
        }).start();
    }


    private void sendNotiToFrozenTransaction(int supplyPostId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TransactionDetailEntity> transactions = transactionDetailRepository.findAllBySupplyProductId(supplyPostId);
                List<RequestNotificationEntity> notis = new ArrayList<>();
                System.out.println("TRANS " + transactions.size());

                for (TransactionDetailEntity item : transactions) {
                    RequestNotificationEntity noti = new RequestNotificationEntity();
                    noti.setStatus(MainConstants.NOTI_NEW);
                    noti.setSupplyProductId(item.getSupplyProductId());
                    noti.setTransactionId(item.getId());
                    System.out.println("TRANS ID " + item.getId());
                    noti.setType(MainConstants.STATUS_TRANS);
                    notis.add(noti);
                }

                System.out.println("NOTI======== " + notis.size());
                requestNotificationRepository.saveAll(notis);
                Thread.currentThread().interrupted();
            }
        }).start();
    }

}

