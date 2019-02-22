package capstone.bwa.demo.controllers;

import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.repositories.CommentRepository;
import capstone.bwa.demo.repositories.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;
import java.util.Map;

@RestController
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private NewsRepository newsRepository;

    /**
     * @param body
     * @param id
     * @return status default ACTIVE (both user & admin use this function)
     */
    @PostMapping("user/{id}/comment")
    public ResponseEntity createComment(@RequestBody Map<String, String> body, @PathVariable int id) {
        return null;
    }

    /**
     * @param id
     * @return list comments with status ACTIVE by news id
     * send status in body
     */
    @PostMapping("news/{id}/comments/page/{pageId}/limit/{quantity}")
    public ResponseEntity getListCommentsByNews(@PathVariable int id, @PathVariable int pageId,
                                                @PathVariable int quantity, Map<String, String> body) {
        return null;
    }

    /**
     * @param newsId
     * @param id
     * @param userId
     * @param body
     * @return user can update comment in 15min compare with createdTime
     */
    @PutMapping("user/{userId}/news/{newsId}/comment/{id}")
    public ResponseEntity updateCommentByUser(@PathVariable int newsId, @PathVariable int id,
                                              @PathVariable int userId,
                                              @RequestBody Map<String, String> body) {
        return null;
    }

    /**
     * @param newsId
     * @param id
     * @param adminId
     * @param body
     * @return admin always can update their comment
     */
    @PutMapping("admin/{adminId}/news/{newsId}/comment/{id}")
    public ResponseEntity updateCommentByAdmin(@PathVariable int newsId, @PathVariable int id,
                                               @PathVariable int adminId, @RequestBody Map<String, String> body) {
        return null;
    }

    /**
     * @param id
     * @param adminId
     * @param body
     * @return admin can HIDDEN comment of user
     */
    @PutMapping("admin/{adminId}/comment/{id}/status")
    public ResponseEntity changeStatusCommentByAdmin(@PathVariable int id,
                                                     @PathVariable int adminId, @RequestBody Map<String, String> body) {
        return null;
    }
}
