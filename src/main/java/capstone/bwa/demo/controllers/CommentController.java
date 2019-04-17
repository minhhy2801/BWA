package capstone.bwa.demo.controllers;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.CommentEntity;
import capstone.bwa.demo.entities.NewsEntity;
import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.repositories.CommentRepository;
import capstone.bwa.demo.repositories.NewsRepository;
import capstone.bwa.demo.utils.DateTimeUtils;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    @JsonView(View.IComments.class)
    @PostMapping("user/{id}/news/{newsId}/comment")
    public ResponseEntity createComment(@RequestBody Map<String, String> body, @PathVariable int id, @PathVariable int newsId) {
        AccountEntity accountEntity = accountRepository.findById(id);
        CommentEntity commentEntity = new CommentEntity();
        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        String description = body.get("description");

        commentEntity.setCreatedTime(DateTimeUtils.getCurrentTime());
        commentEntity.setStatus(MainConstants.COMMENT_PUBLIC);
        commentEntity.setCreatorId(id);
        commentEntity.setNewsId(newsId);
        commentEntity.setDescription(description);
        commentRepository.save(commentEntity);
        return new ResponseEntity(commentEntity, HttpStatus.OK);
    }

    /**
     * @param id
     * @return list comments with status ACTIVE by news id
     * send status in body
     */
    @JsonView(View.IComments.class)
    @GetMapping("news/{id}/comments/page/{pageId}/limit/{quantity}")
    public ResponseEntity getListCommentsByNews(@PathVariable int id, @PathVariable int pageId,
                                                @PathVariable int quantity) {
        NewsEntity newsEntity = newsRepository.findById(id);
        if (newsEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        Pageable pageWithElements = PageRequest.of(pageId, quantity);
        List<CommentEntity> list = commentRepository.findAllByNewsIdOrderByIdDesc(id, pageWithElements);
        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * @param newsId
     * @param id
     * @param userId
     * @param body
     * @return user can update comment in 15min compare with createdTime
     */
    @JsonView(View.IComments.class)
    @PutMapping("user/{userId}/news/{newsId}/comment/{id}")
    public ResponseEntity updateCommentByUser(@PathVariable int newsId, @PathVariable int id,
                                              @PathVariable int userId,
                                              @RequestBody Map<String, String> body) {
        NewsEntity newsEntity = newsRepository.findById(newsId);
        AccountEntity accountEntity = accountRepository.findById(userId);
        CommentEntity commentEntity = commentRepository.findById(id);

        if (commentEntity == null || newsEntity == null || accountEntity == null
                || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !commentEntity.getCreatorId().equals(userId)
                || !commentEntity.getNewsId().equals(newsId))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        String description = body.get("description");

        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        Date createTime = null;

        try {
            createTime = dateFormat.parse(commentEntity.getCreatedTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createTime);
            calendar.add(Calendar.MINUTE, 15);
            Date afterAdding15Mins = calendar.getTime();
            if (date.compareTo(afterAdding15Mins) < 0) {
                commentEntity.setEditedTime(DateTimeUtils.getCurrentTime());
                commentEntity.setDescription(description);
                commentEntity.setEditorId(userId);
                commentRepository.save(commentEntity);
                return new ResponseEntity(commentEntity, HttpStatus.OK);
            }
        } catch (ParseException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    /**
     * @param id
     * @param adminId
     * @param body
     * @return admin always can update their comment
     */
    @JsonView(View.IComments.class)
    @PutMapping("admin/{adminId}/comment/{id}")
    public ResponseEntity changeStatusCommentByAdmin(@PathVariable int id, @PathVariable int adminId,
                                                     @RequestBody Map<String, String> body) {
        AccountEntity accountAdminEntity = accountRepository.findById(adminId);
        CommentEntity commentEntity = commentRepository.findById(id);

        if (accountAdminEntity == null || commentEntity == null ||
                !accountAdminEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE) ||
                !accountAdminEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        String status = body.get("status");

        commentEntity.setEditedTime(DateTimeUtils.getCurrentTime());
        commentEntity.setStatus(status);
        commentEntity.setEditorId(adminId);
        commentRepository.save(commentEntity);
        return new ResponseEntity(commentEntity, HttpStatus.OK);
    }
}
