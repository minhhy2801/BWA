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

    private Map<String, Map<AccessoryEntity, ImageEntity>> categoryMappingAccessories;

    private List<Map<String, String>> listXeTayGa;
    private List<Map<String, String>> listXeSo;
    private List<Map<String, String>> listXeCon;
    private List<Map<String, String>> listXeMoTo;

    public BikeHondaCrawler() {
        categoryMappingAccessories = new HashMap<>();
        listXeTayGa = new ArrayList<>();
        listXeCon = new ArrayList<>();
        listXeSo = new ArrayList<>();
        listXeMoTo = new ArrayList<>();
    }

    public List<Map<String, String>> getListXeMoTo() {
        return listXeMoTo;
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
            //crawler.getBikeFromSuzuki();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Map<String, Map<AccessoryEntity, ImageEntity>> getCategoryMappingAccessories() {
        return categoryMappingAccessories;
    }

    public void crawlBike() throws IOException {
        getBikeFromHonda();
//        getBikeFromSuzuki();
//        getBikeFromYamaha();
    }

    public void crawlAccessory() {
        try {
            getAccessoryFromHonda();
        } catch (IOException e) {

        }
    }

    private void getBikeFromYamaha() throws IOException {
        Document a = Jsoup.connect("https://yamaha-motor.com.vn/xe").validateTLSCertificates(false).get();

    }

    private void getBikeFromSuzuki() throws IOException {
        List<String> linkXeTayGa = new ArrayList<>();
        List<String> linkXeSo = new ArrayList<>();
        List<String> linkXeConTay = new ArrayList<>();
        List<String> linkXeMoTo = new ArrayList<>();
        //Get list link
        Document document = Jsoup.connect("https://www.suzuki.com.vn/index.php/xe-may").get();
        Elements elements = document.select(".item177").select(".maximenuck2 li");
        linkXeMoTo = getListLink(elements, "a", "abs:href");
        elements = document.select(".item209").select(".maximenuck2 li");
        linkXeConTay = getListLink(elements, "a", "abs:href");
        elements = document.select(".item210").select(".maximenuck2 li");
        linkXeTayGa = getListLink(elements, "a", "abs:href");
        elements = document.select(".item211").select(".maximenuck2 li");
        linkXeSo = getListLink(elements, "a", "abs:href");

        Map<String, Document> mapLinkConTay = new HashMap<>();
        Map<String, Document> mapLinkTayGa = new HashMap<>();
        Map<String, Document> mapLinkXeSo = new HashMap<>();
        Map<String, Document> mapLinkXeMoTo = new HashMap<>();
        //Get Map Url and Document
        mapLinkXeMoTo = getListUrlsAndDocuments(linkXeMoTo);
        mapLinkTayGa = getListUrlsAndDocuments(linkXeTayGa);
        mapLinkXeSo = getListUrlsAndDocuments(linkXeSo);
        mapLinkConTay = getListUrlsAndDocuments(linkXeConTay);

        for (Map.Entry<String, Document> mapUrlDocument : mapLinkXeMoTo.entrySet()) {
            document = mapUrlDocument.getValue();
            String name = document.title();
            String key, value;
            Gson gson = new Gson();
            //Dac diem noi bat
            String festhues = "";
            String specifications = "";
            List<String> listAccessories = new ArrayList<>();
            String accessory = "";
            elements = document.select(".uk-panel.nopadding");//.uk-overlay-active
            Map<String, String> outerBox = new HashMap<>();
            for (Element e : elements) {
                key = e.select("h3").text();
                if (!key.equals("")) {
                    if (key.equals("THÔNG SỐ KĨ THUẬT")) {
//                        specifications += key + ":" + value + "\n";
                    } else if (key.equals("PHỤ KIỆN & LINH KIỆN")) {

                    } else {
                        value = e.select("span").text();
                        festhues += key + ":" + value + "\n";
                    }
                }
            }
            outerBox.put("outstanding features", festhues);
            System.out.println(outerBox.toString());
            //Thong so ki thuat

//            listXeTayGa.addAll(crawlBike(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
//        for (Map.Entry<String, Document> mapUrlDocument : mapLinkXeSo.entrySet()) {
//            listXeSo.addAll(crawlBike(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
//        }
//        for (Map.Entry<String, Document> mapUrlDocument : mapLinkConTay.entrySet()) {
//            listXeCon.addAll(crawlBike(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
//        }
//        for (Map.Entry<String, Document> mapUrlDocument : mapLinkXeMoTo.entrySet()) {
//            listXeMoTo.addAll(crawlBike(mapUrlDocument.getValue(),mapUrlDocument.getKey()));
//        }
    }

    private void getBikeFromHonda() throws IOException {
        List<String> linkXeTayGa = new ArrayList<>();
        List<String> linkXeSo = new ArrayList<>();
        List<String> linkXeConTay = new ArrayList<>();
        List<String> linkXeMoTo = new ArrayList<>();
        //get link Moto
        Document doc = Jsoup.connect("https://hondaxemay.com.vn/hondamoto/san-pham").get();
        Elements elements = doc.select(".product_list");
        linkXeMoTo = getListLink(elements, "a.btn", "href");
        //get link Xe So
        doc = Jsoup.connect("https://hondaxemay.com.vn/san-pham/").get();
        elements = doc.select("div.tabs-content.clearfix").select(".content-loai-xe-365");
        linkXeSo = getListLink(elements, ".desc a", "href");
        //get link Xe Tay Ga
        elements = doc.select("div.tabs-content.clearfix").select(".content-loai-xe-364");
        linkXeTayGa = getListLink(elements, ".desc a", "href");
        //get link Xe Con Tay
        elements = doc.select("div.tabs-content.clearfix").select(".content-loai-xe-366");
        linkXeConTay = getListLink(elements, ".desc a", "href");

        Map<String, Document> mapLinkConTay = new HashMap<>();
        Map<String, Document> mapLinkTayGa = new HashMap<>();
        Map<String, Document> mapLinkXeSo = new HashMap<>();
        Map<String, Document> mapLinkXeMoTo = new HashMap<>();

        mapLinkXeMoTo = getListUrlsAndDocuments(linkXeMoTo);
        mapLinkTayGa = getListUrlsAndDocuments(linkXeTayGa);
        mapLinkXeSo = getListUrlsAndDocuments(linkXeSo);
        mapLinkConTay = getListUrlsAndDocuments(linkXeConTay);

        for (Map.Entry<String, Document> mapUrlDocument : mapLinkTayGa.entrySet()) {
            listXeTayGa.addAll(crawlBike(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
        for (Map.Entry<String, Document> mapUrlDocument : mapLinkXeSo.entrySet()) {
            listXeSo.addAll(crawlBike(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
        for (Map.Entry<String, Document> mapUrlDocument : mapLinkConTay.entrySet()) {
            listXeCon.addAll(crawlBike(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
        for (Map.Entry<String, Document> mapUrlDocument : mapLinkXeMoTo.entrySet()) {
            String url = mapUrlDocument.getKey();
            Document document = mapUrlDocument.getValue();
            String name = document.title();
            String key, value;
            Gson gson = new Gson();
            //Dac diem noi bat
            String features = "";
            elements = document.select(".js_detail_glr");
            Map<String, String> outerBox = new HashMap<>();
            for (Element e : elements) {
                key = e.select("h5").text().replace(":", "");
                value = e.select("p").text();
                features += key + ":" + value + "\n";
            }
            outerBox.put("outstanding features", features);
            //Thong so ky thuat
            elements = document.select("table tr");
            String specifications = "";
            for (Element e : elements.select("td")) {
                key = e.select("strong").text();
                e.removeAttr("strong");
                value = e.text();
                if (name.equals("Rebel 300/500")) {
                    key = e.select("td:eq(0)").text().replace(":", "");
                    value = e.select("td:eq(1)").text();
                }
                specifications += key + ":" + value + "\n";
            }
            outerBox.put("technical specifications", specifications);
            String description = gson.toJson(outerBox);
            //get default image
            elements = document.select(".bike_list img[src]");
            String defaultImage = elements.select("img").first().attr("src");
//            System.out.println(defaultImage);
            //version and price
            String version, price;
            elements = document.select(".bike_list");
            for (Element ec : elements.select(".bike_item")) {
                //Create bike and add
                Map<String, String> bikeDetail = new HashMap<>();
                bikeDetail.put("name", name);
                bikeDetail.put("url", url);
                bikeDetail.put("description", description);
                bikeDetail.put("brand", "Honda");
                bikeDetail.put("status", "NEW");
                version = ec.select("h6").text();
                price = ec.attr("data-price").replaceAll("[^0-9]", "").replace(".", "");
                if (version.equals("")) {
                    version = name;
                }
                bikeDetail.put("version", version);
                bikeDetail.put("image", ec.select("img").attr("src"));
                bikeDetail.put("price", price);
                if (!price.equals("")) {
                    listXeMoTo.add(bikeDetail);
                }
            }
        }
    }

    private Map<String, String> getOutstandingFeatures(Elements elements, String selectorKey, String selectorValue,
                                                       String attrKey, String attrValue) {
        Map<String, String> innerBox = new HashMap<>();
        String key, value;
        for (Element e : elements) {
            key = e.select(selectorKey).attr(attrKey);
            value = e.select(selectorValue).attr(attrValue).
                    replace("<p>", "").replace("</p>", "");
            innerBox.put(key, value);
        }
        return innerBox;
    }

    private List<String> getListLink(Elements elements, String selector, String attr) {
        String link;
        List<String> listLink = new ArrayList<>();
        for (Element e : elements.select(selector)) {
            link = e.attr(attr);
            listLink.add(link);
        }
        return listLink;
    }

    private List<Map<String, String>> crawlBike(Document document, String url) {
        List<Map<String, String>> listBike = new ArrayList<>();
        String name = document.title().replace(" – Honda xe máy", "");
        String key, value;
        Gson gson = new Gson();
        //Dac diem noi bat
        Elements elements = document.select(".op-toggle");
        Map<String, String> outerBox = new HashMap<>();
        String features = "";
        for (Element e : elements) {
            key = e.select("a").attr("data-title");
            value = e.select("a").attr("data-description").
                    replace("<p>", "").replace("</p>", "")
                    .replaceAll(":", "");
            features += key + ":" + value + "\n";
        }
        outerBox.put("outstanding_features", features);
        //Thong so ky thuat
        String specifications = "";
        elements = document.select(".wrap-spec");
        for (Element e : elements.select("tr")) {
            key = e.select("td:eq(0)").get(0).text();
            value = e.select("td:eq(1)").get(0).text();
            if (!key.equals("")) {
                specifications += key + ":" + value + "\n";
            }
        }
        outerBox.put("technical_specifications", specifications);
        String description = gson.toJson(outerBox);
        //get default image
        elements = document.select("div.gallery-item img[src]");
        String defaultImage = elements.select("img").first().attr("src");
        //version and price
        String version, price;
        elements = document.select(".wrap-color");
        for (Element ec : elements.select(".box")) {
            //Create bike and add
            Map<String, String> bikeDetail = new HashMap<>();
            bikeDetail.put("name", name);
            bikeDetail.put("url", url);
            bikeDetail.put("description", description);
            bikeDetail.put("brand", "Honda");
            bikeDetail.put("status", "NEW");
            version = ec.select(".color-title").text().replace("*Hình ảnh minh họa có thể khác so với xe thực tế", "");
            if (version.equals("Màu sắc sản phẩm")) {
                elements = ec.select(".data_version").select(".table-version").select("tr");
                int sl = 0;
                for (Element tableDetail : elements) {
                    sl++;
                    version = tableDetail.select("td:eq(0)").text();
                    price = tableDetail.select("td:eq(1)").text();
                    bikeDetail.put("version", version);
                    bikeDetail.put("image", crawlImage(defaultImage, ec));
                    bikeDetail.put("price", price.replaceAll("[^0-9]", "").replace(".", ""));
                    System.out.println(sl);
                }
            } else {
                price = ec.select(".data_version").first().text();
                bikeDetail.put("version", version);
                bikeDetail.put("image", crawlImage(defaultImage, ec));
                bikeDetail.put("price", price.replaceAll("[^0-9]", "").replace(".", ""));
            }
            listBike.add(bikeDetail);
        }
        return listBike;
    }

    private String crawlImage(String defaultImage, Element element) {
        Elements elements = element.select("a.item.get_data_version");
        Map<String, String> innerBox = new HashMap<>();
        String key, value;
        for (Element e : elements) {
            key = e.select(".text").first().text();
            value = e.select(".no-360 img[src]").attr("src");
            if (value.equals("")) {
                value = defaultImage;
            }
            innerBox.put(key, value);
        }
        Gson gson = new Gson();
        value = gson.toJson(innerBox);
        return value;
    }

    private void getAccessoryFromHonda() throws IOException {
        List<String> pageList = getListPageHonda();
        Map<String, String> categoryList = getListCategorys();
        String key, value, categoryName = "";
        Map<String, List<String>> categogyAndAccessoryLink = new HashMap<>();
//        Map<String, List<String>> categogyAndAccessory = new HashMap<>();
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
                        data = getDataByDiv(elements, ".price").replaceAll("[^0-9]", "").replaceAll(".", "");
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

    private List<String> getListPageHonda() {
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

    private Map<String, Document> getListUrlsAndDocuments(List<String> listLink) throws IOException {
        Map<String, Document> mapDocument = new HashMap<>();
        for (String url : listLink) {
            mapDocument.put(url, Jsoup.connect(url).get());
        }
        return mapDocument;
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

