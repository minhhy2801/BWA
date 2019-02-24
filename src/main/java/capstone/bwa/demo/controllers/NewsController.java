package capstone.bwa.demo.controllers;

import capstone.bwa.demo.entities.CategoryEntity;
import capstone.bwa.demo.entities.ImageEntity;
import capstone.bwa.demo.entities.NewsEntity;
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
        NewsEntity newsEntity = newsRepository.findById(id);
        if (newsEntity != null) {
            Gson gson = new Gson();
            try {
                JsonObject objectReturn = new JsonObject();
                objectReturn.addProperty("title", newsEntity.getTitle());
                objectReturn.addProperty("description", newsEntity.getDescription());
                objectReturn.addProperty("imageThumnail", newsEntity.getImgThumbnailUrl());
                CategoryEntity categoryEntity = newsEntity.getCategoryByCategoryId();
                String json = getJson("name", categoryEntity.getName());
                objectReturn.add("category", getJsonObject(json));

                Collection<ImageEntity> listImageEntities = newsEntity.getImagesById();
                List<ImageEntity> listImage = new ArrayList<>();
                for (ImageEntity imageEntity : listImageEntities) {
                    if (imageEntity.getType().equals("NEWS")) {
                        listImage.add(imageEntity);
                    }
                }
                List<Map<String, String>> jsonMap = new ArrayList<>();
                for (ImageEntity imageEntity : listImage) {
                    Map<String, String> mapUrl = new HashMap<>();
                    if (imageEntity.getType().equals("NEWS")) {
                        mapUrl.put("url", imageEntity.getUrl());
                        jsonMap.add(mapUrl);
                    }
                }
                Map<String, Object> data = new HashMap<>();
                data.put("image", jsonMap);
                json = gson.toJson(data);
                objectReturn.add("image", getJsonObject(json));

                String jsonReturn = gson.toJson(objectReturn);
                return new ResponseEntity(jsonReturn, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
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
     * @param pageId
     * @param quantity
     * @param cateId
     * @return list news by categoryId (status ACTIVE)
     */
    @GetMapping("news/page/{pageId}/limit/{quantity}/category/{cateId}")
    public ResponseEntity getListNewsByCategory(@PathVariable int pageId, @PathVariable int quantity,
                                                @PathVariable int cateId) {
        return null;
    }

    private JsonObject getJsonObject(String json) {
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(json).getAsJsonObject();
        return object;
    }

    private String getJson(String key, Object object) {
        Gson gson = new Gson();
        String json;
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put(key, object);
        json = gson.toJson(jsonMap);
        return json;
    }

}

