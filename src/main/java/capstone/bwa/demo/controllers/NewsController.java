package capstone.bwa.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import capstone.bwa.demo.View.Views;
import com.fasterxml.jackson.annotation.JsonView;
import capstone.bwa.demo.entities.NewsEntity;
import capstone.bwa.demo.entities.CategoryEntity;
import capstone.bwa.demo.entities.ImageEntity;
import capstone.bwa.demo.repositories.NewsRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


/*******************************************************************************
 * ::STATUS::
 * PUBLIC
 * HIDDEN
 *******************************************************************************/

@RestController
public class NewsController {

    @Autowired
    NewsRepository newsRepository;
    /**
     * Returns News object
     *
     * @param id of news
     * @return 404 if not found in db
     * 200 if found
     * @apiNote example format
     * {
     * }
     */
	@JsonView(Views.INews.class)
    @GetMapping("news/{id}")
    public ResponseEntity getANews(@PathVariable int id) {
		NewsEntity newsEntity = newsRepository.findById(id);
        if (newsEntity != null) {
            Gson gson = new Gson();
            try {
                JsonObject objectReturn = new JsonObject();
                objectReturn.addProperty("title", newsEntity.getTitle());
                objectReturn.addProperty("description", newsEntity.getDescription());
                objectReturn.addProperty("imageThumnail",newsEntity.getImgThumbnailUrl());
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
                for (ImageEntity imageEntity:listImage){
                    Map<String,String> mapUrl = new HashMap<>();
                    if (imageEntity.getType().equals("NEWS")) {
                        mapUrl.put("url", imageEntity.getUrl());
                        jsonMap.add(mapUrl);
                    }
                }
                Map<String,Object> data = new HashMap<>();
                data.put("image",jsonMap);
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
    private JsonObject getJsonObject(String json) {
        JsonParser parser = new JsonParser();
        JsonObject object = new JsonObject();
        object = parser.parse(json).getAsJsonObject();
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
    /**
     *  Returns list news object sort last by quantity (status public)
     * @param quantity
     * @return 404 if not found in db
     * 200 if found
     */
    @GetMapping("news/limit/{quantity}")
    public ResponseEntity getListNews(@PathVariable int quantity) {

        return null;
    }

    /**
     * Return list news sort last by quantity, status
     * if status = All -> get by quantity
     * @param id
     * @param quantity
     * @param status
     * @return
     * 404 if not found in db
     * 403 if not admin
     * 200 if found
     */
    @GetMapping("admin/{id}/news/limit/{quantity}/status")
    public ResponseEntity getListNewsByAdmin(@PathVariable int id, @PathVariable int quantity, @RequestBody Map<String, String> status) {

        return null;
    }

    /**
     * Returns new event object with status PUBLIC
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
     * Returns update news object
     * Admin can update event of other admin
     * Mem can only update event of him/herself
     *
     * @param id
     * @param userId
     * @param body
     * @return 403 if not admin
     * 404 if not found news
     * 200 if update success
     */

    @PutMapping("admin/{userId}/news/{id}")
    public ResponseEntity updateNews(@PathVariable int id, @PathVariable int userId, @RequestBody Map<String, String> body) {

        return null;
    }

    /**
     * Returns update status news object
     * @param userId
     * @param id
     * @param status
     * @return 403 if not admin
     * 200 if update status hidden or public
     */
    @PutMapping("admin/{userId}/news/{id}/status")
    public ResponseEntity changeStatusNews(@PathVariable int userId, @PathVariable int id, @RequestBody Map<String, String> status){
        return null;
    }

}
