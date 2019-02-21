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
import java.util.HashMap;
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

    @Autowired
    private ReferencesLinkRepository referencesLinkRepository;

    private final String statusActive = "ACTIVE";

    @GetMapping("admin/crawl/data")
    public ResponseEntity crawlData() {
        createCategory("Xe Côn Tay", "BIKE");
        createCategory("Xe Tay Ga", "BIKE");
        createCategory("Xe Số", "BIKE");
        createCategory("Xe Mô Tô", "BIKE");
        createCategory("Phụ kiện thay thế Honda", "ACCESSORY");
        createCategory("Phụ kiện lắp thêm Honda", "ACCESSORY");
        createCategory("Phụ kiện ốp Honda", "ACCESSORY");
        createCategory("Phụ kiện dán Honda", "ACCESSORY");
        createCategory("Xe Honda", "BIKE");
        createCategory("Xe Kymco", "BIKE");
        createCategory("Xe Sym", "BIKE");
        createCategory("Tin Tức","NEWS");

        CategoryEntity categoryEntity = categoryRepository.findByName("Xe Tay Ga");
        String url = "https://yamaha-motor.com.vn/xe/loai-xe/xe-ga";
        createReferencesLink(url, categoryEntity);

        categoryEntity = categoryRepository.findByName("Xe Côn Tay");
        url = "https://yamaha-motor.com.vn/xe/loai-xe/xe-nhap-khau";
        createReferencesLink(url, categoryEntity);

        categoryEntity = categoryRepository.findByName("Xe Số");
        url = "https://yamaha-motor.com.vn/xe/loai-xe/xe-so";
        createReferencesLink(url, categoryEntity);

        categoryEntity = categoryRepository.findByName("Xe Mô Tô");
        url = "https://motoanhquoc.vn/inventory/?body=adventure&view_type=list";
        createReferencesLink(url, categoryEntity);
        url = "https://motoanhquoc.vn/inventory/?body=cruiser&view_type=list";
        createReferencesLink(url, categoryEntity);
        url = "https://motoanhquoc.vn/inventory/?body=modern-classics&view_type=list";
        createReferencesLink(url, categoryEntity);
        url = "https://motoanhquoc.vn/inventory/?body=roadsters&view_type=list";
        createReferencesLink(url, categoryEntity);
        url = "https://hondaxemay.com.vn/hondamoto/san-pham";
        createReferencesLink(url, categoryEntity);

        categoryEntity = categoryRepository.findByName("Xe Honda");
        url = "https://hondaxemay.com.vn/san-pham/";
        createReferencesLink(url, categoryEntity);

        categoryEntity = categoryRepository.findByName("Xe Kymco");
        url = "http://www.kymco.com.vn/san-pham";
        createReferencesLink(url, categoryEntity);

        categoryEntity = categoryRepository.findByName("Xe Sym");
        url = "http://www.sym.com.vn/san-pham.html";
        createReferencesLink(url, categoryEntity);

        categoryEntity = categoryRepository.findByName("Phụ kiện thay thế Honda");
        url = "https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3840&filter_orderby=latest&filter_tax=3721";
        createReferencesLink(url, categoryEntity);
        url = "https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3829&filter_orderby=latest&filter_tax=3721";
        createReferencesLink(url, categoryEntity);
        url = "https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3861&filter_orderby=latest&filter_tax=3721";
        createReferencesLink(url, categoryEntity);
        url = "https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3828&filter_orderby=latest&filter_tax=3721";
        createReferencesLink(url, categoryEntity);

        categoryEntity = categoryRepository.findByName("Phụ kiện lắp thêm Honda");
        url = "https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3840&filter_orderby=latest&filter_tax=3722";
        createReferencesLink(url, categoryEntity);
        url = "https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3829&filter_orderby=latest&filter_tax=3722";
        createReferencesLink(url, categoryEntity);
        url = "https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3861&filter_orderby=latest&filter_tax=3722";
        createReferencesLink(url, categoryEntity);
        url = "https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3828&filter_orderby=latest&filter_tax=3722";
        createReferencesLink(url, categoryEntity);

        categoryEntity = categoryRepository.findByName("Phụ kiện ốp Honda");
        url = "https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3828&filter_orderby=latest&filter_tax=3720";
        createReferencesLink(url, categoryEntity);
        url = "https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3861&filter_orderby=latest&filter_tax=3720";
        createReferencesLink(url, categoryEntity);
        url = "https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3829&filter_orderby=latest&filter_tax=3720";
        createReferencesLink(url, categoryEntity);
        url = "https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3840&filter_orderby=latest&filter_tax=3720";
        createReferencesLink(url, categoryEntity);

        categoryEntity = categoryRepository.findByName("Phụ kiện dán Honda");
        url = "https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3840&filter_orderby=latest&filter_tax=3719";
        createReferencesLink(url, categoryEntity);
        url = "https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3829&filter_orderby=latest&filter_tax=3719";
        createReferencesLink(url, categoryEntity);

        try {
            List<ReferencesLinkEntity> listReferencesLinkEntities = referencesLinkRepository.findByCategoryId(5);
            crawlAccessory(listReferencesLinkEntities);
            listReferencesLinkEntities = referencesLinkRepository.findByCategoryId(6);
            crawlAccessory(listReferencesLinkEntities);
            listReferencesLinkEntities = referencesLinkRepository.findByCategoryId(7);
            crawlAccessory(listReferencesLinkEntities);
            listReferencesLinkEntities = referencesLinkRepository.findByCategoryId(8);
            crawlAccessory(listReferencesLinkEntities);
        }catch (IOException e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        try {
            List<ReferencesLinkEntity> listReferencesLinkEntities = referencesLinkRepository.findByCategoryId(1);
            crawlBike(listReferencesLinkEntities);
            listReferencesLinkEntities = referencesLinkRepository.findByCategoryId(2);
            crawlBike(listReferencesLinkEntities);
            listReferencesLinkEntities = referencesLinkRepository.findByCategoryId(3);
            crawlBike(listReferencesLinkEntities);
            listReferencesLinkEntities = referencesLinkRepository.findByCategoryId(4);
            crawlBike(listReferencesLinkEntities);
            listReferencesLinkEntities = referencesLinkRepository.findByCategoryId(9);
            crawlBike(listReferencesLinkEntities);
            listReferencesLinkEntities = referencesLinkRepository.findByCategoryId(10);
            crawlBike(listReferencesLinkEntities);
            listReferencesLinkEntities = referencesLinkRepository.findByCategoryId(11);
            crawlBike(listReferencesLinkEntities);
        }catch (IOException e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(bikeRepository.findAll(), HttpStatus.OK);
    }

    private void crawlBike(List<ReferencesLinkEntity> listReferencesLinkEntities) throws IOException{
        for (ReferencesLinkEntity referencesLinkEntity : listReferencesLinkEntities) {
            BikeHondaCrawler crawler = new BikeHondaCrawler();
            List<Map<String, String>> listBikes = new ArrayList<>();
            crawler.crawlBike(referencesLinkEntity.getUrl(),referencesLinkEntity.getCategoryByCategoryId().getName());
            if (!crawler.getListBikes().isEmpty()){
                listBikes = crawler.getListBikes();
                addBike(referencesLinkEntity.getCategoryId(), listBikes);
            }else{
                listBikes = crawler.getListXeCon();
                addBike(1, listBikes);
                listBikes = crawler.getListXeTayGa();
                addBike(2, listBikes);
                listBikes = crawler.getListXeMoTo();
                addBike(4, listBikes);
                listBikes = crawler.getListXeSo();
                addBike(3, listBikes);
            }
        }
    }

    private void crawlAccessory(List<ReferencesLinkEntity> listReferencesLinkEntities) throws IOException {
        for (ReferencesLinkEntity referencesLinkEntity : listReferencesLinkEntities) {
            BikeHondaCrawler crawler = new BikeHondaCrawler();
            List<Map<String, String>> listAccessories = new ArrayList<>();
            crawler.crawlAccessoryHonda(referencesLinkEntity.getUrl());
            listAccessories = crawler.getListAccessories();
            addAccessory(referencesLinkEntity.getCategoryId(), listAccessories);
        }
    }

    private void createCategory(String name, String type) {
        if (categoryRepository.findByName(name) == null) {
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setName(name);
            categoryEntity.setType(type);
            categoryEntity.setStatus(statusActive);
            categoryRepository.saveAndFlush(categoryEntity);
        }
    }

    private void createReferencesLink(String url, CategoryEntity categoryEntity) {

        ReferencesLinkEntity referencesLinkEntity = new ReferencesLinkEntity();
        referencesLinkEntity.setCategoryId(categoryEntity.getId());
        referencesLinkEntity.setCategoryByCategoryId(categoryEntity);
        referencesLinkEntity.setStatus(statusActive);
        referencesLinkEntity.setUrl(url);
        referencesLinkRepository.saveAndFlush(referencesLinkEntity);
    }

    private void addAccessory(int catelogyId, List<Map<String, String>> listBike) {
        for (Map<String, String> accessoryDetail : listBike) {
            AccessoryEntity newAccessory = new AccessoryEntity();
            newAccessory.setName(accessoryDetail.get("name"));
            newAccessory.setUrl(accessoryDetail.get("url"));
            newAccessory.setCategoryId(catelogyId);
            newAccessory.setBrand(accessoryDetail.get("brand"));
            newAccessory.setStatus(accessoryDetail.get("status"));
            newAccessory.setDescription(accessoryDetail.get("description"));
            newAccessory.setPrice(accessoryDetail.get("price"));
            String hashAccessoryCode = newAccessory.hashCode() + "";
            newAccessory.setHashAccessoryCode(hashAccessoryCode);
            accessoryRepository.saveAndFlush(newAccessory);
            AccessoryEntity ownAccessory = accessoryRepository.findByHashAccessoryCode(hashAccessoryCode);
            ImageEntity newImage = new ImageEntity();
            newImage.setUrl(accessoryDetail.get("image"));
            newImage.setOwnId(ownAccessory.getId());
            newImage.setStatus(statusActive);
            newImage.setType("ACCESSORY");
            imageRepository.saveAndFlush(newImage);
        }
    }

    private void addBike(int catelogyId, List<Map<String, String>> listBike) {
        for (Map<String, String> bikeDetail : listBike) {
            BikeEntity newBike = new BikeEntity();
            newBike.setName(bikeDetail.get("name"));
            newBike.setUrl(bikeDetail.get("url"));
            newBike.setCategoryId(catelogyId);
            newBike.setBrand(bikeDetail.get("brand"));
            newBike.setStatus(bikeDetail.get("status"));
            newBike.setDescription(bikeDetail.get("description"));
            newBike.setVersion(bikeDetail.get("version"));
            newBike.setPrice(bikeDetail.get("price"));
            String hashAccessoryCode = newBike.hashCode() + "";
            newBike.setHashBikeCode(hashAccessoryCode);
            boolean addBike = checkDuplicateBike(newBike);
            if (addBike) {
                bikeRepository.saveAndFlush(newBike);
                BikeEntity ownBike = bikeRepository.findByHashBikeCode(hashAccessoryCode);
                ImageEntity newImage = new ImageEntity();
                newImage.setUrl(bikeDetail.get("image"));
                newImage.setOwnId(ownBike.getId());
                newImage.setStatus(statusActive);
                newImage.setType("BIKE");
                imageRepository.saveAndFlush(newImage);
            }
        }

    }

    private boolean checkDuplicateBike(BikeEntity newBike) {
        List<BikeEntity> listExitBikes = bikeRepository.findAll();
        for (BikeEntity bikeEntity : listExitBikes) {
            int exit = Integer.parseInt(bikeEntity.getHashBikeCode());
            int bike = Integer.parseInt(newBike.getHashBikeCode());
            if (exit == bike) {
                if (!bikeEntity.getUrl().equals(newBike.getUrl()) || !bikeEntity.getDescription().
                        equals(newBike.getDescription()) || !bikeEntity.getPrice().equals(newBike.getPrice())) {
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
