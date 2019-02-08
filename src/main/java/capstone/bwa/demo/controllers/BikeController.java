package capstone.bwa.demo.controllers;

import capstone.bwa.demo.repositories.BikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("bike")
public class BikeController {
    @Autowired
    private BikeRepository bikeRepository;

    @GetMapping
    public ResponseEntity getAllProduct(){
        if (bikeRepository.findAll().isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        return new ResponseEntity(bikeRepository.findAll(),HttpStatus.OK);
    }
}
