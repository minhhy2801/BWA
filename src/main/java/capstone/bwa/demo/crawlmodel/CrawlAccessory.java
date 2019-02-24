package capstone.bwa.demo.crawlmodel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CrawlAccessory {
    private List<Map<String, String>> listAccessories;
    private final String statusActive = "ACTIVE";

    public List<Map<String, String>> getListAccessories() {
        return listAccessories;
    }

    public CrawlAccessory() {
        listAccessories = new ArrayList<>();
    }

    public void crawlAccessoryFromHonda(String url) {
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //lấy toàn bộ link sản phẩm trong trang tổng theo selector và attr
    private List<String> getAllProductLinks(String url, String selector, String attr) throws IOException {
        Elements links = getElements(url, selector);
        List<String> linkList = getListLink(links, selector, attr);
        return linkList;
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

    //lấy elements theo url và selector
    private Elements getElements(String url, String selector) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.select(selector);
    }

    //lấy bản ghi chi tiết của accessory theo thông tin truyền vào
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

    //lấy elements từ document theo selector
    private Elements getContentFromDoc(Document doc, String selector) {
        return doc.select(selector);
    }

    //lấy elements từ elements theo selector
    private Elements getElementsFromElements(Elements elements, String selector) {
        return elements.select(selector);
    }

    //Lấy map url và document
    private Map<String, Document> getListUrlsAndDocuments(List<String> listLink) {
        Map<String, Document> mapDocument = new HashMap<>();
        for (String url : listLink) {
            //chỉ map nếu url có chứa http
            if (url.contains("http")) {
                try{
                    mapDocument.put(url, Jsoup.connect(url).get());
                }catch (IOException e){
                    System.out.println("loi... " + e.getMessage());
                }
            }
        }
        return mapDocument;
    }


}
