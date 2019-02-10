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

    Map<BikeEntity, ImageEntity> bikeList;
    Map<AccessoryEntity, ImageEntity> accessoryList;

    public BikeHondaCrawler() {
        bikeList = new HashMap<>();
        accessoryList = new HashMap<>();
    }

    public Map<BikeEntity, ImageEntity> getBikeList() {
        return bikeList;
    }

    public Map<AccessoryEntity, ImageEntity> getAccessoryList() {
        return accessoryList;
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
            accessoryList.clear();
        }
    }

    private void getBikeFromHonda() throws IOException {
        List<String> linkBike = getAllProductLinks("https://hondaxemay.com.vn/san-pham/", "a.btn", "href");
        for (String url : linkBike) {
            //--Check if url is true and remove url: https://hondaxemay.com.vn/hondamoto
            if (url.contains("http") && !url.equals("https://hondaxemay.com.vn/hondamoto")) {
                Document doc = Jsoup.connect(url).get();
                BikeEntity newBike = getBike(url, "Honda");
                ImageEntity newImage = getProductImages("Bike");
                Elements elements = getContentFromDoc(doc, ".wrap-spec");
                newBike.setName(doc.title());
                String data = getDataByTag(elements, "td");
                newBike.setDescription(data);
                elements = getContentFromDoc(doc, ".wrap-color");
                data = getDataByDiv(elements, ".color-title") + "\n";
                elements = getElementsFromElements(elements, ".price");
                data += getDataByDiv(elements, ".text");
                newBike.setPrice(data);
                data = newBike.hashCode() + "";
                newBike.setHashCode(data);
                //"ProductDetail"
                elements = getContentFromDoc(doc, ".option-img");
                data = getDataByAttr(elements, "abs:data-img") + "\n";
                //"ColorDetail"
                elements = getContentFromDoc(doc, "div.no-360 img[src]");
                data += getDataByAttr(elements, "src") + "\n";
                //"Gallery"
                elements = getContentFromDoc(doc, "div.gallery-item img[src]");
                data += getDataByAttr(elements, "src");
                newImage.setUrl(data);
                bikeList.put(newBike, newImage);
            }
        }
    }

    private void getAccessoryFromHonda() throws IOException {
        List<String> pageList = getAllProductLinks("https://hondaxemay.com.vn/phukien/phu-kien-xe-may", "a.link", "href");
        for (String url : pageList) {
            if (url.contains("http")) {
                List<String> accessoryUrls = getAllProductLinks(url, "a.btn", "href");
                for (String accessoryUrl : accessoryUrls) {
                    if (accessoryUrl.contains("http")) {
                        Document doc = Jsoup.connect(accessoryUrl).get();
                        AccessoryEntity newAccessory = getAccessory(accessoryUrl, "Honda");
                        ImageEntity newImageEntity = getProductImages("Accessory");
                        Elements elements = getContentFromDoc(doc, ".product-detail");
                        Elements imageData = getElementsFromElements(elements, "#img-big img");
                        String data = getDataByAttr(imageData, "src");
                        newImageEntity.setUrl(data);
                        Elements infoData = getElementsFromElements(elements, ".info");
                        data = getDataByDiv(infoData, ".title");
                        newAccessory.setName(data);
                        data = getDataByDiv(elements, ".price");
                        newAccessory.setPrice(data);
                        infoData = getElementsFromElements(infoData, ".info-detail li:gt(0)");
                        Map<String, Object> infor = new HashMap<>();
//                        data = getDataByTag(infoData, "li");
                        for (Element e : infoData) {
                            String key = getDataByDiv(e.select(".left"), ".left").replace(":", "");
                            if (key.equals("Tính năng")) {
                                key = "features";
                            } else {
                                key = "installation";
                            }
//                            System.out.println("left " + key);
                            String value = getDataByDiv(e.select(".right"), ".right").replace(":", "");
//                            System.out.println("right " + value);
                            infor.put(key, value);
                        }
//                        System.out.println(infor);
                        Gson gson = new Gson();
//                        System.out.println("Infor: " + infor);
                        String json = gson.toJson(infor);
//                        System.out.println("Json: "+json);
                        newAccessory.setDescription(json);
                        String hashCode = newAccessory.hashCode() + "";
                        newAccessory.setHashAccessoryCode(hashCode);
//                        System.out.println(hashCode);
                        accessoryList.put(newAccessory, newImageEntity);
                    }
                }
            }
        }
    }

    private String getDataByAttr(Elements elements, String attr) {
        String data = "";
        for (Element element : elements) {
            data += element.attr(attr) + "||";
        }
        return data;
    }

    private String getDataByDiv(Elements elements, String divClass) {
        String data = "";
        for (Element element : elements.select(divClass)) {
            if (!data.contains(element.text())) {
                data += element.text();
            }
        }
        return data;
    }

    private String getDataByTag(Elements elements, String attr) {
        String data = "";
        for (Element element : elements.select(attr)) {
            data += element.text() + "\n";
        }
        return data;
    }

    private Elements getContentFromDoc(Document doc, String selector) {
        Elements elements = doc.select(selector);
        return elements;
    }

    private Elements getElementsFromElements(Elements elements, String selector) {
        Elements content = elements.select(selector);
        return content;
    }

    private Elements getElements(String url, String selector) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(selector);
        return elements;
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
        newImage.setStatus("new");
        return newImage;
    }

    private BikeEntity getBike(String url, String brand) {
        BikeEntity newBike = new BikeEntity();
        newBike.setUrl(url);
        newBike.setBrand(brand);
        newBike.setStatus("New");
        // Set default category when crawl is new(1)
        newBike.setCategoryId(1);
        return newBike;
    }

    private AccessoryEntity getAccessory(String url, String brand) {
        AccessoryEntity newAccessory = new AccessoryEntity();
        newAccessory.setUrl(url);
        newAccessory.setBrand(brand);
        newAccessory.setStatus("New");
        // Set default category when crawl is new(1)
        newAccessory.setCategoryId(1);
        return newAccessory;
    }

}

