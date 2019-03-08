package capstone.bwa.demo.controllers;


import capstone.bwa.demo.entities.AccessoryEntity;
import capstone.bwa.demo.entities.CategoryEntity;
import capstone.bwa.demo.entities.ImageEntity;
import capstone.bwa.demo.repositories.AccessoryRepository;
import capstone.bwa.demo.repositories.ImageRepository;
import capstone.bwa.demo.views.View;
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

@RestController
public class AccessoryController {
    @Autowired
    AccessoryRepository accessoryRepository;

    @Autowired
    ImageRepository imageRepository;

    @JsonView(View.IAccessories.class)
    @GetMapping("accessories")
    public ResponseEntity getAllAccessory() {
        List<AccessoryEntity> accessoryEntityList = accessoryRepository.findAll();
        return new ResponseEntity(accessoryEntityList, HttpStatus.OK);
    }

    @JsonView(View.IAccessory.class)
    @GetMapping("accessory/{id}")
    public ResponseEntity getAccessory(@PathVariable int id) {
        return null;
    }


}
