package capstone.bwa.demo.crawlmodel;

import capstone.bwa.demo.entities.AccessoryEntity;
import capstone.bwa.demo.entities.BikeEntity;
import capstone.bwa.demo.entities.ImageEntity;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class BikeHondaCrawler {

    private Map<BikeEntity, ImageEntity> bikeList;
    private Map<String, Map<AccessoryEntity, ImageEntity>> categoryMapping;
    private Map<String, Map<String, String>> categoryAndProduct;
    private Map<String, String> linkAndName;
    private Map<String, Map<String, String>> mapBikeAndVersion;

    public BikeHondaCrawler() {
        bikeList = new HashMap<>();
        categoryMapping = new HashMap<>();
        categoryAndProduct = new HashMap<>();
        linkAndName = new HashMap<>();
        mapBikeAndVersion = new HashMap<>();
    }

    public Map<BikeEntity, ImageEntity> getBikeList() {
        return bikeList;
    }

    public Map<String, Map<AccessoryEntity, ImageEntity>> getCategoryMapping() {
        return categoryMapping;
    }

    public Map<String, Map<String, String>> getMapBikeAndVersion() {
        return mapBikeAndVersion;
    }

    public Map<String, Map<String, String>> getCategoryAndProduct() {
        return categoryAndProduct;
    }

    public void crawlBike() {
        try {
            getBikeFromHonda();
        } catch (Exception e) {
            bikeList.clear();
        }
    }

    public void crawlAccessory() {
        try {
            getAccessoryFromHonda();
        } catch (Exception e) {
            categoryMapping.clear();
        }
    }

    private void getBikeFromHonda() throws IOException {
        Document doc = Jsoup.connect("https://hondaxemay.com.vn/san-pham/").get();
        Elements elements = doc.select(".menu-sanpham");
        String catelogyName, link, name;
        List<Document> listDocuments = new ArrayList<>();
        for (Element e : elements.select(".item-wrp")) {
            catelogyName = e.select(".type").first().text();
            for (Element element : e.select(".item")) {
                link = element.select("a").attr("href");
                name = element.select("p").text();
                if (!link.equals("https://hondaxemay.com.vn/hondamoto")) {
                    linkAndName.put(link, name);
                }
            }
            categoryAndProduct.put(catelogyName, linkAndName);
        }

        for (String url : linkAndName.keySet()) {
            //--Check if url is true and remove url: https://hondaxemay.com.vn/hondamoto
            if (url.contains("http")) {
                doc = Jsoup.connect(url).get();
                listDocuments.add(doc);
                BikeEntity newBike = getBike(url, "Honda");
                ImageEntity newImage = getProductImages("Bike");
                //get Description
                Map<String, Object> infor = new HashMap<>();
                Map<String, String> inforDetail = new HashMap<>();
                String key, value;
                //Dac diem noi bat
                elements = getContentFromDoc(doc, ".op-toggle");
                for (Element e : elements) {
                    key = e.select("a").attr("data-title");
                    value = e.select("a").attr("data-description").replace("<p>", "").replace("</p>", "");
                    inforDetail.put(key, value);
                }
                key = "outstanding features";
                infor.put(key, inforDetail);
                //Thong so ky thuat
                elements = getContentFromDoc(doc, ".wrap-spec");
                inforDetail = new HashMap<>();
                for (Element e : elements.select("tr")) {
                    key = e.selectFirst("td:eq(0)").text();
                    value = e.selectFirst("td:eq(1)").text();
                    if (!key.equals("Dung tích nhớt máy")) {
                        inforDetail.put(key, value);
                    }
                }
                key = "technical specifications";
                infor.put(key, inforDetail);
                Gson gson = new Gson();
                String json = gson.toJson(infor);
                newBike.setDescription(json);
                //Get Version and Price
                inforDetail = new HashMap<>();
                elements = getContentFromDoc(doc, ".wrap-color");
                for (Element e : elements.select(".box")) {
                    key = e.select(".color-title").text();
                    value = e.select(".data_version").first().text();
                    inforDetail.put(key, value);
                    mapBikeAndVersion.put(url, inforDetail);
                }

                //get Image
                List<String> imageList;
                Map<String, List<String>> imageJson = new HashMap<>();
                //"ProductDetail"
                key = "ProductDetail";
                elements = getContentFromDoc(doc, ".option-img");
                imageList = getDataByAttr(elements, "abs:data-img");
                imageJson.put(key, imageList);
                //"ColorDetail"
                imageJson.put(key, imageList);
                elements = getContentFromDoc(doc, "div.no-360 img[src]");
                imageList.addAll(getDataByAttr(elements, "src"));
                imageJson.put(key, imageList);
                //"Gallery"
                key = "Gallery";
                elements = getContentFromDoc(doc, "div.gallery-item img[src]");
                imageList.addAll(getDataByAttr(elements, "src"));
                imageJson.put(key, imageList);
                json = gson.toJson(imageJson);
                newImage.setUrl(json);
                bikeList.put(newBike, newImage);
            }
        }
    }

    private void getAccessoryFromHonda() throws IOException {
        List<String> pageList = getLinkList();
        Map<String, String> categoryList = getListCategorys();
        String key, value, categoryName = "";
        Map<String, List<String>> categogyAndAccessoryLink = new HashMap<>();
        //Get list Category in each crawl page.
        for (String url : pageList) {
            if (url.contains("http")) {
                List<String> accessoryUrls = getAllProductLinks(url, "a.btn", "href");
                //Check and get categoryName
                for (Map.Entry<String, String> categoryEntry : categoryList.entrySet()) {
                    if (url.contains(categoryEntry.getKey())) {
                        categoryName = categoryEntry.getValue();
                    }
                }

                if (categogyAndAccessoryLink.keySet().contains(categoryName)) {
                    accessoryUrls.addAll(categogyAndAccessoryLink.get(categoryName));
                    categogyAndAccessoryLink.replace(categoryName, accessoryUrls);
                } else {
                    categogyAndAccessoryLink.put(categoryName, accessoryUrls);
                }
                Map<AccessoryEntity, ImageEntity> accessoryList = new HashMap<>();
                for (String accessoryUrl : accessoryUrls) {
                    if (accessoryUrl.contains("http")) {
                        Document doc = Jsoup.connect(accessoryUrl).get();
                        AccessoryEntity newAccessory = getAccessory(accessoryUrl, "Honda");
                        ImageEntity newImageEntity = getProductImages("Accessory");
                        //Get elements that contain all information of accessory
                        Elements elements = getContentFromDoc(doc, ".product-detail");

                        Elements imageData = getElementsFromElements(elements, "#img-big img");
                        String data = imageData.attr("src");
                        newImageEntity.setUrl(data);
                        Elements infoData = getElementsFromElements(elements, ".info");
                        data = getDataByDiv(infoData, ".title");
                        newAccessory.setName(data);
                        data = getDataByDiv(elements, ".price");
                        newAccessory.setPrice(data);
                        infoData = getElementsFromElements(infoData, ".info-detail li:gt(0)");
                        //Raw data use to paste json
                        Map<String, Object> infor = new HashMap<>();
                        for (Element e : infoData) {
                            key = getDataByDiv(e.select(".left"), ".left").replace(":", "");
                            if (key.equals("Tính năng")) {
                                key = "features";
                            } else {
                                key = "installation";
                            }
                            value = getDataByDiv(e.select(".right"), ".right").replace(":", "");
                            infor.put(key, value);
                        }
                        //Paste Json
                        Gson gson = new Gson();
                        String json = gson.toJson(infor);
                        newAccessory.setDescription(json);
                        String hashCode = newAccessory.hashCode() + "";
                        newAccessory.setHashAccessoryCode(hashCode);
                        accessoryList.put(newAccessory, newImageEntity);
                    }
                    categoryMapping.put(categoryName, accessoryList);
                }
            }
        }
    }

    private List<String> getLinkList() {
        List<String> pageList = new ArrayList<>();
        //Add link use to crawl
        //Xe tay ga
        pageList.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3828&filter_orderby=latest&filter_tax=3720");
        pageList.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3828&filter_orderby=latest&filter_tax=3722");
        pageList.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3828&filter_orderby=latest&filter_tax=3721");
        //Xe so
        pageList.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3861&filter_orderby=latest&filter_tax=3721");
        pageList.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3861&filter_orderby=latest&filter_tax=3720");
        pageList.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3861&filter_orderby=latest&filter_tax=3722");
        //Xe con
        pageList.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3829&filter_orderby=latest&filter_tax=3722");
        pageList.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3829&filter_orderby=latest&filter_tax=3721");
        pageList.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3829&filter_orderby=latest&filter_tax=3720");
        pageList.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3829&filter_orderby=latest&filter_tax=3719");
        //Xe moto
        pageList.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3840&filter_orderby=latest&filter_tax=3721");
        pageList.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3840&filter_orderby=latest&filter_tax=3722");
        pageList.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3840&filter_orderby=latest&filter_tax=3720");
        pageList.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3840&filter_orderby=latest&filter_tax=3719");
        return pageList;
    }

    private Map<String, String> getListCategorys() throws IOException {
        Map<String, String> categoryList = new HashMap<>();
        String key, value;
        List<String> page = getAllProductLinks("https://hondaxemay.com.vn/phukien/phu-kien-xe-may", "a.link", "href");
        for (String url : page) {
            if (url.contains("http")) {
                Document doc = Jsoup.connect(url).get();
                Elements categoryBox = doc.select(".sel_filter_tax");
                for (Element e : categoryBox.select("option")) {
                    key = e.attr("value");
                    if (!key.equals("0")) {
                        value = e.text();
                        categoryList.put(key, value);
                    }
                }
            }
        }
        return categoryList;
    }

    private List<String> getDataByAttr(Elements elements, String attr) {
        List<String> data = new ArrayList<>();
        for (Element element : elements) {
            data.add(element.attr(attr));
        }
        return data;
    }

    private String getDataByDiv(Elements elements, String divClass) {
        String data = "";
        for (Element element : elements.select(divClass)) {
            if (!data.contains(element.text())) {
                data = element.text();
            }
        }
        return data;
    }

    private Elements getContentFromDoc(Document doc, String selector) {
        return doc.select(selector);
    }

    private Elements getElementsFromElements(Elements elements, String selector) {
        return elements.select(selector);
    }

    private Elements getElements(String url, String selector) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.select(selector);
    }

    private List<String> getAllProductLinks(String url, String selector, String attr) throws IOException {
        Elements links = getElements(url, selector);
        List<String> linkList = new ArrayList<>();
        links.forEach((link) -> {
            if (!linkList.contains(link.attr(attr))) {
                linkList.add(link.attr(attr));
            }
        });
        return linkList;
    }

    private ImageEntity getProductImages(String type) {
        ImageEntity newImage = new ImageEntity();
        newImage.setType(type);
        newImage.setStatus("NEW");
        return newImage;
    }

    private BikeEntity getBike(String url, String brand) {
        BikeEntity newBike = new BikeEntity();
        newBike.setUrl(url);
        newBike.setBrand(brand);
        newBike.setStatus("NEW");
        return newBike;
    }

    private AccessoryEntity getAccessory(String url, String brand) {
        AccessoryEntity newAccessory = new AccessoryEntity();
        newAccessory.setUrl(url);
        newAccessory.setBrand(brand);
        newAccessory.setStatus("NEW");
        return newAccessory;
    }

}

