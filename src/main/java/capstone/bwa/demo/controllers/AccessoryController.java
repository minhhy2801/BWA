package capstone.bwa.demo.controllers;


import capstone.bwa.demo.View.ViewsAccessory;
import capstone.bwa.demo.entities.AccessoryEntity;
import capstone.bwa.demo.repositories.AccessoryRepository;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
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
            String json = gson.toJson(accessoryEntity.getDescription());
            try {
                String jsonFormattedString = new JSONTokener(json).nextValue().toString();
                JSONObject object = new JSONObject();
                jsonArray.add(jsonFormattedString);
            }catch (JSONException e){
                System.out.println(e);
            }
        }
        return new ResponseEntity(jsonArray,HttpStatus.OK);
    }
}
