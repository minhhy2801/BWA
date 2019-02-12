package capstone.bwa.demo.controllers;

import capstone.bwa.demo.crawlmodel.BikeHondaCrawler;
import capstone.bwa.demo.entities.*;
import capstone.bwa.demo.repositories.*;
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

    @Autowired
    private NewsRepository newsRepository;

    @GetMapping("crawlBike")
    public ResponseEntity crawlBike() {
        BikeHondaCrawler crawlBikeHonda = new BikeHondaCrawler();
        crawlBikeHonda.crawlBike();
        Map<BikeEntity, ImageEntity> listNewBike = crawlBikeHonda.getBikeList();
        Map<String, Map<String, String>> mapBikeAndVersion = crawlBikeHonda.getMapBikeAndVersion();
        if (listNewBike.size() == 0) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        List<BikeEntity> listExitBike = bikeRepository.findAll();
        boolean addNewProduct;
        for (Map.Entry<BikeEntity, ImageEntity> entry : listNewBike.entrySet()) {
            BikeEntity newBike = entry.getKey();
            ImageEntity imageEntity = entry.getValue();
            addNewProduct = true;
            for (BikeEntity bike : listExitBike) {
                if (bike.getHashBikeCode().equals(newBike.getHashBikeCode())) {
                    addNewProduct = false;
                }
            }
            if (addNewProduct) {
                Map<String, String> versionAndPrice = mapBikeAndVersion.get(newBike.getUrl());
                Map<String, Map<String, String>> categoryMap = crawlBikeHonda.getCategoryAndProduct();
                for (Map.Entry<String, Map<String, String>> mapEntry : categoryMap.entrySet()) {
                    Map<String, String> linkAndName = mapEntry.getValue();
                    if (mapEntry.getValue().keySet().contains(newBike.getUrl())) {
                        int categoryId = checkCategory(mapEntry.getKey());
                        newBike.setCategoryId(categoryId);
                    }
                    String name = linkAndName.get(newBike.getUrl());
                    newBike.setName(name);
                }
                for (Map.Entry<String, String> version : versionAndPrice.entrySet()) {
                    newBike.setVersion(version.getKey());
                    newBike.setPrice(version.getValue());
                    String hashCode = newBike.hashCode() + "";
                    newBike.setHashBikeCode(hashCode);
                    bikeRepository.saveAndFlush(newBike);
                }
                BikeEntity bikeOwn = bikeRepository.findByHashBikeCode(newBike.getHashBikeCode());
                imageEntity.setOwnId(bikeOwn.getId());
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
//        ImageEntity image = new ImageEntity();
//        image.setUrl("http");
//        NewsEntity news = newsRepository.findById(1);
//        image.setNewsByOwnId(news);
//        imageRepository.saveAndFlush(image);
//        return new ResponseEntity(image, HttpStatus.OK);
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
}
