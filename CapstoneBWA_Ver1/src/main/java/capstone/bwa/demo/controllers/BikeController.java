package capstone.bwa.demo.controllers;

import capstone.bwa.demo.crawlmodel.BikeHondaCrawler;
import capstone.bwa.demo.entities.BikeEntity;
import capstone.bwa.demo.repositories.BikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController("bike")
public class BikeController {
    @Autowired
    private BikeRepository bikeRepository;

    @GetMapping
    public ResponseEntity getAllProduct(){
        if (bikeRepository.findAll().isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        return new ResponseEntity(bikeRepository.findAll(),HttpStatus.OK);
    }

    @GetMapping("crawl")
    public ResponseEntity crawlData(){
        BikeHondaCrawler crawlBikeHonda = new BikeHondaCrawler();
        List<BikeEntity> bikeEntityList = new ArrayList<>();
//        try {
//            bikeEntityList= crawlBikeHonda.crawlHonda();
//        }catch (IOException e){
//            return new ResponseEntity(e,HttpStatus.BAD_REQUEST);
//        }
//        for (BikeEntity bike:bikeEntityList){
//            bikeRepository.saveAndFlush(bike);
//        }
        return new ResponseEntity(bikeEntityList,HttpStatus.OK);
    }
}
