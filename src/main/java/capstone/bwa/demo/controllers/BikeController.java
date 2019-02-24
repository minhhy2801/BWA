package capstone.bwa.demo.controllers;

import capstone.bwa.demo.View.Views;
import capstone.bwa.demo.entities.BikeEntity;
import capstone.bwa.demo.entities.CategoryEntity;
import capstone.bwa.demo.entities.ImageEntity;
import capstone.bwa.demo.repositories.BikeRepository;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController("bike")
public class BikeController {
    @Autowired
    private BikeRepository bikeRepository;

    @GetMapping("bikes")
    public ResponseEntity getAllProduct() {
        if (bikeRepository.findAll().isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        return new ResponseEntity(bikeRepository.findAll(), HttpStatus.OK);
    }

    @JsonView(Views.IBike.class)
    @GetMapping("bike/{id}")
    public ResponseEntity getBikeFromId(@PathVariable int id) {
        BikeEntity bikeEntity = bikeRepository.findById(id);
        if (bikeEntity != null) {
            Gson gson = new Gson();
            try {
                JsonObject objectReturn = new JsonObject();

                String json = bikeEntity.getDescription();
                objectReturn.addProperty("name", bikeEntity.getName());
                objectReturn.addProperty("brand", bikeEntity.getBrand());
                objectReturn.addProperty("price", bikeEntity.getPrice());
                objectReturn.add("description", getJsonObject(json));

                CategoryEntity categoryEntity = bikeEntity.getCategoryByCategoryId();
                json = getJson("name", categoryEntity.getName());
                objectReturn.add("category", getJsonObject(json));

                //+Image+
                Collection<ImageEntity> listImageEntities = bikeEntity.getImagesById();
                List<ImageEntity> listImage = new ArrayList<>();
                for (ImageEntity imageEntity : listImageEntities) {
                    if (imageEntity.getType().equals("BIKE")) {
                        listImage.add(imageEntity);
                    }
                }
                List<Map<String, String>> jsonMap = new ArrayList<>();
                for (ImageEntity imageEntity:listImage){
                    Map<String,String> mapUrl = new HashMap<>();
                    mapUrl.put("url",imageEntity.getUrl());
                    jsonMap.add(mapUrl);
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
}
