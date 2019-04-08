package capstone.bwa.demo.controllers;

import capstone.bwa.demo.repositories.BikeRepository;
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

@RestController("bike")
public class BikeController {
    @Autowired
    private BikeRepository bikeRepository;

    @GetMapping("bikes")
    public ResponseEntity getAllProduct() {
        return null;
    }

    @JsonView(View.IBike.class)
    @GetMapping("bike/{id}")
    public ResponseEntity getBikeFromId(@PathVariable int id) {
        return null;
    }

    @GetMapping("bike/brands")
    public ResponseEntity getBrandsDistinct() {
        List<String> brands = bikeRepository.getAllBrands().stream().map(obj -> (String) obj).collect(Collectors.toList());
        return new ResponseEntity(brands, HttpStatus.OK);
    }


}

