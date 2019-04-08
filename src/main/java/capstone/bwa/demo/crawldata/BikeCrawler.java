package capstone.bwa.demo.crawldata;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.BikeEntity;
import capstone.bwa.demo.entities.CategoryEntity;
import capstone.bwa.demo.entities.ReferencesLinkEntity;
import capstone.bwa.demo.repositories.BikeRepository;
import capstone.bwa.demo.repositories.CategoryRepository;
import capstone.bwa.demo.repositories.ImageRepository;
import capstone.bwa.demo.repositories.ReferencesLinkRepository;
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

public class BikeCrawler {
    private static final Logger logger = LoggerFactory.getLogger(BikeCrawler.class);

    private CategoryRepository categoryRepository;
    private BikeRepository bikeRepository;
    private ImageRepository imageRepository;
    private ReferencesLinkRepository referencesLinkRepository;


    public BikeCrawler(CategoryRepository categoryRepository, BikeRepository bikeRepository, ImageRepository imageRepository, ReferencesLinkRepository referencesLinkRepository) {
        this.categoryRepository = categoryRepository;
        this.bikeRepository = bikeRepository;
        this.imageRepository = imageRepository;
        this.referencesLinkRepository = referencesLinkRepository;
    }

    public void crawlAndInsertDB() {
        crawlAndInsertDBGiaXe2Banh();
        crawAndInsertDBMoToanQuoc();
    }

