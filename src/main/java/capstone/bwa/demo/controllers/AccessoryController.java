package capstone.bwa.demo.controllers;


import capstone.bwa.demo.View.ViewsAccessory;
import capstone.bwa.demo.entities.AccessoryEntity;
import capstone.bwa.demo.repositories.AccessoryRepository;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("accessory")
public class AccessoryController {
    @Autowired
    AccessoryRepository accessoryRepository;

    @JsonView(ViewsAccessory.Public.class)
    @GetMapping("getAllAccessory")
    public ResponseEntity getAllAccessory() {
        List<AccessoryEntity> accessoryEntityList = accessoryRepository.findAll();
        JsonArray jsonArray = new JsonArray();
        Gson gson = new Gson();
        for (AccessoryEntity accessoryEntity : accessoryEntityList) {
            try {
                String jsonDes = accessoryEntity.getDescription();
                JsonParser parser = new JsonParser();
                JsonObject object = parser.parse(jsonDes).getAsJsonObject();
                JsonObject objectReturn = new JsonObject();
                objectReturn.addProperty("name",accessoryEntity.getName());
                objectReturn.add("description",object);
                String jsonReturn = gson.toJson(objectReturn);
                jsonArray.add(jsonReturn);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return new ResponseEntity(jsonArray,HttpStatus.OK);
    }
}
