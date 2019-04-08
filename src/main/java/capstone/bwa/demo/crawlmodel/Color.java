package capstone.bwa.demo.crawlmodel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Color {
    public static void main(String[] args) {
        try {
            Document document = Jsoup.connect("https://www.webike.vn/cho-xe-may/dang-tin-ban-xe.html").get();
            Elements elements = document.select("#color_code option");
            String color;
            List<String> listColorNames = new ArrayList<>();
            for (Element e : elements) {
                if (!e.text().equals("")) {
                    color = e.text();
                    listColorNames.add(color);
                }
            }
        } catch (IOException e) {

        }
    }
}
