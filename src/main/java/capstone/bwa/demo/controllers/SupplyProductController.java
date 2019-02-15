package capstone.bwa.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;
import java.util.Map;


/*******************************************************************************
 * ::STATUS::
 * PUBLIC
 * CLOSED
 * HIDDEN
 *******************************************************************************/

@RestController
public class SupplyProductController {
    /**
     * Return list supply posts
     * if status = ALL -> return status closed + public
     * status send in body
     *
     * @return 404 if not found
     * 200 if OK
     * @apiNote {
     * <p>
     * }
     */
    @GetMapping("supply_posts/page/{id}/limit/{quantity}")
    public ResponseEntity getListSupplyPosts(@PathVariable int quantity, @PathVariable int id, @RequestBody Map<String, String> body) {
        return null;
    }

    /**
     * Return supply post
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
     * Return new supply post
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
     * Return change status
     *
     * @param id
     * @param userId
     * @param body
     * @return 403 if cannot create
     * 404 if found
     * 200 if OK
     */
    @PutMapping("user/{userId}/supply_post/{id}")
    public ResponseEntity closeSupplyPost(@PathVariable int id, @PathVariable int userId, @RequestBody Map<String, String> body) {

        return null;
    }

    /**
     *
     * status send in body
     * if status ALL -> get all supply post sort desc without status
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
     * if status ALL -> get all supply post sort desc without status
     * @param adminId
     * @param id
     * @param quantity
     * @param body
     * @return
     */
    @GetMapping("admin/{adminId}/supply_posts/page/{id}/limit/{quantity}")
    public ResponseEntity getListSupplyPostsByAdmin(@PathVariable int adminId, @PathVariable int id,
                                                    @PathVariable int quantity, @RequestBody Map<String, String> body) {

        return null;
    }
}