    public void crawlAndInsertDBGiaXe2Banh() {
        String domain = "https://giaxe.2banh.vn/";
        if (!referencesLinkRepository.existsByUrl(domain)) {
            ReferencesLinkEntity referencesLinkEntity = new ReferencesLinkEntity();
            referencesLinkEntity.setUrl(domain);
            referencesLinkRepository.save(referencesLinkEntity);
        }
        Map<String, String> selectorMap = new HashMap<>();
        selectorMap.put("menu", "nav > ul h2 > a");
        selectorMap.put("category", ".brand-menu-items a");
        selectorMap.put("productList", ".group-brand-items > div");
        selectorMap.put("productItem", ".bike-items");
        selectorMap.put("brand", ".giaxe-breadcrumb > div:nth-child(1) > div");
        selectorMap.put("name", ".bike-item-info .title");
        selectorMap.put("price", ".bike-item-info .price");
        selectorMap.put("url", ".bike-item-info a");
        selectorMap.put("image", ".bike-item-img img");
        selectorMap.put("status", MainConstants.STATUS_BIKE);
        selectorMap.put("description", ".version-price-block td");
        selectorMap.put("domain", "https://giaxe.2banh.vn/");

        List<CategoryEntity> categoryEntityList = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(domain).timeout(30 * 1000).get();

            Elements elements = doc.select(selectorMap.get("menu"));

            for (Element element : elements) {

                try {
                    Document productPage = Jsoup.connect(element.attr("href")).timeout(30 * 1000).get();

                    Elements categoryElements = productPage.select(selectorMap.get("category"));
                    Elements bikeElements = productPage.select(selectorMap.get("productList"));
                    String brand = productPage.select(selectorMap.get("brand")).text();
                    for (int i = 1; i < categoryElements.size(); i++) {

                        Element tabpane = getTabPane(categoryElements.get(i), bikeElements);
                        if (tabpane != null) {
                            CategoryEntity categoryEntity = new CategoryEntity();
                            categoryEntity.setName(categoryElements.get(i).text().trim().toUpperCase());
                            categoryEntity.setType(MainConstants.STATUS_BIKE);
                            categoryEntity.setStatus(MainConstants.CATEGORY_ACTIVE);

                            List<BikeEntity> bikeEntities = getBikesFromLink(tabpane, selectorMap, brand);
                            categoryEntity.setBikesById(bikeEntities);
                            categoryEntityList.add(categoryEntity);
                        }
                    }
                } catch (IOException ex) {
//                    ex.printStackTrace();
                    logger.error(ex.getMessage());
                }
            }

        } catch (IOException e) {
//            e.printStackTrace();
            logger.error(e.getMessage());
        }
        insertToDB(categoryEntityList);
    }

    public void crawAndInsertDBMoToanQuoc() {
        String domain = "https://motoanhquoc.vn/";
        if (!referencesLinkRepository.existsByUrl(domain)) {
            ReferencesLinkEntity referencesLinkEntity = new ReferencesLinkEntity();
            referencesLinkEntity.setUrl(domain);
            referencesLinkRepository.save(referencesLinkEntity);
        }
        Map<String, String> selectorMap = new HashMap<>();
        selectorMap.put("menu", "#menu-item-4525 > ul > li");
        selectorMap.put("productItem", ".stm-isotope-sorting-list .image > a");
        selectorMap.put("productList", ".ubermenu-item-layout-text_only");
        selectorMap.put("brand", ".ubermenu-item-layout-text_only > .ubermenu-target-title");
        selectorMap.put("name", "h1.title");
        selectorMap.put("price", ".price_unit");
        selectorMap.put("image", ".wp-post-image");
        selectorMap.put("status", MainConstants.STATUS_BIKE);
        selectorMap.put("descriptionTable", ".vc_tta-panels > div:nth-child(2)");
        selectorMap.put("description", "tr");
        selectorMap.put("domain", "https://giaxe.2banh.vn/");

        List<CategoryEntity> categoryEntityList = new ArrayList<>();
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName("PKL");
        categoryEntity.setType(MainConstants.STATUS_BIKE);
        categoryEntity.setStatus(MainConstants.CATEGORY_ACTIVE);
        categoryEntityList.add(categoryEntity);

        List<BikeEntity> bikeEntities = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(domain).timeout(10 * 1000).get();
            Elements productBrandElements = doc.select(selectorMap.get("menu"));

            for (Element brandElement : productBrandElements) {
                String brand = brandElement.selectFirst(selectorMap.get("brand")).text();
                Document productListElement = Jsoup.connect(brandElement.selectFirst(selectorMap.get("productList")).attr("href")).timeout(30 * 1000).get();

                Elements productElements = productListElement.select(selectorMap.get("productItem"));

                for (Element productElement : productElements) {
                    try {

                        Document element = Jsoup.connect(productElement.attr("href")).timeout(30 * 1000).get();
                        System.out.println(productElement.attr("href"));
                        BikeEntity bikeEntity = new BikeEntity();

                        bikeEntity.setBrand(brand);
                        bikeEntity.setName(element.select(selectorMap.get("name")).text());
                        bikeEntity.setVersion(element.select(selectorMap.get("name")).text());
                        String price = element.select(selectorMap.get("price")).text().replaceAll("[^\\d]", "");
                        if (price.isEmpty()) {
                            bikeEntity.setPrice("Liên hệ");
                        } else {
                            bikeEntity.setPrice(price);
                        }
                        bikeEntity.setUrl(productElement.attr("href"));
                        bikeEntity.setStatus(selectorMap.get("status"));

//                        ImageEntity imageEntity = new ImageEntity();
//                        imageEntity.setUrl(element.select(selectorMap.get("image")).attr("src"));
//                        List<ImageEntity> imageEntityList = new ArrayList<>();
//                        imageEntityList.add(imageEntity);
//                        bikeEntity.setImagesById(imageEntityList);

                        bikeEntity.setDescription(getBikeDescription(element.selectFirst(selectorMap.get("descriptionTable")), selectorMap.get("description")));
                        bikeEntities.add(bikeEntity);

                    } catch (IOException ex) {
                        logger.error(ex.getMessage() + " " + productElement.attr("href"));
                    }
                }
            }

            categoryEntity.setBikesById(bikeEntities);

        } catch (IOException ex) {
            logger.error(ex.getMessage() + " " + domain);
        }

        insertToDB(categoryEntityList);

    }

    public Element getTabPane(Element category, Elements tabpanes) {

        for (Element element : tabpanes) {

            if (category.attr("href").contains(element.attr("id").trim()))
                return element;
        }
        return null;
    }

    public void insertToDB(List<CategoryEntity> categoryEntityList) {
        for (CategoryEntity entity : categoryEntityList) {
            try {
                CategoryEntity categoryDB = categoryRepository.findByNameIgnoreCaseAndType(entity.getName().trim().toUpperCase(), MainConstants.STATUS_BIKE);
                if (categoryDB == null) categoryDB = categoryRepository.saveAndFlush(entity);
                for (BikeEntity bikeEntity : entity.getBikesById()) {
                    BikeEntity tmp = bikeRepository.findByHashBikeCode(bikeEntity.hashCode() + "");
                    if (tmp == null) {
                        try {
                            bikeEntity.setCategoryId(categoryDB.getId());
                            bikeEntity.setHashBikeCode(bikeEntity.hashCode() + "");
                            bikeRepository.save(bikeEntity);
//                            int bikeId = bikeRepository.saveAndFlush(bikeEntity).getId();
//
//                            for (ImageEntity imageEntity : bikeEntity.getImagesById()) {
//                                imageEntity.setOwnId(bikeId);
//                                imageEntity.setType(MainConstants.STATUS_BIKE);
//                                imageRepository.saveAndFlush(imageEntity);
//                            }
                        } catch (Exception ex) {
                            logger.error(ex.getMessage() + " " + bikeEntity.getUrl());
                        }
                    }
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage() + " " + entity.getName());
            }
        }
    }


    public List<BikeEntity> getBikesFromLink(Element doc, Map<String, String> selectorMap, String brand) {
        List<BikeEntity> bikeEntities = new ArrayList<>();

        Elements productElements = doc.select(selectorMap.get("productItem"));

        for (Element element : productElements) {
            try {
                BikeEntity bikeEntity = new BikeEntity();

                bikeEntity.setBrand(brand);
                bikeEntity.setName(element.select(selectorMap.get("name")).text());
                bikeEntity.setVersion(element.select(selectorMap.get("name")).text());
                String price = element.select(selectorMap.get("price")).text().replaceAll("[^\\d]", "");
                if (price.isEmpty()) {
                    bikeEntity.setPrice("Liên hệ");
                } else {
                    bikeEntity.setPrice(price);
                }
                bikeEntity.setUrl(element.select(selectorMap.get("url")).attr("href"));
                bikeEntity.setStatus(selectorMap.get("status"));
//
//                ImageEntity imageEntity = new ImageEntity();
//                imageEntity.setUrl(element.select(selectorMap.get("image")).attr("src"));
//                List<ImageEntity> imageEntityList = new ArrayList<>();
//                imageEntityList.add(imageEntity);
//                bikeEntity.setImagesById(imageEntityList);

                bikeEntity.setDescription(getBikeDescription(element.select(selectorMap.get("url")).attr("href"), selectorMap.get("description")));
                bikeEntities.add(bikeEntity);

            } catch (IOException ex) {
                logger.error(ex.getMessage() + " " + element.select(selectorMap.get("url")).attr("href"));
            }
        }
        return bikeEntities;
    }

    public String getBikeDescription(String url, String selector) throws IOException {
        String result = "";
        System.out.println(url);

        Document doc = Jsoup.connect(url).timeout(10 * 1000).get();

        Elements descriptionElements = doc.select(selector);
        for (Element element : descriptionElements) {
            result += element.text() + "; ";
        }
        return result.trim();
    }

    public String getBikeDescription(Element element, String selector) {
        String result = "";

        if (element != null) {
            Elements descriptionElements = element.select(selector);
            for (Element dElement : descriptionElements) {
                result += dElement.text() + "; ";
            }
        } else {
            System.out.println("No description");
        }
        return result.trim();
    }

}
