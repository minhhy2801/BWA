package capstone.bwa.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;
import java.util.Map;


/*******************************************************************************
 * ::STATUS::
 * ACTIVE
 * HIDDEN
 *******************************************************************************/

@RestController
public class NewsController {

    /**
     * Returns News object with status ACTIVE
     * status send in body
     *
     * @param id of news
     * @return 404 if not found in db
     * 200 if found
     */
    @PostMapping("news/{id}")
    public ResponseEntity getANews(@PathVariable int id, @RequestBody Map<String, String> body) {

        return null;
    }

    /**
     * @param quantity
     * @param id
     * @param body
     * @return list news sort latest with status in body
     */
    @PostMapping("news/page/{id}/limit/{quantity}")
    public ResponseEntity getListNews(@PathVariable int quantity, @PathVariable int id,
                                      @RequestBody Map<String, String> body) {

        return null;
    }

    /**
     * Return list news sort latest with status in body
     *
     * @param id
     * @param quantity
     * @param body     (status)
     * @return 404 if not found in db
     * 403 if not admin
     * 200 if found
     */
    @PostMapping("admin/{id}/news/page/{pageId}/limit/{quantity}")
    public ResponseEntity getListNewsByAdmin(@PathVariable int id, @PathVariable int quantity,
                                             @RequestBody Map<String, String> body, @PathVariable int pageId) {

        return null;
    }

    /**
     * Returns news object with status ACTIVE
     * only admin can create News
     *
     * @param id
     * @param body
     * @return 403 if not admin
     * 200 if create success
     */

    @PostMapping("admin/{id}/news")
    public ResponseEntity createNews(@PathVariable int id, @RequestBody Map<String, String> body) {

        return null;
    }

    /**
     * @param id
     * @param adminId
     * @param body
     * @return
     */
    @PutMapping("admin/{adminId}/news/{id}")
    public ResponseEntity updateNews(@PathVariable int id, @PathVariable int adminId,
                                     @RequestBody Map<String, String> body) {

        return null;
    }

    /**
     *
     * @param pageId
     * @param quantity
     * @param cateId
     * @return list news by categoryId (status ACTIVE)
     */
    @GetMapping("news/page/{pageId}/limit/{quantity}/category/{cateId}")
    public ResponseEntity getListNewsByCategory(@PathVariable int pageId, @PathVariable int quantity,
                                                @PathVariable int cateId){
        return null;
    }
}
