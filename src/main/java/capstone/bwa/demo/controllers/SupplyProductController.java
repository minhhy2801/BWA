package capstone.bwa.demo.controllers;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.BikeEntity;
import capstone.bwa.demo.entities.ImageEntity;
import capstone.bwa.demo.entities.SupplyProductEntity;
import capstone.bwa.demo.repositories.*;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
public class SupplyProductController {
    @Autowired
    private SupplyProductRepository supplyProductRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private BikeRepository bikeRepository;
    @Autowired
    private ImageRepository imageRepository;

    /**
     * Return list supply posts
     * status send in body
     *
     * @return 404 if not found
     * 200 if OK
     * @apiNote {
     * <p>
     * }
     */
    @JsonView(View.ISupplyPosts.class)
    @PostMapping("supply_posts/page/{id}/limit/{quantity}")
    public ResponseEntity getListSupplyPosts(@PathVariable int quantity, @PathVariable int id,
                                             @RequestBody Map<String, String> body) {
        String status = body.get("status");

        if (!status.equals(MainConstants.SUPPLY_POST_PUBLIC) && !status.equals(MainConstants.SUPPLY_POST_CLOSED)
                && !status.equals(MainConstants.GET_ALL)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        List<SupplyProductEntity> list;
        // number of page with n elements
        Pageable pageWithElements = PageRequest.of(id, quantity);

        if (status.equals(MainConstants.GET_ALL)) {
            List<String> statusSupplyPost = new ArrayList<>();
            statusSupplyPost.add(MainConstants.SUPPLY_POST_CLOSED);
            statusSupplyPost.add(MainConstants.SUPPLY_POST_PUBLIC);
            list = supplyProductRepository.findAllByStatusInOrderByIdDesc(statusSupplyPost, pageWithElements);
        } else list = supplyProductRepository.findAllByStatusOrderByIdDesc(status, pageWithElements);
        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);
        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * Return supply post
     * check status
     *
     * @param id
     * @return 404 if not found
     * 200 if OK
     * @apiNote {
     * <p>
     * }
     */
    @JsonView(View.ISupplyPostDetail.class)
    @GetMapping("supply_post/{id}")
    public ResponseEntity getSupplyPost(@PathVariable int id) {
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(id);
        if (supplyProductEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (!supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_PUBLIC) &&
                !supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_CLOSED))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        List<ImageEntity> imageEntities = imageRepository.findAllBySupplyProductByOwnId_IdAndType(id, MainConstants.STATUS_SUPPLY_POST);

        Map<String, Object> map = new HashMap<>();
        map.put("supply_post", supplyProductEntity);
        map.put("images", imageEntities);

        return new ResponseEntity(map, HttpStatus.OK);
    }

    /**
     * Return new supply post with status PENDING
     *
     * @param id
     * @param body
     * @return 403 if cannot create
     * 200 if OK
     */
    @JsonView(View.ISupplyPostDetail.class)
    @PostMapping("user/{id}/supply_post_bike")
    public ResponseEntity createSupplyPostBike(@PathVariable int id, @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(id);
        if (accountEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (!accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE) ||
                !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_USER))
            return new ResponseEntity(HttpStatus.LOCKED);
        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        SupplyProductEntity supplyProductEntity = paramSupplyPostEntityRequest(body);
        supplyProductEntity.setCreatedTime(dateFormat.format(date));
        supplyProductEntity.setStatus(MainConstants.PENDING);
        supplyProductRepository.save(supplyProductEntity);
        return new ResponseEntity(supplyProductEntity, HttpStatus.OK);
    }

    /**
     * Return update obj or close supply post by user
     * after update status will PENDING
     *
     * @param id
     * @param userId
     * @param body
     * @return 403 if cannot create
     * 404 if found
     * 200 if OK
     */
    @PutMapping("user/{userId}/supply_post/{id}")
    public ResponseEntity updateSupplyPostByUser(@PathVariable int id, @PathVariable int userId,
                                                 @RequestBody Map<String, String> body) {

        return null;
    }

    /**
     * @param id
     * @param adminId
     * @param body
     * @return change status any supply post
     */
    @PutMapping("admin/{adminId}/supply_post/{id}")
    public ResponseEntity changeStatusSupplyPostByAdmin(@PathVariable int id, @PathVariable int adminId,
                                                        @RequestBody Map<String, String> body) {
        return null;
    }

    /**
     * status send in body
     * if status ALL -> get all supply post sort desc without status
     *
     * @param userId
     * @param id
     * @param quantity
     * @param body
     * @return list supply posts base on userId
     */
    @GetMapping("user/{userId}/supply_posts/page/{id}/limit/{quantity}")
    public ResponseEntity getListSupplyPostsByUser(@PathVariable int userId, @PathVariable int id,
                                                   @PathVariable int quantity, @RequestBody Map<String, String> body) {


        return null;
    }

    /**
     * status send in body
     *
     * @param adminId
     * @param id
     * @param quantity
     * @param body
     * @return list supply posts
     **/

    @GetMapping("admin/{adminId}/supply_posts/page/{id}/limit/{quantity}")
    public ResponseEntity getListSupplyPostsByAdmin(@PathVariable int adminId, @PathVariable int id,
                                                    @PathVariable int quantity, @RequestBody Map<String, String> body) {

        return null;
    }

    //==========================
    private SupplyProductEntity paramSupplyPostEntityRequest(Map<String, String> body) {
        SupplyProductEntity supplyProductEntity = new SupplyProductEntity();
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
        BikeEntity bikeEntity = paramBikeEntityRequest(body);
        bikeRepository.save(bikeEntity);
        supplyProductEntity.setItemId(bikeEntity.getId());

        return supplyProductEntity;
    }

    private BikeEntity paramBikeEntityRequest(Map<String, String> body) {
        BikeEntity bikeEntity = new BikeEntity();
        String name = body.get("name");
        String brand = body.get("brand");
        String price = body.get("price");
        int cateId = Integer.parseInt(body.get("categoryIdBike"));
        String version = body.get("version");
        String img = body.get("images"); //arrays images

        //set description
        Map<String, String> map = new HashMap<>();
        map.put("numOfKms", body.get("numOfKms"));
        map.put("stateVehicle", body.get("stateVehicle"));
        map.put("color", body.get("color"));
        map.put("yearOfBike", body.get("yearOfBike"));
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
        String tmp = img.replace("[", "").replace("]", "").trim();
        String[] arr = tmp.split(",");
        List<ImageEntity> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setUrl(arr[i].trim());
            imageEntity.setOwnId(bikeEntity.getId());
            imageEntity.setType(MainConstants.STATUS_BIKE);
            list.add(imageEntity);
//                System.out.println("url " + imageEntity.getUrl());
        }
        imageRepository.saveAll(list);

        return bikeEntity;
    }
}

