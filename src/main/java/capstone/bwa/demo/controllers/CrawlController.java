package capstone.bwa.demo.controllers;

import capstone.bwa.demo.View.ViewsAccessory;
import capstone.bwa.demo.crawlmodel.BikeHondaCrawler;
import capstone.bwa.demo.entities.AccessoryEntity;
import capstone.bwa.demo.entities.BikeEntity;
import capstone.bwa.demo.entities.CategoryEntity;
import capstone.bwa.demo.entities.ImageEntity;
import capstone.bwa.demo.repositories.AccessoryRepository;
import capstone.bwa.demo.repositories.BikeRepository;
import capstone.bwa.demo.repositories.CategoryRepository;
import capstone.bwa.demo.repositories.ImageRepository;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class CrawlController {
    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private AccessoryRepository accessoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("crawlBike")
    public ResponseEntity crawlBike() {
        BikeHondaCrawler crawlBikeHonda = new BikeHondaCrawler();
        crawlBikeHonda.crawlBike();
        Map<BikeEntity, ImageEntity> listNewBike = crawlBikeHonda.getBikeList();
        if (listNewBike.size() == 0) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        List<BikeEntity> listExitBike = bikeRepository.findAll();
        boolean addNewProduct;
        for (Map.Entry<BikeEntity, ImageEntity> entry : listNewBike.entrySet()) {
            addNewProduct = true;
            for (BikeEntity bike : listExitBike) {
                if (bike.getHashCode().equals(entry.getKey().getHashCode())) {
                    addNewProduct = false;
                }
            }
            if (addNewProduct) {
                bikeRepository.saveAndFlush(entry.getKey());
                BikeEntity bikeOwn = bikeRepository.findByHashCode(entry.getKey().getHashCode());
                ImageEntity imageEntity = entry.getValue();
                imageEntity.setBikeByOwnId(bikeOwn);
                imageRepository.saveAndFlush(imageEntity);
            }
        }
        return new ResponseEntity(bikeRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("crawlAccessory")
    public ResponseEntity crawlAccessory() {
        BikeHondaCrawler crawlBikeHonda = new BikeHondaCrawler();
        crawlBikeHonda.crawlAccessory();
        int categoryID;
        Map<String, Map<AccessoryEntity, ImageEntity>> categoryMapping = crawlBikeHonda.getCategoryMapping();
        for (Map.Entry<String, Map<AccessoryEntity, ImageEntity>> mapping : categoryMapping.entrySet()) {
            categoryID = checkCatelogy(mapping.getKey());
            Map<AccessoryEntity, ImageEntity> listNewAccessory = mapping.getValue();
            if (listNewAccessory.size() == 0) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
            List<AccessoryEntity> listExitAccessory = accessoryRepository.findAll();
            boolean addNewProduct;
            for (Map.Entry<AccessoryEntity, ImageEntity> entry : listNewAccessory.entrySet()) {
                addNewProduct = true;
                for (AccessoryEntity accessory : listExitAccessory) {
                    if (accessory.getHashAccessoryCode().equals(entry.getKey().getHashAccessoryCode())) {
                        addNewProduct = false;
                    }
                }
                if (addNewProduct) {
                    AccessoryEntity newAccessory = entry.getKey();
                    newAccessory.setCategoryId(categoryID);
                    accessoryRepository.saveAndFlush(newAccessory);
                    newAccessory = accessoryRepository.findByHashAccessoryCode(entry.getKey().getHashAccessoryCode());
                    ImageEntity imageEntity = entry.getValue();
                    imageEntity.setAccessoryByOwnId(newAccessory);
                    imageRepository.saveAndFlush(imageEntity);
                }
            }
        }
        return new ResponseEntity(accessoryRepository.findAll(), HttpStatus.OK);
    }

    private int checkCatelogy(String catelogyName) {
        int categoryID;
        CategoryEntity categoryEntity = categoryRepository.findByName(catelogyName);
        if (categoryEntity == null) {
            CategoryEntity newCategory = new CategoryEntity();
            newCategory.setName(catelogyName);
            newCategory.setType("NewCategogy");
            newCategory.setStatus("New");
            categoryRepository.saveAndFlush(newCategory);
            categoryEntity = categoryRepository.findByName(catelogyName);
        }
        categoryID = categoryEntity.getId();
        return categoryID;
    }
}
