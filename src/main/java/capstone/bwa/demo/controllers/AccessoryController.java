package capstone.bwa.demo.controllers;


import capstone.bwa.demo.repositories.AccessoryRepository;
import capstone.bwa.demo.repositories.ImageRepository;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AccessoryController {
    @Autowired
    AccessoryRepository accessoryRepository;

    @Autowired
    ImageRepository imageRepository;

    @JsonView(View.IAccessories.class)
    @GetMapping("accessories")
    public ResponseEntity getAllAccessory() {
        return null;
    }

    @JsonView(View.IAccessory.class)
    @GetMapping("accessory/{id}")
    public ResponseEntity getAccessory(@PathVariable int id) {
        return null;
    }

    @GetMapping("accessory/brands")
    public ResponseEntity getBrandsDistinct() {
        List<String> brands = accessoryRepository.getAllBrands().stream().map(obj -> (String) obj).collect(Collectors.toList());
        return new ResponseEntity(brands, HttpStatus.OK);
    }
}
