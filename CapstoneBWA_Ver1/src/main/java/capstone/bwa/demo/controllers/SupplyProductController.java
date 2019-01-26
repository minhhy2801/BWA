package capstone.bwa.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * Return supply posts PUBLIC + CLOSED
     *
     * @return 404 if not found
     * 200 if OK
     * @apiNote {
     *
     * }
     */
    @GetMapping("supply_posts/limit/{quantity}/status")
    public ResponseEntity getListSupplyPosts(@PathVariable int quantity, @RequestBody Map<String, String> body) {
        return null;
    }

    /**
     * Return supply post
     *
     * @param id
     * @return 404 if not found
     * 200 if OK
     * @apiNote {
     *
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
    @PutMapping("user/{userId}/supply_post{id}")
    public ResponseEntity closeSupplyPost(@PathVariable int id, @PathVariable int userId, @RequestBody Map<String, String> body) {

        return null;
    }
}
