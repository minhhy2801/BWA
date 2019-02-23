package capstone.bwa.demo.controllers;


import capstone.bwa.demo.View.ViewsAccessory;
import capstone.bwa.demo.entities.AccessoryEntity;
import capstone.bwa.demo.entities.CategoryEntity;
import capstone.bwa.demo.entities.ImageEntity;
import capstone.bwa.demo.repositories.AccessoryRepository;
import capstone.bwa.demo.repositories.ImageRepository;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController("accessory")
public class AccessoryController {
    @Autowired
    AccessoryRepository accessoryRepository;

    @Autowired
    ImageRepository imageRepository;

    @JsonView(ViewsAccessory.IListAccessories.class)
    @GetMapping("accessories")
    public ResponseEntity getAllAccessory() {
        List<AccessoryEntity> accessoryEntityList = accessoryRepository.findAll();
        return new ResponseEntity(accessoryEntityList, HttpStatus.OK);
    }

    @JsonView(ViewsAccessory.IAccessory.class)
    @GetMapping("accessory/{id}")
    public ResponseEntity getAccessory(@PathVariable int id) {
        AccessoryEntity accessoryEntity = accessoryRepository.findById(id);
        if (accessoryEntity != null) {
            Gson gson = new Gson();
            try {
                JsonObject objectReturn = new JsonObject();

                String json = accessoryEntity.getDescription();
                objectReturn.addProperty("name", accessoryEntity.getName());
                objectReturn.addProperty("price", accessoryEntity.getPrice());
                objectReturn.add("description", getJsonObject(json));

                CategoryEntity categoryEntity = accessoryEntity.getCategoryByCategoryId();
                json = getJson("name", categoryEntity.getName());
                objectReturn.add("category", getJsonObject(json));

                //+Image+
                Collection<ImageEntity> listImageEntities = accessoryEntity.getImagesById();
                List<ImageEntity> listImage = new ArrayList<>();
                listImage.addAll(listImageEntities);
                Map<String, Object> jsonMap = new HashMap<>();
                for (int i = 0; i < listImage.size(); i++) {
                    if (listImage.get(i).getType().equals("ACCESSORY")) {
                        jsonMap.put("url", listImage.get(i).getUrl());
                    }
                }
                json = gson.toJson(jsonMap);
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

}
