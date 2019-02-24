package capstone.bwa.demo.crawlmodel;

import capstone.bwa.demo.entities.ImageEntity;
import capstone.bwa.demo.entities.NewsEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CrawlNews {
    private String domain;
    private List<String> pages;
    private List<String> newsUrls;
    private final String END_LINE_CHAR = "\n";
    private final String statusActive = "ACTIVE";
    private HashMap<NewsEntity, ImageEntity> results;

    public CrawlNews() {
        this.newsUrls = new ArrayList<>();
        this.pages = new ArrayList<>();
        this.results = new HashMap<>();
    }

    //ham tong quat, de api goi
    public void crawlNews() {
        try {
            getPagesLink(); //lấy tất cả link theo số trang
            getAllNewsLink(); //lấy tất cả link bài viết của các link phía trên
            for (String url : newsUrls) {
                //TH đặc biệt, loại bỏ những link này
                if (url.equals("https://hondaxemay.com.vn/tin-tuc/honda-winner-150-phoi-mau-moi-phong-cach-cung-tem-xe" +
                        "-rieng-biet-doi-dien-mao-them-tao-bao-2/") || url.equals("https://motoanhquoc.vn/tam-su-" +
                        "tuoi-30/.html")) {
                    newsUrls.remove(url);
                    break;
                }
            }
            //crawl bài viết từ các link bài viết đã lấy và lưu trữ lại vào map result
            List<Document> mapDocument = new ArrayList<>();
            for (String newsURL : newsUrls) {
                Document document = Jsoup.connect(newsURL).get();
                mapDocument.add(document);
            }
            for (Document document : mapDocument) {
                crawlNewsDetail(document);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //crawl nội dung của bài viết và hình ảnh
    public void crawlNewsDetail(Document document) throws IOException {
        //tạo image entity
        ImageEntity imageEntity = new ImageEntity();
        String url = crawlImages(document);
        imageEntity.setUrl(url);
        if (imageEntity.getUrl().equals("")) return;

        //tạo news entity
        String title = crawlTitle(document);
        String description = crawlText(document);

        NewsEntity newsEntity = new NewsEntity();
        newsEntity.setTitle(title);
        newsEntity.setDescription(description);

        newsEntity.setImgThumbnailUrl(getFirstImageURL(imageEntity));
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        Date date = new Date(System.currentTimeMillis());
        newsEntity.setCreatedTime(dateFormat.format(date));
        newsEntity.setStatus(statusActive);
        imageEntity.setNewsByOwnId(newsEntity);

        this.results.put(newsEntity, imageEntity);
    }

    //lấy link hình ảnh đầu tiên trong url của ImageEntity để làm thumbnail
    private String getFirstImageURL(ImageEntity imageEntity) {
        if (imageEntity != null && !imageEntity.getUrl().equals("")) {
            StringTokenizer tokenizer = new StringTokenizer(imageEntity.getUrl(), ",");
            return tokenizer.nextToken();
        }
        return null;
    }

    //crawl title của bài viết
    private String crawlTitle(Document document) throws IOException {
        String selector = "";
        if (domain.equals("https://hondaxemay.com.vn/tin-tuc/")) {
            //class chứa tittle
            selector = "div#content-print h1.title";
        } else if (domain.equals("https://motoanhquoc.vn/tin-tuc")) {
            selector = ".col-md-12 h2.post-title";
        } else if (domain.equals("https://autodaily.vn/chuyen-muc/xe-moi/xe-may/14")) {
            selector = "h2.article-title";
        }
        Elements elements = document.select(selector);
        for (Element element : elements) {
            if (!element.text().equals("")) return element.text();
        }
        return null;
    }

    //crawl nội dung chữ của bài viết
    private String crawlText(Document document) throws IOException {
        //format cua content:
        //cuoi moi doan se ket thuc bang ky tu \n
        //cac noi dung kieu liet ke trong <ul> <li> se co dang - noidung, cuoi moi dong van co ky tu \n
        //doan cuoi cung se ko co ky tu \n
        String selector = "";
        if (domain.equals("https://motoanhquoc.vn/tin-tuc")) {
            selector = ".post-content";
        } else if (domain.equals("https://hondaxemay.com.vn/tin-tuc/")) {
            selector = "div#content-print div.editable";
        } else if (domain.equals("https://autodaily.vn/chuyen-muc/xe-moi/xe-may/14")) {
            selector = ".article-detail";
        }
        Elements divElements = document.select(selector);
        String content = "";
        for (Element divElement : divElements) {

            for (Element childEl : divElement.children()) {
                if (childEl.tagName().equals("p")) {
                    content += childEl.text() + END_LINE_CHAR;
                } else if (childEl.tagName().equals("ul") || childEl.tagName().equals("ol")) {
                    content += handleUlLiElement(childEl);
                } else {
                    //ko xu ly truong hop the <table>
                    content += childEl.text() + END_LINE_CHAR;
                }
            }
        }
        //loại bỏ ký tự \n cuối cùng trong content
        if (!content.equals("")) {
            int index = content.lastIndexOf(END_LINE_CHAR);
            content = content.substring(0, index);
        }
        return content;
    }

    //crawl lấy link các hình ảnh trong bài viết, tạo thành chuỗi url cho ImageEntity
    private String crawlImages(Document document) throws IOException {
        String selector = "";
        if (domain.equals("https://motoanhquoc.vn/tin-tuc")) {
            selector = ".post-content img[src]";
        } else if (domain.equals("https://hondaxemay.com.vn/tin-tuc/")) {
            selector = "div#content-print div.editable img[src]";
        } else if (domain.equals("https://autodaily.vn/chuyen-muc/xe-moi/xe-may/14")) {
            selector = ".article-detail img[src]";
        }
        Elements imgElements = document.select(selector);
        String imgUrls = "";
        //lưu hình ảnh dùng dấu , để tách các ảnh
        for (int i = 0; i < imgElements.size(); i++) {
            if (i == (imgElements.size() - 1)) //last element
                imgUrls += imgElements.get(i).attr("src");
            else imgUrls += imgElements.get(i).attr("src") + ",";
        }
        return imgUrls;
    }

    //crawl nội dung có trong thẻ ul li của bài viết
    private String handleUlLiElement(Element ulElement) {
        String content = "";
        for (Element liElement : ulElement.children()) {
            content += "- " + liElement.text() + END_LINE_CHAR;
        }
        return content;
    }

    //từ các link trang tin tức, crawl lấy link đến trực tiếp bài viết
    private void getAllNewsLink() throws IOException {
        String selector = "";
        if (domain.equals("https://hondaxemay.com.vn/tin-tuc/")) {
            //lấy link từ khung lớn (bài viết nổi bật)
            crawlNewsLink(this.pages.get(0), "div.large-left > a");
            //lấy link từ khung nhỏ (các bài viết nổi bật)
            crawlNewsLink(this.pages.get(0), "div.small-box > a");
            //selector dùng để tách lấy link bài viết
            selector = "div.row-list > div.news-item > div.inner > a";
        } else if (domain.equals("https://motoanhquoc.vn/tin-tuc")) {
            selector = ".title-relative > a";
        } else if (domain.equals("https://autodaily.vn/chuyen-muc/xe-moi/xe-may/14")) {
            selector = ".late-news-tit > a";
        }
        for (String page : this.pages) {
            crawlNewsLink(page, selector);
        }
    }

    //lấy link các trang tin tức từ trand domain đưa vào, các trang này dạng đánh số 1, 2, 3, ...
    public void getPagesLink() throws IOException {
        Document document = Jsoup.connect(domain).get();
        String selector = "";
        int remove = 1;
        int index = 1;
        if (domain.equals("https://hondaxemay.com.vn/tin-tuc/")) {
            selector = "ul.pagination li a[class^='page-numbers']";
            //do trang này có dấu / ở cuối
            index = 2;
        } else if (domain.equals("https://motoanhquoc.vn/tin-tuc")) {
            //do trang này không có dấu / ở cuối
            selector = ".page-numbers a[class^='page-numbers']";
        } else if (domain.equals("https://autodaily.vn/chuyen-muc/xe-moi/xe-may/14")) {
            selector = "ul.paginations li a";
            //trang này có nút tiến ở cuối
            remove = 2;
        }
        Elements pageNumbers = document.select(selector);
        //cắt bỏ số trang
        //vd: /page/24/ -> /page/
        int size = pageNumbers.size() - remove;
        int lastPageNumber = Integer.parseInt(pageNumbers.get(size).text());
        String url = pageNumbers.last().attr("href");
        //cắt theo dấu /
        int secondIndex = getNthLastIndexOf(index, "/", url);
        String temp = url.substring(0, secondIndex + 1);

        //thay số vào để ra từng link
        for (int i = lastPageNumber; i >= 1; i--) {
            pages.add(temp + i + "/");
        }
    }

    //get n th last character
    private int getNthLastIndexOf(int nth, String ch, String string) {
        if (nth <= 0) return string.length();
        return getNthLastIndexOf(--nth, ch, string.substring(0, string.lastIndexOf(ch)));
    }

    //hàm tổng quát để lấy link cụ thể các bài viết, tùy thuộc vào câu query truyền vào
    private void crawlNewsLink(String pageLink, String cssQuery) throws IOException {
        Document document = Jsoup.connect(pageLink).get();
        Elements newsLinks = document.select(cssQuery);
        for (Element link : newsLinks) {
            newsUrls.add(link.attr("href"));
        }
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public HashMap<NewsEntity, ImageEntity> getResults() {
        return results;
    }
}