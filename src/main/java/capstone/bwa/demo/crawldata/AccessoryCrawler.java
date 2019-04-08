package capstone.bwa.demo.crawldata;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.AccessoryEntity;
import capstone.bwa.demo.entities.CategoryEntity;
import capstone.bwa.demo.entities.ImageEntity;
import capstone.bwa.demo.entities.ReferencesLinkEntity;
import capstone.bwa.demo.repositories.AccessoryRepository;
import capstone.bwa.demo.repositories.CategoryRepository;
import capstone.bwa.demo.repositories.ImageRepository;
import capstone.bwa.demo.repositories.ReferencesLinkRepository;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessoryCrawler {

    private static final Logger logger = LoggerFactory.getLogger(AccessoryCrawler.class);
    private CategoryRepository categoryRepository;
    private AccessoryRepository accessoryRepository;
    private ImageRepository imageRepository;
    private ReferencesLinkRepository referencesLinkRepository;

    public AccessoryCrawler(CategoryRepository categoryRepository, AccessoryRepository accessoryRepository, ImageRepository imageRepository, ReferencesLinkRepository referencesLinkRepository) {
        this.categoryRepository = categoryRepository;
        this.accessoryRepository = accessoryRepository;
        this.imageRepository = imageRepository;
        this.referencesLinkRepository = referencesLinkRepository;
    }

    public void crawAndInsertDB() {
        String domain = "https://shop2banh.vn/";

        if (!referencesLinkRepository.existsByUrl(domain)) {
            ReferencesLinkEntity referencesLinkEntity = new ReferencesLinkEntity();
            referencesLinkEntity.setUrl(domain);
            referencesLinkRepository.saveAndFlush(referencesLinkEntity);
        }

        Map<String, String> selectorMap = new HashMap<>();
        selectorMap.put("category", "section .submenu-lv1 a");
        selectorMap.put("paging", ".pager > a");
        selectorMap.put("productList", ".items");
        selectorMap.put("name", ".title-prod a");
        selectorMap.put("price", ".price-new-prod");
        selectorMap.put("url", ".title-prod a");
        selectorMap.put("image", ".img-prod img");
        selectorMap.put("status", MainConstants.STATUS_ACCESSORY);
        selectorMap.put("description", ".description-detail");
        selectorMap.put("bike-relation", ".right-detail .external a");
        selectorMap.put("domain", "https://shop2banh.vn/");


        try {
            Document doc = Jsoup.connect(domain).timeout(10 * 1000).get();
            Elements elements = doc.select(selectorMap.get("category"));
            List<CategoryEntity> categoryEntityList = new ArrayList<>();
            for (int i = 0; i < elements.size(); i++) {
                CategoryEntity categoryEntity = new CategoryEntity();
                categoryEntity.setName(elements.get(i).text());
                categoryEntity.setStatus(MainConstants.CATEGORY_ACTIVE);
                categoryEntity.setType(MainConstants.STATUS_ACCESSORY);

                try {
                    Document productPage = Jsoup.connect(elements.get(i).attr("href")).timeout(30 * 1000).get();

                    //get the first page
                    List<AccessoryEntity> accessoryEntityList = getAccessoryFromLink(productPage, selectorMap);
                    categoryEntity.setAccessoriesById(accessoryEntityList);

                    //get another page
                    Elements pagerElement = productPage.select(selectorMap.get("paging"));
                    for (int j = 1; j < pagerElement.size() - 2; j++) {

                        productPage = Jsoup.connect(pagerElement.get(j).attr("href")).timeout(30 * 1000).get();
                        accessoryEntityList = getAccessoryFromLink(productPage, selectorMap);
                        categoryEntity.getAccessoriesById().addAll(accessoryEntityList);
                    }

                    if (!categoryEntity.getAccessoriesById().isEmpty()) {
                        categoryEntityList.add(categoryEntity);
                    }

                } catch (IOException e) {
                    logger.error("FAIL to load " + elements.get(i).attr("href"));
                }
            }

            insertToDB(categoryEntityList);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void insertToDB(List<CategoryEntity> categoryEntityList) {

        for (CategoryEntity entity : categoryEntityList) {

            try {
                CategoryEntity categoryDB = categoryRepository.findByNameIgnoreCaseAndType(entity.getName().trim().toUpperCase(), MainConstants.STATUS_ACCESSORY);
                if (categoryDB == null) {
                    categoryDB = categoryRepository.saveAndFlush(entity);
                }

                int categoryId = categoryDB.getId();

                for (AccessoryEntity accessoryEntity : entity.getAccessoriesById()) {

                    AccessoryEntity tmp = accessoryRepository.findByHashAccessoryCode(accessoryEntity.hashCode() + "");
                    if (tmp == null) {
                        accessoryEntity.setCategoryId(categoryId);
                        accessoryEntity.setHashAccessoryCode(accessoryEntity.hashCode() + "");
                        try {
                            accessoryRepository.save(accessoryEntity);
//                            int accessoryId = accessoryRepository.saveAndFlush(accessoryEntity).getId();

//                            for (ImageEntity imageEntity : accessoryEntity.getImagesById()) {
//                                imageEntity.setOwnId(accessoryId);
//                                imageEntity.setType(MainConstants.STATUS_ACCESSORY);
//                                imageRepository.saveAndFlush(imageEntity);
//                            }
                        } catch (Exception ex) {
                            logger.error(ex.getMessage() + " " + accessoryEntity.getUrl());
                        }
                    }
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage() + " " + entity.getName());
            }
        }
    }

    public List<AccessoryEntity> getAccessoryFromLink(Document doc, Map<String, String> selectorMap) {
        List<AccessoryEntity> bikeEntities = new ArrayList<>();

        Elements productElements = doc.select(selectorMap.get("productList"));
        for (Element element : productElements) {
            try {
                AccessoryEntity accessoryEntity = new AccessoryEntity();

                accessoryEntity.setName(element.select(selectorMap.get("name")).text());
                String price = element.select(selectorMap.get("price")).text().replaceAll("[^\\d]", "");
                if (price.isEmpty()) {
                    accessoryEntity.setPrice("Liên hệ");
                } else {
                    accessoryEntity.setPrice(price);
                }
                accessoryEntity.setUrl(element.select(selectorMap.get("url")).attr("href"));
                accessoryEntity.setStatus(selectorMap.get("status"));

//                ImageEntity imageEntity = new ImageEntity();
//                imageEntity.setUrl(element.select(selectorMap.get("image")).attr("src"));
//                List<ImageEntity> imageEntityList = new ArrayList<>();
//                imageEntityList.add(imageEntity);
//                accessoryEntity.setImagesById(imageEntityList);

                accessoryEntity.setDescription(getAccessoryDescription(element.select(selectorMap.get("url")).attr("href"), selectorMap));
                bikeEntities.add(accessoryEntity);
            } catch (IOException ex) {
                logger.error("FAIL to load " + element.select(selectorMap.get("url")).attr("href"));
            }
        }

        return bikeEntities;
    }

    public String getAccessoryDescription(String url, Map<String, String> selectorMap) throws IOException {
        String result = "";
        Map<String, String> map = new HashMap<>();
        System.out.println(url);

        Document doc = null;

        doc = Jsoup.connect(url).timeout(10 * 1000).get();
        Elements descriptionElements = doc.select(selectorMap.get("description"));
        for (Element element : descriptionElements) {
            result += element.text() + "; ";
        }

        map.put("feature", result);

        result = "";
        Elements bikeCompatibleElements = doc.select(selectorMap.get("bike-relation"));
        for (Element element : bikeCompatibleElements) {
            result += element.text() + "; ";
        }
        map.put("bikeCompatible", result);

        return new Gson().toJson(map);
    }
}
