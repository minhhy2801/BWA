package capstone.bwa.demo.crawlmodel;

import capstone.bwa.demo.entities.ImageEntity;
import capstone.bwa.demo.entities.NewsEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class HondaxemayCrawler {
    private String domain;
    private List<String> pages;
    private List<String> newsUrls;
    private final String END_LINE_CHAR = "\n";
    private HashMap<NewsEntity, ImageEntity> results;

    public HondaxemayCrawler() {
        this.newsUrls = new ArrayList<>();
        this.pages = new ArrayList<>();
        results = new HashMap<>();
    }

//    public static void main(String[] args) {
//        HondaxemayCrawler crawler = new HondaxemayCrawler();
////        crawler.setDomain("https://autodaily.vn/chuyen-muc/xe-moi/xe-may/14");
////        crawler.setDomain("https://motoanhquoc.vn/tin-tuc");
//        crawler.setDomain("https://hondaxemay.com.vn/tin-tuc/");
//        crawler.crawl();
//    }

    //ham tong quat, de api goi
    public void crawl() {
        try {
            getPagesLink(); //lay tat ca link theo so trang cua domain dua vao
            getAllNewsLink(); //lay link cua tat ca bai viet từ những link trang đã lấy phía trên
            for (String url : newsUrls) {
                //TH dac biệt, phải bỏ link này
                if (url.equals("https://hondaxemay.com.vn/tin-tuc/honda-winner-150-phoi-mau-moi-phong-cach-cung-tem-xe-rieng-biet-doi-dien-mao-them-tao-bao-2/")) {
                    newsUrls.remove(url);
                    break;
                }
            }
            //crawl bài viết từ các link bài viết đã lấy và lưu trữ lại vào map result
            for (String newsURL : newsUrls) {
                System.out.println(newsURL);
                crawlNews(newsURL);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //crawl nội dung của bài viết và hình ảnh
    public void crawlNews(String newsURL) throws IOException {
        //get image entity
        ImageEntity imageEntity = new ImageEntity();
        String url = crawlImages(newsURL);
        imageEntity.setUrl(url);
        if (imageEntity.getUrl().equals(""))
            return;

        //get news entity
        String title = crawlTitle(newsURL);
        String description = crawlText(newsURL);

        NewsEntity newsEntity = new NewsEntity();
        newsEntity.setTitle(title);
        newsEntity.setDescription(description);

        newsEntity.setImgThumbnailUrl(getFirstImageURL(imageEntity));
        newsEntity.setCreatedTime(java.time.LocalDate.now().toString());
        newsEntity.setStatus("NEW");

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
    private String crawlTitle(String newsURL) throws IOException {
        Document document = Jsoup.connect(newsURL).get();
        String selector = "";
        if (domain.equals("https://hondaxemay.com.vn/tin-tuc/")) {
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
    private String crawlText(String newsURL) throws IOException {
        //format cua content:
        //cuoi moi doan se ket thuc bang ky tu \n
        //cac noi dung kieu liet ke trong <ul> <li> se co dang - noidung, cuoi moi dong van co ky tu \n
        //doan cuoi cung se ko co ky tu \n
        Document document = Jsoup.connect(newsURL).get();
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
    private String crawlImages(String newsUrl) throws IOException {
        Document document = Jsoup.connect(newsUrl).get();
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
        for (int i = 0; i < imgElements.size(); i++) {
            if (i == (imgElements.size() - 1)) //last element
                imgUrls += imgElements.get(i).attr("src");
            else imgUrls += imgElements.get(i).attr("src") + ",";
        }
        System.out.println("Image: " + imgUrls);
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
            //get link large-left news
            //all page will have same this news, so we just crawl 1 time at first page
            crawlNewsLink(this.pages.get(0), "div.large-left > a");
            //get link small-box news
            //all page will have same this news, so we just crawl 1 time at first page
            crawlNewsLink(this.pages.get(0), "div.small-box > a");
            selector = "div.row-list > div.news-item > div.inner > a";
        } else if (domain.equals("https://motoanhquoc.vn/tin-tuc")) {
            selector = ".title-relative > a";
        } else if (domain.equals("https://autodaily.vn/chuyen-muc/xe-moi/xe-may/14")) {
            selector = ".late-news-tit > a";
        }

        for (String page : this.pages) {
            //get link row-list news

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
            //do trang có dấu / ở cuối
            index = 2;
        } else if (domain.equals("https://motoanhquoc.vn/tin-tuc")) {
            selector = ".page-numbers a[class^='page-numbers']";
        } else if (domain.equals("https://autodaily.vn/chuyen-muc/xe-moi/xe-may/14")) {
            selector = "ul.paginations li a";
            //trang có nút tiến ở cuối
            remove = 2;
        }
        Elements pageNumbers = document.select(selector);
        //cat bo so trang da co
        //vd: /page/24/ -> /page/
        int size = pageNumbers.size() - remove;
        int lastPageNumber = Integer.parseInt(pageNumbers.get(size).text());
//        System.out.println("last: " + lastPageNumber);
        String url = pageNumbers.last().attr("href");
        System.out.println("linkbefor: "+url);
        //cắt theo dấu /
        int secondIndex = getNthLastIndexOf(index, "/", url);
        String temp = url.substring(0, secondIndex + 1);
        System.out.println("linkafter: "+temp);

        //thay so vao de ra tung link
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


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<String> getNewsUrls() {
        return newsUrls;
    }

    public void setNewsUrls(List<String> newsUrls) {
        this.newsUrls = newsUrls;
    }

    public List<String> getPages() {
        return pages;
    }

    public void setPages(List<String> pages) {
        this.pages = pages;
    }

    public HashMap<NewsEntity, ImageEntity> getResults() {
        return results;
    }

    public void setResults(HashMap<NewsEntity, ImageEntity> results) {
        this.results = results;
    }
}