package capstone.bwa.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


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
     * @return 404 if not found
     * 200 if OK
     * @apiNote {
     *
     * }
     */
    @GetMapping("supply_posts")
    public ResponseEntity getListSupplyPosts(){
        return null;
    }

    /**
     * Return supply post
     * @param id
     * @return 404 if not found
     * 200 if OK
     * @apiNote {
     *
     * }
     */
    @GetMapping("supply_post/{id}")
    public ResponseEntity getSupplyPost(@PathVariable int id){
        return null;
    }

}
