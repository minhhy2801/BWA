package capstone.bwa.demo.crawlmodel;

import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.*;
import java.util.*;

public class BikeHondaCrawler {

    private List<Map<String, String>> listXeTayGa;
    private List<Map<String, String>> listXeSo;
    private List<Map<String, String>> listXeCon;
    private List<Map<String, String>> listXeMoTo;
    private List<Map<String, String>> listAccessories;
    private List<Map<String, String>> listBikes;

    private final String statusActive = "ACTIVE";

    public BikeHondaCrawler() {
        listXeTayGa = new ArrayList<>();
        listXeCon = new ArrayList<>();
        listXeSo = new ArrayList<>();
        listXeMoTo = new ArrayList<>();
        listAccessories = new ArrayList<>();
        listBikes = new ArrayList<>();
    }

    public static void main(String[] args) {
        BikeHondaCrawler crawler = new BikeHondaCrawler();
        try {
            crawler.getBikeFromKymco("http://www.kymco.com.vn/san-pham");
            //crawler.getBikeFromYamaha("https://yamaha-motor.com.vn/xe/loai-xe/xe-ga", "Xe Tay Ga");
        } catch (IOException e) {

        }
    }

    public List<Map<String, String>> getListBikes() {
        return listBikes;
    }

    public List<Map<String, String>> getListAccessories() {
        return listAccessories;
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

    public void crawlBike(String url, String categoryName) throws IOException {
        //Kiểm tra url và gọi hàm crawl tương ứng
        if (url.contains("motoanhquoc")) {
            getBikeFromTriumph(url);
        } else if (url.contains("yamaha")) {
            getBikeFromYamaha(url, categoryName);
        } else if (url.contains("hondamoto")) {
            getMotoBikeFromHonda(url);
        } else if (url.contains("hondaxemay")) {
            getBikeFromHonda(url);
        } else if (url.contains("kymco")) {
            getBikeFromKymco(url);
        } else if (url.contains("sym")) {
            getBikeFromSym(url);
        }
    }

    public void crawlAccessoryHonda(String url) throws IOException {
        List<String> accessoryUrls = getAllProductLinks(url, "a.btn", "href");
        //crawl và lưu dưới dạng map link và document
        Map<String, Document> mapLinkAndDoc = new HashMap<>();
        mapLinkAndDoc = getListUrlsAndDocuments(accessoryUrls);
        String key, value;
        //phân tích từng document
        for (Map.Entry<String, Document> mapUrlDocument : mapLinkAndDoc.entrySet()) {
            String accessoryUrl = mapUrlDocument.getKey();
            Document doc = mapUrlDocument.getValue();
            //lấy elements chứa toàn bộ data của accessory
            Elements elements = getContentFromDoc(doc, ".product-detail");
            //lấy hình ảnh
            Elements imageData = getElementsFromElements(elements, "#img-big img");
            String imageUrl = imageData.attr("src");
            if (!imageUrl.equals("")) {
                //lấy elements chứa toàn bộ thông tin của accessory
                Elements infoData = getElementsFromElements(elements, ".info");

                String name = getDataByDiv(infoData, ".title");
                String price = getDataByDiv(infoData, ".price").replaceAll("[^0-9]", "")
                        .replace(".", "");
                infoData = getElementsFromElements(infoData, ".info-detail li:gt(0)");

                String description = "";
                //lấy thông tin mô tả
                for (Element e : infoData) {
                    key = getDataByDiv(e.select(".left"), ".left").replace(":", "");
                    value = getDataByDiv(e.select(".right"), ".right").replace(":", "");
                    description += "|" + key.replace(":", "") + ":" + value;
                }
                description = description.replaceFirst("\\|", "");
                Map<String, String> accessoryDetail = new HashMap<>();
                //gán thông tin lấy được để tạo thành thông tin hoàn chỉnh và thêm vào 1 list
                accessoryDetail = accessoryInfo(accessoryUrl, name, "Honda", description, price, imageUrl);
                listAccessories.add(accessoryDetail);
            }
        }
    }

    private void getBikeFromTriumph(String url) throws IOException {
        List<String> linkXeMoto = new ArrayList<>();
        //Get list link
        Document document = Jsoup.connect(url).get();
        Elements elements = document.select(".stm-single-car-links a[href~=(?i)\\.]");
        linkXeMoto = getListLink(elements, "a", "href");
        //Get map LinkAndDocument
        Map<String, Document> mapLinkAndDocument = new HashMap<>();
        mapLinkAndDocument = getListUrlsAndDocuments(linkXeMoto);
        //Crawl Bike From Triumph
        for (Map.Entry<String, Document> mapUrlDocument : mapLinkAndDocument.entrySet()) {
            listBikes.addAll(crawlBikeTriumph(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
    }

    private List<Map<String, String>> crawlBikeTriumph(Document document, String url) {
        List<Map<String, String>> listBike = new ArrayList<>();
        Map<String, String> bikeDetail = new HashMap<>();
        String name, price, image, version;

        Elements nameAndPrice = document.select(".stm-listing-single-price-title");
        price = nameAndPrice.select(".price_unit").text().replaceAll("[^0-9]", "")
                .replace(".", "");
        name = nameAndPrice.select(".title").text();
        version = name;

        String key, value;
        Gson gson = new Gson();
        //Dac diem noi bat and mau sac
        Elements elements = document.select(".vc_col-sm-4").select(".wpb_wrapper");
        Map<String, String> outerBox = new HashMap<>();
        String features = "";
        int i = 0;
        if (elements.select(".list-style-1 li").text().equals("")) {
            i++;
            value = elements.select("p").first().text();
            features += "|" + i + ":" + value;
        } else {
            for (Element e : elements.select(".list-style-1 li")) {
                i++;
                if (!e.text().equals("")) features += "|" + i + ":" + e.text();
            }
        }
        outerBox.put("outstanding_features", features.replaceFirst("\\|", ""));
        Map<String, String> listImageAndColor = new HashMap();
        for (Element e : elements.select("p:not(br)")) {
            String color = e.text();
            if (!color.equals("Màu sắc:")) {
                key = "color";
                value = color.replace("⇒ ", "").replaceAll("[0-9]", "")
                        .replace("đ", "").replace(".", "").trim();
                listImageAndColor.put(key, value);
                key = "url";
                int vitri = 0;
                Elements imageElements = document.select("a.prettyphoto");
                if (vitri <= imageElements.size()) {
                    e = imageElements.get(vitri);
                    value = e.attr("href");
                    vitri++;
                }
//                value = "Xem hình ảnh trong gallery";
                listImageAndColor.put(key, value);
            }
        }
        image = gson.toJson(listImageAndColor);
        if (!listImageAndColor.toString().equals("")) {
            Map<String, Object> mapImage = new HashMap();
            mapImage.put("image",listImageAndColor);
            //.vc_general a[href~=http]
//        for (Element e : document.select("a.prettyphoto")) {
//            key = "url";
//            value = e.attr("href");
//            mapImage.put(key, value);
//            listImage.add(mapImage);
//        }
//        Map<String,Object> jsonImage = new HashMap<>();
//        jsonImage.put("gallery",listImage);
//        String gallery = gson.toJson(listImage);
            image = gson.toJson(mapImage);
            //Thong so ky thuat
            String specifications = "";
            elements = document.select(".stm-tech-infos");
            for (Element e : elements.select("tr")) {
                key = e.select("td:eq(0)").text();
                value = e.select("td:eq(1)").text();
                if (!key.equals("")) specifications += "|" + key + ":" + value;
            }
            outerBox.put("technical_specifications", specifications.replaceFirst("\\|", ""));
            String description = gson.toJson(outerBox);
            //Create bike and add
            bikeDetail = bikeInfo(url, name, "Triumph", description, version, price, image);
            listBike.add(bikeDetail);
        }
        return listBike;
    }

    private void getBikeFromYamaha(String url, String categoryName) throws IOException {
        List<String> listBikeLinks = new ArrayList<>();
        //Get list link
        Document document;
        Elements elements;
        if (categoryName.equals("Xe Tay Ga")) {
            document = Jsoup.connect(url).validateTLSCertificates(false).get();
        } else if (categoryName.equals("Xe Côn Tay")) {
            document = Jsoup.connect(url).validateTLSCertificates(false).get();
        } else {
            document = Jsoup.connect(url).validateTLSCertificates(false).get();
        }
        elements = document.select(".cate_pro.filtr-item");
        listBikeLinks = getListLink(elements, "a.btnView", "href");
        //Get Map Url and Document
        Map<String, Document> mapLinkBikeAndDocument = new HashMap<>();
        mapLinkBikeAndDocument = getListUrlsAndDocuments(listBikeLinks);
        //get list bike
        for (Map.Entry<String, Document> mapUrlDocument : mapLinkBikeAndDocument.entrySet()) {
            listBikes.addAll(crawlBikeYamaha(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
    }

    private List<Map<String, String>> crawlBikeYamaha(Document document, String url) {
        List<Map<String, String>> listBike = new ArrayList<>();
        String nameAndVersion = document.select(".selected").text().replaceAll("<br>", "|");
        String name, version;
        if (nameAndVersion.equals("")) {
            name = document.title().replace(" - Yamaha Việt Nam", "")
                    .replace("Bảng giá mua xe máy tay ga ", "")
                    .replace(" - Yamaha Motor Viet Nam", "");
            version = name;
        } else {
            name = nameAndVersion;
            version = nameAndVersion;
        }
        String key, value;
        Gson gson = new Gson();
        //Dac diem noi bat
        Elements elements = document.select(".eachFeature");
        Map<String, String> outerBox = new HashMap<>();
        String features = "";
        for (Element e : elements) {
            key = e.select(".feature_name").text();
            value = e.select(".div").text();
            if (!key.equals("")) features += "|" + key + ":" + value;
        }
        outerBox.put("outstanding_features", features.replaceFirst("\\|", ""));
        //Thong so ky thuat
        String specifications = "";
        elements = document.select(".blockInfoDetail");
        for (Element e : elements.select(".eachSpec").select(".col-sm-4")) {
            value = e.select(".fixHeight").text();
            e.select(".fixHeight").remove();
            key = e.text();
            if (!key.equals("")) specifications += "|" + key + ":" + value;
        }
        outerBox.put("technical_specifications", specifications.replaceFirst("\\|", ""));
        String description = gson.toJson(outerBox);
        //get default image: sử dụng nếu bike không có hình riêng cho sản phẩm
        elements = document.select(".boxGallery a");
        String defaultImage = elements.select("a").attr("href");
        //get price
        String price, image;
        Element element = document.select(".imageArea").first();
        Map<String, String> bikeDetail = new HashMap<>();
        price = document.select(".priceBike").text().replaceAll("[^0-9]", "")
                .replace(".", "");
        //get image
        //Step1: lấy map id,url của image
        Map<String, String> listImageById = new HashMap();
        for (Element sub : element.select(".reelDiv img")) {
            key = sub.attr("id");
            value = sub.attr("src");
            listImageById.put(key, value);
        }
        //Step2: so sánh id để lấy thông tin color
        List<Map<String, String>> listImage = new ArrayList<>();
        for (Element e : document.select(".colorBike li")) {
            Map<String, String> mapImage = new HashMap<>();
            key = e.select("a").attr("style");
            key.replace("background-color:", "");
            if (key.contains("background-image")) key = "Cập nhật sau";
            mapImage.put("color", key);
            value = e.select("a").attr("id");
            value = "_" + value.replace("-reel", "");
            value = listImageById.get(value);
            mapImage.put("url", value);
            listImage.add(mapImage);
        }
        Map<String, Object> jsonImage = new HashMap<>();
        jsonImage.put("image", listImage);
        image = gson.toJson(jsonImage);
        bikeDetail = bikeInfo(url, name, "Yamaha", description, version, price, image);
        listBike.add(bikeDetail);
        return listBike;
    }

    private void getMotoBikeFromHonda(String url) throws IOException {
        List<String> listLinkBikes = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".product_list");

        listLinkBikes = getListLink(elements, "a.btn", "href");
        Map<String, Document> mapLinkXeMoTo = new HashMap<>();
        mapLinkXeMoTo = getListUrlsAndDocuments(listLinkBikes);

        for (Map.Entry<String, Document> mapUrlDocument : mapLinkXeMoTo.entrySet()) {
            listBikes.addAll(crawlMotoBikeHonda(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
    }

    private List<Map<String, String>> crawlMotoBikeHonda(Document document, String url) {
        List<Map<String, String>> listBike = new ArrayList<>();
        String name = document.title();
        String key, value;
        Gson gson = new Gson();
        //Dac diem noi bat
        String features = "";
        Elements elements = document.select(".js_detail_glr div");
        Map<String, String> outerBox = new HashMap<>();
        for (Element e : elements.select("div:not(.thumb)")) {
            key = e.select("h5").text().replace(":", "");
            value = e.select("p").text();
            if (!key.equals("")) features += "|" + key + ":" + value;
        }
        outerBox.put("outstanding_features", features.replaceFirst("\\|", ""));
        //Thong so ky thuat
        elements = document.select("table tr td");
        String specifications = "";
        for (Element e : elements) {
            key = e.select("strong").text();
            e.removeAttr("strong");
            value = e.text();
            if (name.equals("Rebel 300/500")) {
                key = e.select("td:eq(0)").text().replace(":", "");
                value = e.select("td:eq(1)").text();
            }
            if (!key.equals("")) {
                specifications += "|" + key + ":" + value;
            }
        }
        outerBox.put("technical_specifications", specifications.replaceFirst("\\|", ""));
        String description = gson.toJson(outerBox);
        //get default image
        elements = document.select(".bike_list img[src]");
        String defaultImage = elements.select("img").first().attr("src");
        //version and price
        String version, price, image;
        elements = document.select(".bike_list");
        for (Element ec : elements.select(".bike_item")) {
            Map<String, String> bikeDetail = new HashMap<>();
            version = ec.select("h6").text();
            price = ec.attr("data-price").replaceAll("[^0-9]", "")
                    .replace(".", "");
            if (version.equals("")) {
                version = name;
            }
            //get Image
            image = ec.select("img").attr("src");
            if (image.equals("")) {
                image = defaultImage;
            }
            bikeDetail = bikeInfo(url, name, "Honda", description, version, price, image);
            if (!price.equals("")) {
                listBike.add(bikeDetail);
            }
        }
        return listBike;
    }

    private void getBikeFromSym(String allUrl) throws IOException {
        List<String> linkXeTayGa = new ArrayList<>();
        List<String> linkXeSo = new ArrayList<>();

        Document doc = Jsoup.connect(allUrl).get();
        //lấy link bike theo category trong từng tab khác nhau
        Elements elements = doc.select(".fleft [data-tabs=\"xe-nhap-khau\"] .box-item-product");
        linkXeTayGa = getListLink(elements, "a", "href");

        elements = doc.select(".fleft [data-tabs=\"xe-so\"] .box-item-product");
        linkXeSo = getListLink(elements, "a", "href");

        elements = doc.select(".fleft [data-tabs=\"xe-tay-ga\"] .box-item-product");
        linkXeTayGa.addAll(getListLink(elements, "a", "href"));

        //tạo map link và document
        Map<String, Document> mapLinkTayGa = new HashMap<>();
        Map<String, Document> mapLinkXeSo = new HashMap<>();

        mapLinkTayGa = getListUrlsAndDocuments(linkXeTayGa);
        mapLinkXeSo = getListUrlsAndDocuments(linkXeSo);

        for (Map.Entry<String, Document> mapUrlDocument : mapLinkTayGa.entrySet()) {
            listXeTayGa.addAll(crawlBikeSym(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
        for (Map.Entry<String, Document> mapUrlDocument : mapLinkXeSo.entrySet()) {
            listXeSo.addAll(crawlBikeSym(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
    }

    private List<Map<String, String>> crawlBikeSym(Document document, String url) {
        List<Map<String, String>> listBike = new ArrayList<>();
        String name = document.title().replace("| SYM Vietnam", "");
        String key, value;
        Gson gson = new Gson();
        //Dac diem noi bat
        Elements elements = document.select(".box-jssor-product");
        Map<String, String> outerBox = new HashMap<>();
        String features = "";
        for (Element e : elements) {
            key = e.select(".detail-title").text().replace(":", "");
            value = e.select(".detail-content").text();
            if (!key.equals("")) features += "|" + key + ":" + value;
        }
        outerBox.put("outstanding_features", features.replaceFirst("\\|", ""));
        //Thong so ky thuat
        //TH1: data inside td
        String specifications = "";
        elements = document.select(".box-detail-products:contains(THÔNG SỐ)");
        if (!elements.select("tr").isEmpty()) {
            for (Element sub : elements.select("tr")) {
                key = sub.select("td:eq(0)").text();
                value = sub.select("td:eq(1)").text();
                if (key.equals("")) specifications += "|" + key + ":" + value;
            }
        }
        //TH2: data inside li
        for (Element e : elements.select("li")) {
            key = e.select(".col-left").text();
            value = e.select(".col-right").text();
            if (!key.equals("")) {
                if (key.equals("")) specifications += "|" + key + ":" + value;
            }
        }
        outerBox.put("technical_specifications", specifications.replaceFirst("\\|", ""));
        String description = gson.toJson(outerBox);
//        System.out.println("des: " + description);
        //version and price
        String version, price, image;
        version = name;
        price = document.select(".num-price").text();
        price = price.replaceAll("[^0-9]", "")
                .replace(".", "");
        Map<String, String> bikeDetail = new HashMap<>();
        //get Image
        elements = document.select(".fancybox-thumb");
        List<Map<String, String>> listImage = new ArrayList<>();
        for (Element e : elements) {
            //color nằm trong class title
            Map<String, String> mapImage = new HashMap<>();
            key = e.select("a").attr("title");
            mapImage.put("color", key);
            //value trong thẻ a
            value = e.select("a").attr("href");
            mapImage.put("url", value);
            listImage.add(mapImage);
        }
        Map<String, Object> jsonImage = new HashMap<>();
        jsonImage.put("image", listImage);
        image = gson.toJson(jsonImage);
//        System.out.println("image: " + image);
        //tạo thông tin chi tiết bike và thêm vào list
        bikeDetail = bikeInfo(url, name, "SYM", description, version, price, image);
        listBike.add(bikeDetail);
        return listBike;
    }

    private void getBikeFromKymco(String allUrl) throws IOException {
        List<String> linkXeMoto = new ArrayList<>();
        List<String> linkXeTayGa = new ArrayList<>();
        List<String> linkXeSo = new ArrayList<>();
        //Get list link
        Document document = Jsoup.connect(allUrl).get();
        Elements elements = document.select(".product-row.row");
        //1st element is xe tay ga
        Element element = elements.get(0);
        String link;
        for (Element e : element.select("a[href~=(?i)\\.]")) {
            link = e.attr("href");
            if (link.contains("http")) {
                linkXeTayGa.add(link);
            }
        }
        element = elements.get(1);
        for (Element e : element.select("a[href~=(?i)\\.]")) {
            link = e.attr("href");
            if (link.contains("http")) {
                linkXeSo.add(link);
            }
        }
        element = elements.get(2);
        for (Element e : element.select("a[href~=(?i)\\.]")) {
            link = e.attr("href");
            if (link.contains("http")) {
                linkXeMoto.add(link);
            }
        }
        Map<String, Document> mapLinkMoTo = new HashMap<>();
        Map<String, Document> mapLinkTayGa = new HashMap<>();
        Map<String, Document> mapLinkXeSo = new HashMap<>();
        //Get Map Url and Document
        mapLinkTayGa = getListUrlsAndDocuments(linkXeTayGa);
        mapLinkXeSo = getListUrlsAndDocuments(linkXeSo);
        mapLinkMoTo = getListUrlsAndDocuments(linkXeMoto);

        for (Map.Entry<String, Document> mapUrlDocument : mapLinkMoTo.entrySet()) {
            listXeMoTo.addAll(crawlBikeKymco(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
        for (Map.Entry<String, Document> mapUrlDocument : mapLinkTayGa.entrySet()) {
            listXeTayGa.addAll(crawlBikeKymco(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
        for (Map.Entry<String, Document> mapUrlDocument : mapLinkXeSo.entrySet()) {
            listXeSo.addAll(crawlBikeKymco(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
    }

    private List<Map<String, String>> crawlBikeKymco(Document document, String url) {
        List<Map<String, String>> listBike = new ArrayList<>();
        Map<String, String> bikeDetail = new HashMap<>();
        String name, price, image, version;
        //get price and name
        price = document.select(".price.price-0").text();
        price = price.replaceAll("[^0-9]", "")
                .replace(".", "");
        name = document.title().replace(" | KYMCO Việt Nam", "");
        version = name;
        String key, value;
        Gson gson = new Gson();
        //Dac diem noi bat
        Elements elements = document.select(".content.h-fixed");
        Map<String, String> outerBox = new HashMap<>();
        String features = "";
        for (Element e : elements.select(".ct")) {
            key = e.select("h2").text();
            if (!key.equals("Giới Thiệu Sản Phẩm") && !key.equals("")) {
                features += "|" + key + ":" + e.select("h3").text();
            }
        }
        outerBox.put("outstanding_features", features.replaceFirst("\\|", ""));
        //Thong so ky thuat
        String specifications = "";
        elements = document.select(".tab-expand .content");
        for (Element e : elements.select("tr")) {
            key = e.select("td:eq(0)").text();
            value = e.select("td:eq(1)").text();
            if (!key.equals("")) specifications += "|" + key + ":" + value;
        }
        outerBox.put("technical_specifications", specifications.replaceFirst("\\|", ""));
        String description = gson.toJson(outerBox);
        Map<String, String> mapImage = new HashMap();
        List<Map<String,String>> listImage = new ArrayList<>();
        //lấy image
        for (Element e : document.select(".banner-top360.jarallax")) {
            Elements subElements = e.select(".select-color .item");
            //tạo map id và color
            Map<String, String> mapIdAndStyle = new HashMap<>();
            for (Element child : subElements) {
                key = child.attr("data-id");
                //color
                value = child.attr("style");
                mapIdAndStyle.put(key, value);
            }
            subElements = e.select(".product img");
            for (Element child : subElements) {
                key = child.attr("class").replace("img-top img-top-", "");
                for (Map.Entry<String, String> entry : mapIdAndStyle.entrySet()) {
                    //kiểm tra key và data-id
                    if (key.equals(entry.getKey())) {
                        key = entry.getValue();
                        key = key.replace("background:", "");
                        mapImage.put("color", key);
                    }
                }
                //thêm url
                key = "url";
                value = child.attr("src");
                mapImage.put(key, value);
                listImage.add(mapImage);
            }
        }
        Map<String, Object> jsonImage = new HashMap<>();
        jsonImage.put("image", listImage);
        image = gson.toJson(jsonImage);
//        //lấy thông tin bike và thêm vào list
        bikeDetail = bikeInfo(url, name, "KYMCO", description, version, price, image);
        listBike.add(bikeDetail);
        return listBike;
    }

//    private void getBikeFromSuzuki() throws IOException {
//        List<String> linkXeTayGa = new ArrayList<>();
//        List<String> linkXeSo = new ArrayList<>();
//        List<String> linkXeConTay = new ArrayList<>();
//        List<String> linkXeMoTo = new ArrayList<>();
//        //Get list link
//        Document document = Jsoup.connect("https://www.suzuki.com.vn/index.php/xe-motor").get();
//        Elements elements = document.select(".sppb-col-sm-6");
//        linkXeMoTo = getListLink(elements, "a", "href");
//        linkXeMoTo.contains("https://suzuki.com.vn/index.php/xe-motor/vstrom1000");
//        document = Jsoup.connect("https://www.suzuki.com.vn/index.php/xe-may").get();
//        elements = document.select(".item209").select(".maximenuck2 li");
//        linkXeConTay = getListLink(elements, "a", "abs:href");
//        elements = document.select(".item210").select(".maximenuck2 li");
//        linkXeTayGa = getListLink(elements, "a", "abs:href");
//        elements = document.select(".item211").select(".maximenuck2 li");
//        linkXeSo = getListLink(elements, "a", "abs:href");
//
//        Map<String, Document> mapLinkConTay = new HashMap<>();
//        Map<String, Document> mapLinkTayGa = new HashMap<>();
//        Map<String, Document> mapLinkXeSo = new HashMap<>();
//        Map<String, Document> mapLinkXeMoTo = new HashMap<>();
//        //Get Map Url and Document
//        mapLinkXeMoTo = getListUrlsAndDocuments(linkXeMoTo);
//        mapLinkTayGa = getListUrlsAndDocuments(linkXeTayGa);
//        mapLinkXeSo = getListUrlsAndDocuments(linkXeSo);
//        mapLinkConTay = getListUrlsAndDocuments(linkXeConTay);
//
//        for (Map.Entry<String, Document> mapUrlDocument : mapLinkTayGa.entrySet()) {
//            listXeTayGa.addAll(crawlBikeSuzuki(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
//        }
//        for (Map.Entry<String, Document> mapUrlDocument : mapLinkXeSo.entrySet()) {
//            listXeSo.addAll(crawlBikeSuzuki(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
//        }
//        for (Map.Entry<String, Document> mapUrlDocument : mapLinkConTay.entrySet()) {
//            listXeCon.addAll(crawlBikeSuzuki(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
//        }
//        for (Map.Entry<String, Document> mapUrlDocument : mapLinkXeMoTo.entrySet()) {
//            listXeMoTo.addAll(crawlBikeSuzuki(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
//        }
//    }
//
//    private List<Map<String, String>> crawlBikeSuzuki(Document document, String url) {
//        List<Map<String, String>> listBike = new ArrayList<>();
//        String price = "";
//        String name = document.title();
//        String key, value;
//        Gson gson = new Gson();
//        //Dac diem noi bat
//        String features = "";
//        String specifications = "";
//        List<String> listAccessories = new ArrayList<>();
//        String accessory = "";
//        Elements elements = document.select(".uk-panel.nopadding");//.uk-overlay-active
//        Map<String, String> outerBox = new HashMap<>();
//        for (Element e : elements) {
//            key = e.select("h3").text();
//            if (!key.equals("")) {
//                //Thong so ki thuat
//                if (!key.equals("THÔNG SỐ KĨ THUẬT") & !key.equals("PHỤ KIỆN & LINH KIỆN")) {
//                    value = e.select("span").text();
//                    features += "|" + key + ":" + value;
//                }
//            }
//        }
//        outerBox.put("outstanding_features", features.replaceFirst("\\|", ""));
//
//        String fakeKey = "";
//        for (Element e : document.select(".uk-margin table")) {
//            String titlle = e.select("thead").text();
//            for (Element sub : e.select("tbody tr")) {
//                switch (titlle) {
//                    case "KÍCH THƯỚC VÀ TRỌNG LƯỢNG":
//                        key = sub.select("td:eq(0)").text();
//                        value = sub.select("td:eq(2)").text()
//                                + sub.select("td:eq(1)").text();
//                        specifications += "|" + key + ":" + value;
//                        break;
//                    case "ĐỘNG CƠ":
//                        key = sub.select("td:eq(0)").text();
//                        value = sub.select("td:eq(1)").text();
//                        specifications += "|" + key + ":" + value;
//                        break;
//                    case "HỆ THỐNG TRUYỀN ĐỘNG":
//                        key = sub.select("td:eq(0)").text();
//                        value = sub.select("td:eq(1)").text();
//                        specifications += "|" + key + ":" + value;
//                        break;
//                    case "KHUNG SƯỜN":
//                        key = sub.select("td:eq(0)").text();
//                        value = sub.select("td:eq(1)").text();
////                                key = fakeKey + sub.select("td:eq(0)").text();
////                                value = sub.select("td:eq(1)").text();
//                        specifications += "|" + key + ":" + value;
//                        break;
//                }
//            }
//        }
//        outerBox.put("technical_specifications", specifications.replaceFirst("\\|", ""));
//        String description = gson.toJson(outerBox);
//        System.out.println(description);
//        elements = document.select(".smart-slider-border2 img");
//        String image;
//        Map<String, String> mapImage = new HashMap<>();
//        int i = 0;
//        for (Element e : elements) {
//            i++;
//            value = e.attr("src");
//            mapImage.put(i + "", value);
//        }
//        image = gson.toJson(mapImage);
//        System.out.println(image);
//        Map<String, String> bikeDetail = bikeInfo(url, name, "Suzuki", description, name, price, image);
//        listBike.add(bikeDetail);
//        return listBike;
//    }

    private void getBikeFromHonda(String allUrl) throws IOException {
        List<String> linkXeTayGa = new ArrayList<>();
        List<String> linkXeSo = new ArrayList<>();
        List<String> linkXeConTay = new ArrayList<>();

        //get link Xe So
        Document doc = Jsoup.connect(allUrl).get();
        Elements elements = doc.select("div.tabs-content.clearfix").select(".content-loai-xe-365");
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

        mapLinkTayGa = getListUrlsAndDocuments(linkXeTayGa);
        mapLinkXeSo = getListUrlsAndDocuments(linkXeSo);
        mapLinkConTay = getListUrlsAndDocuments(linkXeConTay);

        for (Map.Entry<String, Document> mapUrlDocument : mapLinkTayGa.entrySet()) {
            listXeTayGa.addAll(crawlBikeHonda(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
        for (Map.Entry<String, Document> mapUrlDocument : mapLinkXeSo.entrySet()) {
            listXeSo.addAll(crawlBikeHonda(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
        for (Map.Entry<String, Document> mapUrlDocument : mapLinkConTay.entrySet()) {
            listXeCon.addAll(crawlBikeHonda(mapUrlDocument.getValue(), mapUrlDocument.getKey()));
        }
    }

    private List<Map<String, String>> crawlBikeHonda(Document document, String url) {
        List<Map<String, String>> listBike = new ArrayList<>();
        String name = document.title().replace(" – Honda xe máy", "");
        String key, value;
        Gson gson = new Gson();
        //Dac diem noi bat
        Elements elements = document.select(".option");
        Map<String, String> outerBox = new HashMap<>();
        String features = "";
        for (Element e : elements) {
            key = e.select("a").attr("data-title").replaceAll(":", "");
            value = e.select("a").attr("data-description").
                    replace("<br />", " ").replace("<p>", "").
                    replace("</p>", "").replace("\n", "")
                    .replace("<i>(Hình ảnh mang tính minh hoạ)</i>", "");
            if (!key.equals("")) features += "|" + key + ":" + value;
        }
        outerBox.put("outstanding_features", features.replaceFirst("\\|", ""));
        //Thong so ky thuat
        String specifications = "";
        elements = document.select(".wrap-spec");
        for (Element e : elements.select("tr")) {
            key = e.select("td:eq(0)").get(0).text();
            value = e.select("td:eq(1)").get(0).text();
            if (!key.equals("")) {
                specifications += "|" + key + ":" + value;
            }
        }
        outerBox.put("technical_specifications", specifications.replaceFirst("\\|", ""));
        String description = gson.toJson(outerBox);
        //lấy default image
        elements = document.select("div.gallery-item img[src]");
        String defaultImage = elements.select("img").first().attr("src");
        //version and price
        String version, price, image;
        elements = document.select(".wrap-color");
        for (Element ec : elements.select(".box")) {
            Map<String, String> bikeDetail = new HashMap<>();
            //Create bike and add
            //get version
            //TH1: version hiển thị ở bên phải trong elements
            version = ec.select(".color-title").text()
                    .replace("*Hình ảnh minh họa có thể khác so với xe thực tế", "");
            //
            //TH2: version hiển thị ở bên trái trong elements
            if (version.equals("Màu sắc sản phẩm")) {
                //nhiểu hơn 1 version
                elements = ec.select(".data_version").select(".table-version").select("tr");
                for (Element tableDetail : elements) {
                    version = tableDetail.select("td:eq(0)").text();
                    price = tableDetail.select("td:eq(1)").text().replaceAll("[^0-9]", "")
                            .replace(".", "");
                    image = crawlImageHonda(defaultImage, ec);
                    bikeDetail = bikeInfo(url, name, "Honda", description, version, price, image);
                    listBike.add(bikeDetail);
                }
            } else {
                //chỉ có 1 version
                price = ec.select(".data_version").first().text().replaceAll("[^0-9]", "").replace(".", "");
                image = crawlImageHonda(defaultImage, ec);
                bikeDetail = bikeInfo(url, name, "Honda", description, version, price, image);
                listBike.add(bikeDetail);
            }
        }
        return listBike;
    }

    //lấy list link theo elements
    private List<String> getListLink(Elements elements, String selector, String attr) {
        String link;
        List<String> listLink = new ArrayList<>();
        for (Element e : elements.select(selector)) {
            link = e.attr(attr);
            if (link.contains("http")) {
                listLink.add(link);
            }
        }
        return listLink;
    }

    //lấy bản ghi chi tiết của accessory
    private Map<String, String> accessoryInfo(String url, String name, String brand, String description, String price,
                                              String image) {
        Map<String, String> accessoryDetail = new HashMap<>();
        accessoryDetail.put("name", name);
        accessoryDetail.put("url", url);
        accessoryDetail.put("description", description);
        accessoryDetail.put("brand", brand);
        accessoryDetail.put("status", statusActive);
        accessoryDetail.put("image", image);
        accessoryDetail.put("price", price);
        return accessoryDetail;
    }

    //lấy bản ghi chi tiết của bike
    private Map<String, String> bikeInfo(String url, String name, String brand, String description, String version,
                                         String price, String image) {
        Map<String, String> bikeDetail = new HashMap<>();
        bikeDetail.put("name", name);
        bikeDetail.put("url", url);
        bikeDetail.put("description", description);
        bikeDetail.put("brand", brand);
        bikeDetail.put("status", "NEW");
        bikeDetail.put("version", version);
        bikeDetail.put("image", image);
        bikeDetail.put("price", price);
        return bikeDetail;
    }

    //lấy link image của bike
    private String crawlImageHonda(String defaultImage, Element element) {
        Elements elements = element.select("a.item.get_data_version");
        List<Map<String, String>> listImage = new ArrayList<>();
        String key, value;
        for (Element e : elements) {
            Map<String, String> innerBox = new HashMap<>();
            key = "color";
            value = e.select(".text").first().text();
            innerBox.put(key, value);
            key = "url";
            value = e.select(".no-360 img[src]").attr("src");
            //nếu không có image dùng image mặc định
            if (value.equals("")) value = defaultImage;
            innerBox.put(key, value);
            listImage.add(innerBox);
        }
        Map<String, Object> jsonImage = new HashMap<>();
        key = "image";
        jsonImage.put(key, listImage);
        Gson gson = new Gson();
        value = gson.toJson(jsonImage);
        return value;
    }

    //tạo map url và document
    private Map<String, Document> getListUrlsAndDocuments(List<String> listLink) throws IOException {
        Map<String, Document> mapDocument = new HashMap<>();
        for (String url : listLink) {
            if (url.equals("https://suzuki.com.vn/index.php/xe-motor/vstrom1000")) {
                url = "https://suzuki.com.vn/index.php/xe-motor/v-strom-1000";
            }
            if (url.contains("yamaha")) {
                mapDocument.put(url, Jsoup.connect(url).validateTLSCertificates(false).get());
            } else if (url.contains("http")) {
                mapDocument.put(url, Jsoup.connect(url).get());
            }
        }
        return mapDocument;
    }

    //lấy text trong thẻ div
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

    //lấy toàn bộ link sản phẩm trong trang tổng theo selector và attr
    private List<String> getAllProductLinks(String url, String selector, String attr) throws IOException {
        Elements links = getElements(url, selector);
        List<String> linkList = getListLink(links,selector,attr);
        return linkList;
    }

//    private String getColor(String colorText) {
//        String color = "Cập nhật sau";
//        if (colorText.contains("background-color")) {
//            color = colorText.replace("background-color: ", "");
//        } else if (colorText.contains("background-image")) {
//
//        }
//        return color;
//    }

}