package capstone.bwa.demo.controllers;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import capstone.bwa.demo.entities.CategoryEntity;
import capstone.bwa.demo.entities.ImageEntity;
import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.NewsEntity;
import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.repositories.CategoryRepository;
import capstone.bwa.demo.repositories.ImageRepository;
import capstone.bwa.demo.repositories.NewsRepository;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/*******************************************************************************
 * ::STATUS::
 * ACTIVE
 * HIDDEN
 *******************************************************************************/

@RestController
public class NewsController {

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CategoryRepository categoryRepository;


    /**
     * Returns News object with status ACTIVE
     * status send in body
     *
     * @param id of news
     * @return 404 if not found in db
     * 200 if found
     */
    @JsonView(View.INews.class)
    @PostMapping("news/{id}")
    public ResponseEntity getANews(@PathVariable int id) {
//        NewsEntity newsEntity = newsRepository.findById(id);
//        if (newsEntity != null) {
//            Gson gson = new Gson();
//            try {
//                JsonObject objectReturn = new JsonObject();
//                objectReturn.addProperty("title", newsEntity.getTitle());
//                objectReturn.addProperty("description", newsEntity.getDescription());
//                objectReturn.addProperty("imageThumnail", newsEntity.getImgThumbnailUrl());
//                CategoryEntity categoryEntity = newsEntity.getCategoryByCategoryId();
//                String json = getJson("name", categoryEntity.getName());
//                objectReturn.add("category", getJsonObject(json));
//
//                Collection<ImageEntity> listImageEntities = newsEntity.getImagesById();
//                List<ImageEntity> listImage = new ArrayList<>();
//                for (ImageEntity imageEntity : listImageEntities) {
//                    if (imageEntity.getType().equals("NEWS")) {
//                        listImage.add(imageEntity);
//                    }
//                }
//                List<Map<String, String>> jsonMap = new ArrayList<>();
//                for (ImageEntity imageEntity : listImage) {
//                    Map<String, String> mapUrl = new HashMap<>();
//                    if (imageEntity.getType().equals("NEWS")) {
//                        mapUrl.put("url", imageEntity.getUrl());
//                        jsonMap.add(mapUrl);
//                    }
//                }
//                Map<String, Object> data = new HashMap<>();
//                data.put("image", jsonMap);
//                json = gson.toJson(data);
//                objectReturn.add("image", getJsonObject(json));
//
//                String jsonReturn = gson.toJson(objectReturn);
//                return new ResponseEntity(jsonReturn, HttpStatus.OK);
//            } catch (Exception e) {
//                return new ResponseEntity(HttpStatus.BAD_REQUEST);
//            }
//        }
//        return new ResponseEntity(HttpStatus.NOT_FOUND);
        NewsEntity newsEntity = newsRepository.findById(id);
        if (newsEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        String url = imageRepository.findAllByNewsByOwnId_IdAndType("NEWS", id);
        String[] arrUrl = url.split(",");
        List<String> listUrl = new ArrayList<>();
        for (String s : arrUrl) {
            listUrl.add(s);
        }
        Map<String, Object> jsonReturn = new HashMap<>();
        jsonReturn.put("news", newsEntity);
        jsonReturn.put("url", listUrl);

        return new ResponseEntity(jsonReturn, HttpStatus.OK);
    }

    /**
     * @param quantity
     * @param id
     * @param body
     * @return list news sort latest with status in body
     */
    @JsonView(View.INews.class)
    @PostMapping("news/page/{id}/limit/{quantity}")
    public ResponseEntity getListNews(@PathVariable int quantity, @PathVariable int id,
                                      @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.NO_CONTENT);

        if (!status.equals(MainConstants.NEWS_HIDDEN) && !status.equals(MainConstants.NEWS_PUBLIC)
                && !status.equals(MainConstants.NEWS_ACTIVE) && !status.equals(MainConstants.GET_ALL))
            return new ResponseEntity(HttpStatus.FORBIDDEN);

        List<NewsEntity> list;

        Pageable pageWithElements = PageRequest.of(id, quantity);

        if (status.equals(MainConstants.GET_ALL)) {
            List<String> statusNewsShow = new ArrayList<>();
            statusNewsShow.add(MainConstants.NEWS_PUBLIC);
            statusNewsShow.add(MainConstants.NEWS_HIDDEN);
            statusNewsShow.add(MainConstants.NEWS_ACTIVE);

            list = newsRepository.findAllByStatusInOrderByIdDesc(statusNewsShow, pageWithElements);
        } else list = newsRepository.findAllByStatusOrderByIdDesc(status, pageWithElements);

        if (list.size() < 1)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        return new ResponseEntity(list, HttpStatus.OK);

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
    @JsonView(View.INews.class)
    @PostMapping("admin/{id}/news")
    public ResponseEntity createNews(@PathVariable int id, @RequestBody Map<String, String> body) {
        AccountEntity accountAdminEntity = accountRepository.findById(id);
        NewsEntity newsEntity = new NewsEntity();
        if (accountAdminEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (!accountAdminEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.LOCKED);
        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);

        Date date = new Date(System.currentTimeMillis());
        int cateId = Integer.parseInt(body.get("categoryId"));
        String status = body.get("status");
        String title = body.get("title");
        String description = body.get("description");
        String imgThumnailUrl = body.get("imgThumnailUrl");


        newsEntity.setCreatedTime(date.toString());
        newsEntity.setStatus(status);
        newsEntity.setCreatorId(id);
        newsEntity.setDescription(description);
        newsEntity.setTitle(title);
        newsEntity.setImgThumbnailUrl(imgThumnailUrl);
        newsEntity.setCategoryId(cateId);
        newsRepository.saveAndFlush(newsEntity);
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
        if (accountAdminEntity == null || newsEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (!accountAdminEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.LOCKED);
        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Date date = new Date(System.currentTimeMillis());
        int cateId = Integer.parseInt(body.get("categoryId"));
        String status = body.get("status");
        String title = body.get("title");
        String description = body.get("description");
        String imgThumnailUrl = body.get("imgThumnailUrl");


        newsEntity.setEditedTime(date.toString());
        newsEntity.setStatus(status);
        newsEntity.setCreatorId(id);
        newsEntity.setDescription(description);
        newsEntity.setTitle(title);
        newsEntity.setImgThumbnailUrl(imgThumnailUrl);
        newsEntity.setCategoryId(cateId);
        newsRepository.saveAndFlush(newsEntity);
        return new ResponseEntity(newsEntity, HttpStatus.OK);
    }


    /**
     * @param pageId
     * @param quantity
     * @param cateId
     * @return list news by categoryId (status ACTIVE)
     */
    @GetMapping("news/page/{pageId}/limit/{quantity}/category/{cateId}")
    public ResponseEntity getListNewsByCategory(@PathVariable int pageId, @PathVariable int quantity,
                                                @PathVariable int cateId) {
        CategoryEntity categoryEntity = categoryRepository.findById(cateId);
        if (categoryEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<NewsEntity> list;

        Pageable pageWithElements = PageRequest.of(pageId, quantity);

        list = newsRepository.findAllByCategoryIdInOrderByIdDesc(cateId, pageWithElements);


        if (list.size() < 1)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        return new ResponseEntity(list, HttpStatus.OK);
    }
}

