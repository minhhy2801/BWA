package capstone.bwa.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;
import java.util.Map;


/*******************************************************************************
 * ::STATUS::
 * PENDING
 * ACTIVE
 * CLOSED
 * HIDDEN
 *******************************************************************************/

@RestController
public class SupplyProductController {
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
    @PostMapping("supply_posts/page/{id}/limit/{quantity}")
    public ResponseEntity getListSupplyPosts(@PathVariable int quantity, @PathVariable int id,
                                             @RequestBody Map<String, String> body) {
        return null;
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
    @GetMapping("supply_post/{id}")
    public ResponseEntity getSupplyPost(@PathVariable int id) {
        return null;
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
}
