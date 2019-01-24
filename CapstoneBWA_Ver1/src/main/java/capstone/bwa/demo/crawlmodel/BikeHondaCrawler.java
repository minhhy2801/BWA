package capstone.bwa.demo.crawlmodel;

import capstone.bwa.demo.entities.BikeEntity;
import capstone.bwa.demo.entities.ImageEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BikeHondaCrawler {
    Map<String, List<String>> listImage;

//    public List<BikeEntity> crawlHonda() throws IOException {
    public static void main(String[] args) throws IOException{
        BikeHondaCrawler crawler = new BikeHondaCrawler();
        List<String> linkList = crawler.getAllProductLinks("https://hondaxemay.com.vn/san-pham/");
        List<BikeEntity> BikeList = new ArrayList<>();
        for (String url : linkList){
            if (!url.equals("")) {
                BikeEntity newBike = crawler.getProduct(url);
                BikeList.add(newBike);
            }
        }
        System.out.println(BikeList.toString());
//        return BikeList;
    }

    private BikeEntity getProduct(String url) throws IOException{
        BikeEntity newBike = new BikeEntity();
        String name = getName(url);
        newBike.setName(name);
        newBike.setUrl(url);
        newBike.setBrand("Honda");
        String price = getPrice(url,".wrap-color");
        newBike.setPrice(price);
        newBike.setDescription(getData(url,"div.wrap-spec"));
        newBike.setStatus("New");
        int hash = (url + name + price).hashCode();
        newBike.setHashCode("" + hash);
        newBike.setCategoryId(1);
//        Collection<Map<String,List<String>>> img = new ArrayList<>();
//        img.add(getListImage("ProductDetail",url,".option-img","abs:data-img"));
//        img.add(getListImage("ColorDetail",url,".wrap-360","src"));
//        img.add(getListImage("Gallery",url,".gallery-item","src"));
        return newBike;
    }

    private List<String> getAllProductLinks(String url)throws IOException{
        Elements links = getElements(url,"a.btn");
        List<String> linkList = new ArrayList<>();
        links.forEach((link) -> {
            if (!linkList.contains(link.attr("href"))) {
                linkList.add(link.attr("href"));
            }
        });
        return linkList;
    }

    private Elements getElements(String url, String selector) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select(selector);
        return links;
    }

    private String getPrice(String url, String selector) throws IOException {
        Elements rawData = getElements(url,selector);
        String data = "";
        List<String> listPrice = new ArrayList<>();
        for (Element element:rawData){
            String price="";
            price = element.getElementsByClass(".color-title").text() + "\n"
                    + element.getElementsByClass(".text").text() + "\n" + element.text() + "\n";
            listPrice.add(price);
        };
        for (String price: listPrice){
            data += price +"\n";
        }
        return data;
    }

    private String getName(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String name = doc.title();
        return name;
    }

    private String getData(String url, String selector) throws IOException {
        Elements rawData = getElements(url,selector);
        String data = "";
        for (Element element:rawData.select("td")){
            data += element.text() + "\n";
        };
        return data;
    }

    private Map<String, List<String>> getListImage(String key, String url, String selector, String attr) throws IOException {
        Elements rawData = getElements(url,selector);
        List<String> linkImages = new ArrayList<>();
        rawData.forEach((content) -> {
            linkImages.add(content.absUrl(attr));
        });
        listImage.put(key,linkImages);
        return listImage;
    }

}
