package capstone.bwa.demo.controllers;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.crawlmodel.CrawlAccessory;
import capstone.bwa.demo.crawlmodel.CrawlBike;
import capstone.bwa.demo.crawlmodel.CrawlNews;
import capstone.bwa.demo.crawlmodel.DBSetup;
import capstone.bwa.demo.entities.*;
import capstone.bwa.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private ReferencesLinkRepository referencesLinkRepository;

    private final String statusActive = "ACTIVE";
    private final String typeBike = "BIKE";
    private final String typeAccessory = "ACCESSORY";
    private final String typeNews = "NEWS";

    @GetMapping("admin/crawl/bike")
    public ResponseEntity crawlBike() {
        DBSetup dbSetup = new DBSetup();

        //tạo db nếu chưa có
        setCategoriesAndReferenceLinksDB();

        //lấy list link theo category với type bike
        List<String> listLink = dbSetup.listLinksXeSo();
        //lấy category theo tên category
        CategoryEntity categoryEntity = categoryRepository.findByName("Xe Số");
        //crawl bike theo category và list link của category đó
        crawlBike(categoryEntity, listLink);

        listLink = dbSetup.listLinksXeTayGa();
        categoryEntity = categoryRepository.findByName("Xe Tay Ga");
        crawlBike(categoryEntity, listLink);

        listLink = dbSetup.listLinksXeConTay();
        categoryEntity = categoryRepository.findByName("Xe Côn Tay");
        crawlBike(categoryEntity, listLink);

        listLink = dbSetup.listLinksXeMoTo();
        categoryEntity = categoryRepository.findByName("Xe Mô Tô");
        crawlBike(categoryEntity, listLink);

        //list link special không chia theo category xe
        listLink = dbSetup.listLinksSpecial();
        crawlBikeWithSpecialLink(listLink);

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("admin/crawl/accessory")
    public ResponseEntity crawlAccessory() {
        DBSetup dbSetup = new DBSetup();

        //tạo db nếu chưa có
        setCategoriesAndReferenceLinksDB();

        //lấy list link theo category với type accessory
        List<String> listLinks = dbSetup.listLinksAccessoryDan();
        //lấy category theo tên category
        CategoryEntity categoryEntity = categoryRepository.findByName("Honda Phụ Kiện Dán");
        //crawl accessory theo category và list link của category đó
        crawlAccessory(categoryEntity, listLinks);

        listLinks = dbSetup.listLinksAccessoryOp();
        categoryEntity = categoryRepository.findByName("Honda Phụ Kiện Ốp");
        crawlAccessory(categoryEntity, listLinks);

        listLinks = dbSetup.listLinksAccessoryLapThem();
        categoryEntity = categoryRepository.findByName("Honda Phụ Kiện Lắp Thêm");
        crawlAccessory(categoryEntity, listLinks);

        listLinks = dbSetup.listLinksAccessoryThayThe();
        categoryEntity = categoryRepository.findByName("Honda Phụ Kiện Thay Thế");
        crawlAccessory(categoryEntity, listLinks);

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("admin/crawl/news")
    public ResponseEntity crawlNews() {
        DBSetup dbSetup = new DBSetup();
        setCategoriesAndReferenceLinksDB();
        List<String> listLinks = dbSetup.listLinksNews();
        CategoryEntity categoryEntity = categoryRepository.findByName("Tin Tức");
        //crawl news theo list link
        crawlNews(categoryEntity, listLinks);
        return new ResponseEntity(HttpStatus.OK);
    }

    private void crawlNews(CategoryEntity categoryEntity, List<String> listLink) {
        for (String url : listLink) {
            CrawlNews crawler = new CrawlNews();
            //set link dùng để crawl
            crawler.setDomain(url);
            crawler.crawlNews();
            crawler.getResults().forEach((newsEntity, imageEntity) -> {
                //kiểm tra trùng
                if (!newsRepository.existsByTitle(newsEntity.getTitle())) {
                    //set news infor và lưu
                    newsEntity.setCategoryId(categoryEntity.getId());
                    newsRepository.save(newsEntity);
                    System.out.println("News " + newsEntity.getTitle());
                    // set image info và lưu
                    imageEntity.setOwnId(newsEntity.getId());
                    imageEntity.setType(MainConstants.STATUS_NEWS);
                    imageEntity.setStatus(statusActive);
                    imageRepository.save(imageEntity);
                    System.out.println("Img " + imageEntity.getId());
                }
            });
        }
    }

    private void crawlBike(CategoryEntity categoryEntity, List<String> listLink) {
        for (String url : listLink) {
            CrawlBike crawler = new CrawlBike();
            List<Map<String, String>> listBikes = new ArrayList<>();
            //truyền vào url dùng để crawl
            crawler.crawlBike(url);
            //lấy list bike đã crawl
            listBikes = crawler.getListBikes();
            //thêm thông tin category và lưu bike nếu không trùng
            addBike(categoryEntity, listBikes);
        }
    }

    private void crawlBikeWithSpecialLink(List<String> listLink) {
        for (String url : listLink) {
            CrawlBike crawler = new CrawlBike();
            List<Map<String, String>> listBikes = new ArrayList<>();
            //truyền vào url dùng để crawl
            crawler.crawlBike(url);
            //special link không phân category riêng biệt nên khi crawl cần lưu bike theo các category bike
            CategoryEntity categoryEntity = categoryRepository.findByName("Xe Tay Ga");
            listBikes = crawler.getListXeCon();
            //thêm thông tin category và lưu
            addBike(categoryEntity, listBikes);

            categoryEntity = categoryRepository.findByName("Xe Tay Ga");
            listBikes = crawler.getListXeTayGa();
            addBike(categoryEntity, listBikes);

            categoryEntity = categoryRepository.findByName("Xe Mô Tô");
            listBikes = crawler.getListXeMoTo();
            addBike(categoryEntity, listBikes);

            categoryEntity = categoryRepository.findByName("Xe Số");
            listBikes = crawler.getListXeSo();
            addBike(categoryEntity, listBikes);
        }
    }

    private void crawlAccessory(CategoryEntity categoryEntity, List<String> listLink) {
        for (String url : listLink) {
            CrawlAccessory crawlAccessory = new CrawlAccessory();
            List<Map<String, String>> listAccessories = new ArrayList<>();
            //crawl accessory theo link danh sách accessory
            crawlAccessory.crawlAccessoryFromHonda(url);
            //lấy accessory info đã crawl
            listAccessories = crawlAccessory.getListAccessories();
            //thêm thông tin về category và lưu accessory
            addAccessory(categoryEntity, listAccessories);
        }
    }

    private void addAccessory(CategoryEntity categoryEntity, List<Map<String, String>> listBike) {
        for (Map<String, String> accessoryDetail : listBike) {
            AccessoryEntity newAccessory = new AccessoryEntity();
            //lấy thông tin về accessory từ map theo key
            newAccessory.setName(accessoryDetail.get("name"));
            newAccessory.setUrl(accessoryDetail.get("url"));
            newAccessory.setCategoryId(categoryEntity.getId());
            newAccessory.setBrand(accessoryDetail.get("brand"));
            newAccessory.setStatus(accessoryDetail.get("status"));
            newAccessory.setDescription(accessoryDetail.get("description"));
            newAccessory.setPrice(accessoryDetail.get("price"));
            //tạo hashCode
            String hashAccessoryCode = newAccessory.hashCode() + "";
            newAccessory.setHashAccessoryCode(hashAccessoryCode);
            newAccessory.setCategoryByCategoryId(categoryEntity);
            //kiểm tra trùng
            boolean addAccessory = checkDuplicateAccessory(newAccessory);
            if (addAccessory == false) {
                //lưu accessory nếu không trùng
                accessoryRepository.saveAndFlush(newAccessory);
                AccessoryEntity ownAccessory = accessoryRepository.findByHashAccessoryCode(hashAccessoryCode);
                //thêm image theo accessory
                ImageEntity newImage = new ImageEntity();
                newImage.setUrl(accessoryDetail.get("image"));
                newImage.setOwnId(ownAccessory.getId());
                newImage.setStatus(statusActive);
                newImage.setType("ACCESSORY");
                imageRepository.saveAndFlush(newImage);
            }
        }
    }

    private void addBike(CategoryEntity categoryEntity, List<Map<String, String>> listBike) {
        for (Map<String, String> bikeDetail : listBike) {
            BikeEntity newBike = new BikeEntity();
            //lấy thông tin bike từ map theo key
            newBike.setName(bikeDetail.get("name"));
            newBike.setUrl(bikeDetail.get("url"));
            newBike.setCategoryId(categoryEntity.getId());
            newBike.setCategoryByCategoryId(categoryEntity);
            newBike.setBrand(bikeDetail.get("brand"));
            newBike.setStatus(bikeDetail.get("status"));
            newBike.setDescription(bikeDetail.get("description"));
            newBike.setVersion(bikeDetail.get("version"));
            newBike.setPrice(bikeDetail.get("price"));
            //tạo hashCode
            String hashAccessoryCode = newBike.hashCode() + "";
            newBike.setHashBikeCode(hashAccessoryCode);
            //kiểm tra trùng
            boolean addBike = checkDuplicateBike(newBike);
            if (addBike == false) {
                //lưu bike nếu không trùng
                bikeRepository.saveAndFlush(newBike);
                BikeEntity ownBike = bikeRepository.findByHashBikeCode(hashAccessoryCode);
                //thêm Image theo bike
                ImageEntity newImage = new ImageEntity();
                newImage.setUrl(bikeDetail.get("image"));
                newImage.setOwnId(ownBike.getId());
                newImage.setStatus(statusActive);
                newImage.setType("BIKE");
                imageRepository.saveAndFlush(newImage);
            }
        }
    }

    private boolean checkDuplicateAccessory(AccessoryEntity newAccessory) {
        //kiểm tra trùng theo hashCode
        boolean exited = accessoryRepository.existsByHashAccessoryCode(newAccessory.getHashAccessoryCode());
        if (exited == true) {
            //nếu trùng thì kiểm tra price và description
            AccessoryEntity exitAccessory = accessoryRepository.findByHashAccessoryCode(newAccessory.getHashAccessoryCode());
            if (!exitAccessory.getPrice().equals(newAccessory.getPrice())
                    || !exitAccessory.getDescription().equals(newAccessory.getDescription())) {
                //thay đổi thông tin của accessory (price và description) nếu price hoặc descripton thay đổi
                exitAccessory.setDescription(newAccessory.getDescription());
                exitAccessory.setPrice(newAccessory.getPrice());
                //update accessory
                accessoryRepository.saveAndFlush(exitAccessory);
            }
            return true;
        }
        return false;
    }

    private boolean checkDuplicateBike(BikeEntity newBike) {
        //kiểm tra trùng theo hashCode
        boolean exited = bikeRepository.existsByHashBikeCode(newBike.getHashBikeCode());
        if (exited == true) {
            //nếu trùng thì kiểm tra price và description
            BikeEntity exitBike = bikeRepository.findByHashBikeCode(newBike.getHashBikeCode());
            if (!exitBike.getPrice().equals(newBike.getPrice())
                    || !exitBike.getDescription().equals(newBike.getDescription())) {
                //thay đổi thông tin của bike (price và description) nếu price hoặc descripton thay đổi
                exitBike.setDescription(newBike.getDescription());
                exitBike.setPrice(newBike.getPrice());
                //update bike
                bikeRepository.saveAndFlush(exitBike);
            }
            return true;
        }
        return false;
    }

    public void setCategoriesAndReferenceLinksDB() {
        //thêm category và reference nếu chưa có
        DBSetup dbSetup = new DBSetup();
        //Lấy list category và lưu nếu chưa có
        List<String> listCategory = dbSetup.listCategoryBike();
        listCategory(listCategory, typeBike);

        listCategory = dbSetup.listCategoryAccessory();
        listCategory(listCategory, typeAccessory);

        listCategory = dbSetup.listCategoryNews();
        listCategory(listCategory, typeNews);

        //Lấy list reference vaf lưu nếu chưa có
        List<String> listLinkCategory = dbSetup.listLinksAccessoryOp();
        listReferenceLink(listLinkCategory, "Honda Phụ Kiện Ốp");

        listLinkCategory = dbSetup.listLinksAccessoryLapThem();
        listReferenceLink(listLinkCategory, "Honda Phụ Kiện Lắp Thêm");

        listLinkCategory = dbSetup.listLinksAccessoryThayThe();
        listReferenceLink(listLinkCategory, "Honda Phụ Kiện Thay Thế");

        listLinkCategory = dbSetup.listLinksAccessoryDan();
        listReferenceLink(listLinkCategory, "Honda Phụ Kiện Dán");

        listLinkCategory = dbSetup.listLinksSpecial();
        listReferenceLink(listLinkCategory, "Special");

        listLinkCategory = dbSetup.listLinksNews();
        listReferenceLink(listLinkCategory, "Tin Tức");

        listLinkCategory = dbSetup.listLinksXeConTay();
        listReferenceLink(listLinkCategory, "Xe Côn Tay");

        listLinkCategory = dbSetup.listLinksXeTayGa();
        listReferenceLink(listLinkCategory, "Xe Tay Ga");

        listLinkCategory = dbSetup.listLinksXeSo();
        listReferenceLink(listLinkCategory, "Xe Số");

        listLinkCategory = dbSetup.listLinksXeMoTo();
        listReferenceLink(listLinkCategory, "Xe Mô Tô");
    }

    private void listReferenceLink(List<String> listLinkCategory, String categoryName) {
        for (String url : listLinkCategory) {
            //kiểm tra reference có hay chưa
            boolean exited = referencesLinkRepository.existsByUrl(url);
            if (exited == false) {
                //thêm nếu chưa có
                ReferencesLinkEntity referencesLinkEntity = new ReferencesLinkEntity();
                referencesLinkEntity.setUrl(url);
                referencesLinkEntity.setStatus(statusActive);
                //lấy category theo tên category để gán
                CategoryEntity categoryEntity = categoryRepository.findByName(categoryName);
                referencesLinkEntity.setCategoryId(categoryEntity.getId());
                referencesLinkEntity.setCategoryByCategoryId(categoryEntity);

                referencesLinkRepository.save(referencesLinkEntity);
            }
        }
    }

    private void listCategory(List<String> listCategory, String typeBike) {
        for (String name : listCategory) {
            //kiểm tra category có hay chưa
            if (categoryRepository.findByName(name) == null) {
                //thêm nếu chưa có
                CategoryEntity categoryEntity = new CategoryEntity();
                categoryEntity.setType(typeBike);
                categoryEntity.setStatus(statusActive);
                categoryEntity.setName(name);
                categoryRepository.save(categoryEntity);
            }
        }
    }

}
