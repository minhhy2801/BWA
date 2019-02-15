package capstone.bwa.demo.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*******************************************************************************
 * ::STATUS::
 * GOING
 * FINISHED
 *******************************************************************************/
@RestController
public class TransactionDetailController {

    /**
     * Return a transaction of user
     *
     * @param userId
     * @param supProId
     * @param id
     * @return
     */
    @GetMapping("user/{userId}/supply_post/{supProId}/trans/{id}")
    public ResponseEntity getUserTrans(@PathVariable int userId, @PathVariable int supProId, @PathVariable int id) {
        return null;
    }

    /**
     * Return a transaction of supply post (Guest view) after finish supply post
     * @param supProId
     * @param id
     * @return
     */
    @GetMapping("supply_post/{supProId}/trans/{id}")
    public ResponseEntity getTrans(@PathVariable int supProId, @PathVariable int id) {
        return null;
    }

    /**
     * Return list transactions of user who create a supply post view
     *
     * @param userId
     * @param supProId
     * @return
     */
    @GetMapping("user/{userId}/supply_post/{supProId}/trans")
    public ResponseEntity getListTransByOwnId(@PathVariable int userId, @PathVariable int supProId) {
        return null;
    }


    /**
     * Return new transaction when click exchange supply post view
     *
     * @param userId
     * @param supProId
     * @return
     */
    @PostMapping("user/{userId}/supply_post/{supProId}/trans")
    public ResponseEntity createTrans(@PathVariable int userId, @PathVariable int supProId, @RequestBody Map<String, String> body) {
        return null;
    }

    /**
     * Return list transactions when click close supply post. Update all transactions, update status
     *
     * @param userId
     * @param supProId
     * @param body
     * @return
     */
    @PutMapping("user/{userId}/supply_post/{supProId}/trans")
    public ResponseEntity closeFinishedListTrans(@PathVariable int userId, @PathVariable int supProId, @RequestBody Map<String, String> body) {
        return null;
    }


}
