package capstone.bwa.demo.controllers;

import capstone.bwa.demo.crawlmodel.BikeHondaCrawler;
import capstone.bwa.demo.entities.*;
import capstone.bwa.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
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

    @Autowired
    private NewsRepository newsRepository;

    @GetMapping("admin/crawl_bikes")
    public ResponseEntity crawlBike() {
        BikeHondaCrawler crawler = new BikeHondaCrawler();
        List<Map<String,String>> listBike = new ArrayList<>();
        try {
            crawler.getBikeFromHonda();
            listBike = crawler.getListXeTayGa();
            addBike(6,listBike);
            listBike = crawler.getListXeCon();
            addBike(5,listBike);
            listBike = crawler.getListXeSo();
            addBike(7,listBike);
            listBike = crawler.getListXeMoTo();
            addBike(8,listBike);
        }catch (IOException e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(bikeRepository.findAll(),HttpStatus.OK);
    }

    @GetMapping("admin/crawl_accessories")
    public ResponseEntity crawlAccessory() {
        BikeHondaCrawler crawlBikeHonda = new BikeHondaCrawler();
        crawlBikeHonda.crawlAccessory();
        int categoryID;
        Map<String, Map<AccessoryEntity, ImageEntity>> categoryMapping = crawlBikeHonda.getCategoryMappingAccessories();
        for (Map.Entry<String, Map<AccessoryEntity, ImageEntity>> mapping : categoryMapping.entrySet()) {
            categoryID = checkCategory(mapping.getKey());
            Map<AccessoryEntity, ImageEntity> listNewAccessory = mapping.getValue();
            if (listNewAccessory.size() == 0) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
            List<AccessoryEntity> listExitAccessory = accessoryRepository.findAll();
            boolean addNewProduct;
            for (Map.Entry<AccessoryEntity, ImageEntity> entry : listNewAccessory.entrySet()) {
                AccessoryEntity newAccessory = entry.getKey();
                ImageEntity imageEntity = entry.getValue();
                addNewProduct = true;
                if (imageEntity.getUrl().equals("")) {
                    addNewProduct = false;
                } else {
                    for (AccessoryEntity accessory : listExitAccessory) {
                        if (accessory.getHashAccessoryCode().equals(newAccessory.getHashAccessoryCode())) {
                            if (!accessory.getPrice().equals(newAccessory.getPrice())) {
                                accessory.setPrice(newAccessory.getPrice());
                                accessoryRepository.saveAndFlush(accessory);
                            }
                            addNewProduct = false;
                        }
                    }
                }
                if (addNewProduct) {
                    newAccessory.setCategoryId(categoryID);
                    accessoryRepository.saveAndFlush(newAccessory);
                    newAccessory = accessoryRepository.findByHashAccessoryCode(entry.getKey().getHashAccessoryCode());
                    imageEntity.setOwnId(newAccessory.getId());
                    imageEntity.setAccessoryByOwnId(newAccessory);
                    imageRepository.saveAndFlush(imageEntity);
                }
            }
        }
        return new ResponseEntity(accessoryRepository.findAll(), HttpStatus.OK);
    }

//    @GetMapping("test")
//    public ResponseEntity insertNews(){
//        BikeHondaCrawler crawler = new BikeHondaCrawler();
//        List<Map<String,String>> listBike = new ArrayList<>();
//        try {
//            crawler.getBikeFromHonda();
//            listBike = crawler.getListXeTayGa();
//            addBike(6,listBike);
//            listBike = crawler.getListXeCon();
//            addBike(5,listBike);
//            listBike = crawler.getListXeSo();
//            addBike(7,listBike);
//        }catch (IOException e){
//            return new ResponseEntity(HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity(bikeRepository.findAll(),HttpStatus.OK);
//    }

    private int checkCategory(String categogyName) {
        int categoryID;
        CategoryEntity categoryEntity = categoryRepository.findByName(categogyName);
        if (categoryEntity == null) {
            CategoryEntity newCategory = new CategoryEntity();
            newCategory.setName(categogyName);
            newCategory.setType("NewCategogy");
            newCategory.setStatus("NEW");
            categoryRepository.saveAndFlush(newCategory);
            categoryEntity = categoryRepository.findByName(categogyName);
        }
        categoryID = categoryEntity.getId();
        return categoryID;
    }

    private void addBike(int catelogyId, List<Map<String,String>> listBike){
        for (Map<String,String> bikeDetail:listBike){
            BikeEntity newBike = new BikeEntity();
            newBike.setName(bikeDetail.get("name"));
            newBike.setUrl(bikeDetail.get("url"));
            newBike.setCategoryId(catelogyId);
            newBike.setBrand(bikeDetail.get("brand"));
            newBike.setStatus(bikeDetail.get("status"));
            newBike.setDescription(bikeDetail.get("description"));
            newBike.setVersion(bikeDetail.get("version"));
            newBike.setPrice(bikeDetail.get("price"));
            String hashAccessoryCode = newBike.hashCode()+"";
            newBike.setHashBikeCode(hashAccessoryCode);
            boolean addBike = checkDuplicateBike(newBike);
            if (addBike){
                bikeRepository.saveAndFlush(newBike);
                BikeEntity ownBike = bikeRepository.findByHashBikeCode(hashAccessoryCode);
                ImageEntity newImage = new ImageEntity();
                newImage.setUrl(bikeDetail.get("image"));
                newImage.setOwnId(ownBike.getId());
                newImage.setStatus("NEW");
                newImage.setType("Bike");
                imageRepository.saveAndFlush(newImage);
            }
        }
    }

    private boolean checkDuplicateBike(BikeEntity newBike){
        List<BikeEntity> listExitBikes = bikeRepository.findAll();
        for (BikeEntity bikeEntity:listExitBikes){
            int exit = Integer.parseInt(bikeEntity.getHashBikeCode());
            int bike = Integer.parseInt(newBike.getHashBikeCode());
            if (exit == bike) {
                if (!bikeEntity.getUrl().equals(newBike.getUrl()) || !bikeEntity.getDescription().
                        equals(newBike.getDescription()) || !bikeEntity.getPrice().equals(newBike.getPrice())){
                    bikeEntity.setPrice(newBike.getPrice());
                    bikeEntity.setDescription(newBike.getDescription());
                    bikeEntity.setUrl(newBike.getUrl());
                    bikeRepository.saveAndFlush(bikeEntity);
                }
                return false;
            }
        }
        return true;
    }
}
