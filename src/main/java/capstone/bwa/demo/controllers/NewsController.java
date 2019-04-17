package capstone.bwa.demo.controllers;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.NewsEntity;
import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.repositories.ImageRepository;
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

    @JsonView(View.INewsDetail.class)
    @GetMapping("news/{id}")
    public ResponseEntity getANews(@PathVariable int id) {
        NewsEntity newsEntity = newsRepository.findById(id);
        if (newsEntity == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<String> img = imageRepository.findAllByOwnIdAndType(MainConstants.STATUS_NEWS, id);

        Map<String, Object> map = new HashMap<>();
        map.put("news", newsEntity);
        map.put("images", img);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @JsonView(View.INews.class)
    @GetMapping("news/page/{id}/limit/{quantity}")
    public ResponseEntity getListNews(@PathVariable int quantity, @PathVariable int id) {
        Pageable pageWithElements = PageRequest.of(id, quantity);
        List<NewsEntity> list = newsRepository.findAllByStatusOrderByIdDesc(MainConstants.NEWS_PUBLIC, pageWithElements);

        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(list, HttpStatus.OK);
    }


    @JsonView(View.INews.class)
    @GetMapping("admin/{id}/list_news")
    public ResponseEntity getListNewsByAdmin(@PathVariable int id) {
        AccountEntity accountEntity = accountRepository.findById(id);
        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<NewsEntity> newsEntities = newsRepository.findAllByOrderByIdDesc();

        if (newsEntities.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(newsEntities, HttpStatus.OK);
    }

    @JsonView(View.INewsDetail.class)
    @PostMapping("admin/{id}/news")
    public ResponseEntity createNews(@PathVariable int id, @RequestBody Map<String, String> body) {
        AccountEntity accountAdminEntity = accountRepository.findById(id);
        if (accountAdminEntity == null ||
                !accountAdminEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        NewsEntity newsEntity = paramNewsRequest(body, new NewsEntity());
        newsEntity.setCreatedTime(DateTimeUtils.getCurrentTime());
        newsEntity.setCreatorId(id);

        newsRepository.save(newsEntity);

        return new ResponseEntity(newsEntity, HttpStatus.OK);
    }

    /**
     * @param id
     * @param adminId
     * @param body
     * @return
     */
    @JsonView(View.INewsDetail.class)
    @PutMapping("admin/{adminId}/news/{id}")
    public ResponseEntity updateNews(@PathVariable int id, @PathVariable int adminId,
                                     @RequestBody Map<String, String> body) {
        AccountEntity accountAdminEntity = accountRepository.findById(adminId);
        NewsEntity newsEntity = newsRepository.findById(id);

        if (accountAdminEntity == null || newsEntity == null
                || !accountAdminEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (newsEntity.getCreatorId() == null) {
            newsEntity.setCreatorId(adminId);
            newsEntity.setCreatedTime(DateTimeUtils.getCurrentTime());
        }

        newsEntity = paramNewsRequest(body, newsEntity);
        newsEntity.setEditedTime(DateTimeUtils.getCurrentTime());
        newsEntity.setEditorId(adminId);
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

        newsEntity.setEditedTime(DateTimeUtils.getCurrentTime());
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

    private NewsEntity paramNewsRequest(Map<String, String> body, NewsEntity newsEntity) {
        int cateId = Integer.parseInt(body.get("categoryId"));
        String title = body.get("title");
        String description = body.get("description");
        String imgThumbnailUrl = body.get("imgThumbnailUrl");
        newsEntity.setDescription(description);
        newsEntity.setTitle(title);
        newsEntity.setImgThumbnailUrl(imgThumbnailUrl);
        newsEntity.setCategoryId(cateId);
        newsEntity.setStatus(MainConstants.NEWS_PUBLIC);
        return newsEntity;
    }
}

