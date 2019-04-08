package capstone.bwa.demo.controllers;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.NewsEntity;
import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.repositories.ImageRepository;
import capstone.bwa.demo.repositories.NewsRepository;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class NewsController {

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private AccountRepository accountRepository;

    /**
     * Returns News object with status ACTIVE
     * status send in body
     *
     * @param id of news
     * @return 404 if not found in db
     * 200 if found
     */
    @JsonView(View.INewsDetail.class)
    @GetMapping("news/{id}")
    public ResponseEntity getANews(@PathVariable int id) {
        NewsEntity newsEntity = newsRepository.findById(id);
        if (newsEntity == null || !newsEntity.getStatus().equals(MainConstants.NEWS_PUBLIC))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<String> img = imageRepository.findAllByOwnIdAndType(MainConstants.STATUS_NEWS, id);

        Map<String, Object> map = new HashMap<>();
        map.put("news", newsEntity);
        map.put("images", img);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    /**
     * @param quantity
     * @param id
     * @return list news sort latest with status in body
     */
    @JsonView(View.INews.class)
    @GetMapping("news/page/{id}/limit/{quantity}")
    public ResponseEntity getListNews(@PathVariable int quantity, @PathVariable int id) {
        Pageable pageWithElements = PageRequest.of(id, quantity);
        List<NewsEntity> list = newsRepository.findAllByStatusOrderByIdDesc(MainConstants.NEWS_PUBLIC, pageWithElements);

        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(list, HttpStatus.OK);
    }

    /**
     * Return list news sort latest with status in body
     *
     * @param id
     * @param quantity
     * @return 404 if not found in db
     * 403 if not admin
     * 200 if found
     */
    @JsonView(View.INews.class)
    @GetMapping("admin/{id}/news/page/{pageId}/limit/{quantity}")
    public ResponseEntity getListNewsByAdmin(@PathVariable int id, @PathVariable int quantity,
                                             @PathVariable int pageId) {
        AccountEntity accountEntity = accountRepository.findById(id);
        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        Pageable pageWithElements = PageRequest.of(pageId, quantity);
        List<NewsEntity> newsEntities = newsRepository.findAllByOrderByIdDesc(pageWithElements);

        if (newsEntities.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(newsEntities, HttpStatus.OK);
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
    @JsonView(View.INews.class)
    @PostMapping("admin/{id}/news")
    public ResponseEntity createNews(@PathVariable int id, @RequestBody Map<String, String> body) {
        AccountEntity accountAdminEntity = accountRepository.findById(id);
        if (accountAdminEntity == null ||
                !accountAdminEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        int cateId = Integer.parseInt(body.get("categoryId"));
        String title = body.get("title");
        String description = body.get("description");
        String imgThumbnailUrl = body.get("imgThumbnailUrl");

        NewsEntity newsEntity = new NewsEntity();
        newsEntity.setTitle(title);
        newsEntity.setImgThumbnailUrl(imgThumbnailUrl);
        newsEntity.setCreatedTime(dateFormat.format(date));
        newsEntity.setStatus(MainConstants.NEWS_PUBLIC);
        newsEntity.setCreatorId(id);
        newsEntity.setDescription(description);
        newsEntity.setCategoryId(cateId);
        newsRepository.save(newsEntity);
        return new ResponseEntity(newsEntity, HttpStatus.OK);
    }

    /**
     * @param id
     * @param adminId
     * @param body
     * @return
     */
    @JsonView(View.INews.class)
    @PutMapping("admin/{adminId}/news/{id}")
    public ResponseEntity updateNews(@PathVariable int id, @PathVariable int adminId,
                                     @RequestBody Map<String, String> body) {
        AccountEntity accountAdminEntity = accountRepository.findById(adminId);
        NewsEntity newsEntity = newsRepository.findById(id);
        if (accountAdminEntity == null || newsEntity == null
                || !accountAdminEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        int cateId = Integer.parseInt(body.get("categoryId"));
        String title = body.get("title");
        String description = body.get("description");
        String imgThumbnailUrl = body.get("imgThumbnailUrl");

        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        newsEntity.setEditedTime(dateFormat.format(date));
        newsEntity.setEditorId(adminId);
        newsEntity.setDescription(description);
        newsEntity.setTitle(title);
        newsEntity.setImgThumbnailUrl(imgThumbnailUrl);
        newsEntity.setCategoryId(cateId);
        newsRepository.saveAndFlush(newsEntity);
        return new ResponseEntity(newsEntity, HttpStatus.OK);
    }

    @JsonView(View.INews.class)
    @PutMapping("admin/{id}/news/{newsId}/status")
    public ResponseEntity changeStatusByAdmin(@PathVariable int id, @PathVariable int newsId,
                                              @RequestBody Map<String, String> body) {
        AccountEntity accountAdminEntity = accountRepository.findById(id);
        NewsEntity newsEntity = newsRepository.findById(newsId);
        if (accountAdminEntity == null || newsEntity == null
                || !accountAdminEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        String status = body.get("status");
        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        newsEntity.setEditedTime(dateFormat.format(date));
        newsEntity.setEditorId(id);
        newsEntity.setStatus(status);
        newsRepository.save(newsEntity);
        return new ResponseEntity(newsEntity, HttpStatus.OK);
    }

    @JsonView(View.INewsFilter.class)
    @GetMapping("news/search_filter")
    public ResponseEntity searchFilterNews() {
        List<NewsEntity> list = newsRepository.findTop200ByStatusOrderByIdDesc(MainConstants.NEWS_PUBLIC);
        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(list);
    }

    @GetMapping("news/record")
    public ResponseEntity countTotalPage() {

        int totalRecord = newsRepository.countAllByStatus(MainConstants.NEWS_PUBLIC);

        return new ResponseEntity(totalRecord, HttpStatus.OK);
    }

    @JsonView(View.INews.class)
    @PostMapping("news/search")
    public ResponseEntity searchTitleNews(@RequestBody Map<String, String> body) {
        String text = body.get("search").trim();
        if (text.isEmpty() || text == "") return new ResponseEntity(HttpStatus.BAD_REQUEST);
        List<NewsEntity> list = newsRepository.findAllByStatusAndTitleContainingIgnoreCase(MainConstants.NEWS_PUBLIC, text);
        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(list);
    }
}

