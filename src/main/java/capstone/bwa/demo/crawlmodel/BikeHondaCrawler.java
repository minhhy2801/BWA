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
    private Map<String, Map<AccessoryEntity, ImageEntity>> categoryMappingAccessories;
    private Map<String, List<BikeEntity>> categoryMappingBike;
    private Map<String, Map<String, String>> mapBikeAndVersionPrice;

    private List<Map<String, String>> listXeTayGa;
    private List<Map<String, String>> listXeSo;
    private List<Map<String, String>> listXeCon;

    public BikeHondaCrawler() {
        bikeList = new HashMap<>();
        categoryMappingAccessories = new HashMap<>();
        mapBikeAndVersionPrice = new HashMap<>();
        categoryMappingBike = new HashMap<>();
        listXeTayGa = new ArrayList<>();
        listXeCon = new ArrayList<>();
        listXeSo = new ArrayList<>();
    }

    public List<Map<String, String>> getListXeTayGa() {
        return listXeTayGa;
    }

    public List<Map<String, String>> getListXeSo() {
        return listXeSo;
    }

    public List<Map<String, String>> getListXeCon() {
        return listXeCon;
    }

    public static void main(String[] args) {
        BikeHondaCrawler crawler = new BikeHondaCrawler();
        try {
            crawler.getBikeFromHonda();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<BikeEntity, ImageEntity> getBikeList() {
        return bikeList;
    }

    public Map<String, List<BikeEntity>> getCategoryMappingBike() {
        return categoryMappingBike;
    }

    public Map<String, Map<AccessoryEntity, ImageEntity>> getCategoryMappingAccessories() {
        return categoryMappingAccessories;
    }

    public Map<String, Map<String, String>> getMapBikeAndVersionPrice() {
        return mapBikeAndVersionPrice;
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
            categoryMappingAccessories.clear();
        }
    }

    public void getBikeFromHonda() throws IOException {
        List<String> linkXeTayGa = new ArrayList<>();
        List<String> linkXeSo = new ArrayList<>();
        List<String> linkXeConTay = new ArrayList<>();
        String link;
        Document doc = Jsoup.connect("https://hondaxemay.com.vn/san-pham/").get();
        Elements elements = doc.select("div.tabs-content.clearfix");
        for (Element e : elements.select(".content-loai-xe-365")) {
            link = e.select(".desc").select("a").attr("href");
            linkXeSo.add(link);
        }
        for (Element e : elements.select(".content-loai-xe-364")) {
            link = e.select(".desc").select("a").attr("href");
            linkXeTayGa.add(link);
        }
        for (Element e : elements.select(".content-loai-xe-366")) {
            link = e.select(".desc").select("a").attr("href");
            linkXeConTay.add(link);
        }
        Map<String, Document> mapLinkConTay = new HashMap<>();
        Map<String, Document> mapLinkTayGa = new HashMap<>();
        Map<String, Document> mapLinkXeSo = new HashMap<>();
        for (String url : linkXeConTay) {
            mapLinkConTay.put(url, Jsoup.connect(url).get());
        }
        for (String url : linkXeTayGa) {
            mapLinkTayGa.put(url, Jsoup.connect(url).get());
        }
        for (String url : linkXeSo) {
            mapLinkXeSo.put(url, Jsoup.connect(url).get());
        }
        for (Map.Entry<String, Document> mapUrlDocument : mapLinkTayGa.entrySet()) {
            listXeTayGa.addAll(crawlBike(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
        for (Map.Entry<String, Document> mapUrlDocument : mapLinkXeSo.entrySet()) {
            listXeSo.addAll(crawlBike(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
        for (Map.Entry<String, Document> mapUrlDocument : mapLinkConTay.entrySet()) {
            listXeCon.addAll(crawlBike(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
    }

    private List<Map<String, String>> crawlBike(Document document, String url) {
        List<Map<String, String>> listBike = new ArrayList<>();
        String name = document.title().replace(" – Honda xe máy", "");
        String key, value;
        Gson gson = new Gson();
        //Dac diem noi bat
        Elements elements = getContentFromDoc(document, ".op-toggle");
        Map<String, Object> outerBox = new HashMap<>();
        Map<String, String> innerBox = new HashMap<>();
        for (Element e : elements) {
            key = e.select("a").attr("data-title");
            value = e.select("a").attr("data-description").replace("<p>", "").replace("</p>", "");
            innerBox.put(key, value);
        }
        outerBox.put("outstanding features", innerBox);
        //Thong so ky thuat
        elements = getContentFromDoc(document, ".wrap-spec");
        innerBox = new HashMap<>();
        for (Element e : elements.select("tr")) {
            key = e.selectFirst("td:eq(0)").text();
            value = e.selectFirst("td:eq(1)").text();
            if (!key.equals("Dung tích nhớt máy")) {
                innerBox.put(key, value);
            }
        }
        outerBox.put("technical specifications", innerBox);
        String description = gson.toJson(outerBox);
        //get default image
        elements = document.select("div.gallery-item img[src]");
        String defaultImage = elements.select("img").first().attr("src");
        //version and price
        String version, price;
        elements = getContentFromDoc(document, ".wrap-color");
        for (Element ec : elements.select(".box")) {
            //Create bike and add
            Map<String, String> bikeDetail = new HashMap<>();
            bikeDetail.put("name", name);
            bikeDetail.put("url", url);
            bikeDetail.put("description", description);
            bikeDetail.put("brand", "Honda");
            bikeDetail.put("status", "NEW");
            version = ec.select(".color-title").text();
            price = ec.select(".data_version").first().text().replaceAll("[^0-9,.]", "");
            bikeDetail.put("version", version);
            bikeDetail.put("image",crawlImage(defaultImage,ec));
            bikeDetail.put("price", price);
            listBike.add(bikeDetail);
        }
        return listBike;
    }

    private String crawlImage(String defaultImage, Element element) {
        Elements elements = element.select("a.item.get_data_version");
        Map<String,String> innerBox = new HashMap<>();
        String key,value;
        for (Element e:elements){
            key = e.select(".text").first().text();
            value = e.select(".no-360 img[src]").attr("src");
            if (value.equals("")){
                value = defaultImage;
            }
            innerBox.put(key,value);
        }
        Gson gson = new Gson();
        value = gson.toJson(innerBox);
        return value;
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
                        data = getDataByDiv(elements, ".price").replaceAll("[^0-9,.]", "");
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
                    categoryMappingAccessories.put(categoryName, accessoryList);
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

    private AccessoryEntity getAccessory(String url, String brand) {
        AccessoryEntity newAccessory = new AccessoryEntity();
        newAccessory.setUrl(url);
        newAccessory.setBrand(brand);
        newAccessory.setStatus("NEW");
        return newAccessory;
    }

}

