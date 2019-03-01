package capstone.bwa.demo.controllers;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.BikeEntity;
import capstone.bwa.demo.entities.SupplyProductEntity;
import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.repositories.FeedbackRepository;
import capstone.bwa.demo.repositories.SupplyProductRepository;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
public class SupplyProductController {
    @Autowired
    private SupplyProductRepository supplyProductRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;

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

        return new ResponseEntity(supplyProductEntity, HttpStatus.OK);
    }

    /**
     * Return new supply post with status PENDING
     *
     * @param id
     * @param body
     * @return 403 if cannot create
     * 200 if OK
     */
    @PostMapping("user/{id}/supply_post")
    public ResponseEntity createSupplyPost(@PathVariable int id, @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(id);
        if (accountEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (!accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE) ||
                !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_USER))
            return new ResponseEntity(HttpStatus.LOCKED);

        return null;
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
    private SupplyProductEntity paramSupplyPostEntityRequest(Map<String, String> body, SupplyProductEntity supplyProductEntity) {
        String title = body.get("title");
        String description = body.get("description");
        String imgThumbnail = body.get("imgThumbnailUrl");
        String location = body.get("location");
        int cateId = Integer.parseInt(body.get("categoryId"));
        String typeItem = body.get("typeItem");

        return supplyProductEntity;
    }

    private BikeEntity paramBikeEntityRequest(Map<String, String> body, BikeEntity bikeEntity) {


        return bikeEntity;
    }
}

