package capstone.bwa.demo.crawldata;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.NewsEntity;
import capstone.bwa.demo.entities.ReferencesLinkEntity;
import capstone.bwa.demo.repositories.ImageRepository;
import capstone.bwa.demo.repositories.NewsRepository;
import capstone.bwa.demo.repositories.ReferencesLinkRepository;
import capstone.bwa.demo.utils.DateTimeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsCrawler {
    private static final Logger logger = LoggerFactory.getLogger(NewsCrawler.class);

    private ReferencesLinkRepository referencesLinkRepository;
    private NewsRepository newsRepository;
    private ImageRepository imageRepository;

    public NewsCrawler(ReferencesLinkRepository referencesLinkRepository, NewsRepository newsRepository, ImageRepository imageRepository) {
        this.referencesLinkRepository = referencesLinkRepository;
        this.newsRepository = newsRepository;
        this.imageRepository = imageRepository;
    }

    public void crawNewsJsoup() {

        String domain = "https://danhgiaxe.net/danh-gia-xe/";

        if (!referencesLinkRepository.existsByUrl(domain)) {
            ReferencesLinkEntity referencesLinkEntity = new ReferencesLinkEntity();
            referencesLinkEntity.setUrl(domain);
            referencesLinkRepository.saveAndFlush(referencesLinkEntity);
        }

        try {
            Document doc = Jsoup.connect(domain).get();
            int totalPage = Integer.parseInt(doc.select(".page-numbers:nth-child(5)").first().text());
            String link = doc.select(".page-numbers:nth-child(5)").first().attr("href");

            link = link.substring(0, link.length() - 1);

            link = link.substring(0, link.lastIndexOf("/") + 1);

            List<NewsEntity> entities = new ArrayList<>();

            for (int i = 2; i <= 100; i++) {
                try {
                    String nextLink = link + i;
                    String userAgent = "Mozilla/5.0 (X11; U; Linux i586; en-US; rv:1.7.3) Gecko/20040924 Epiphany/1.4.4 (Ubuntu)";
                    Document page = Jsoup.connect(nextLink).userAgent(userAgent).get();
                    Elements elements = page.select(".entry-title > a");
                    for (Element element : elements) {
                        Document detailPage = Jsoup.connect(element.attr("href"))
                                .userAgent(userAgent).get();
                        System.out.println(nextLink);
                        Element articleElement = detailPage.select(".post").first();

                        //remove footer of article
                        Element footer = articleElement.select(".entry-footer").first();
                        footer.remove();

                        //remove date of article
                        Element metadata = articleElement.select(".entry-meta").first();
                        metadata.remove();

                        //get html of article
                        String article = articleElement.html();
                        article = article.replaceAll("class=\"([^']*?)\"", "");
                        String title = detailPage.select(".entry-title").first().text();

                        NewsEntity entity = new NewsEntity();
                        //add source and link to article
                        article += "<p>(Nguồn: <a href='" + element.attr("href") + "'>" + element.attr("href") + "</a> )";
                        entity.setDescription(article);
                        entity.setStatus(MainConstants.NEWS_DRAFT);
                        entity.setCreatedTime(DateTimeUtils.getCurrentTime());
                        entity.setTitle(title);
                        entity.setImgThumbnailUrl(articleElement.selectFirst("img").attr("src"));
                        //get first image in article
//                        ImageEntity imageEntity = new ImageEntity();
//                        imageEntity.setUrl(articleElement.selectFirst("img").attr("src"));
//                        List<ImageEntity> imageEntityList = new ArrayList<>();
//                        imageEntityList.add(imageEntity);
//                        entity.setImagesById(imageEntityList);

                        entities.add(entity);
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage() + "location: " + ex.getLocalizedMessage());
                }
            }

            insertNews(newsRepository, entities);
        } catch (IOException ex) {
            logger.error(ex.getMessage() + "location: " + ex.getLocalizedMessage());
        }


    }


    public void insertNews(NewsRepository newsRepository, List<NewsEntity> entities) {
        for (NewsEntity entity : entities) {
            if (!newsRepository.existsByTitle(entity.getTitle().trim())) {
                try {
                    newsRepository.save(entity);
//
//                    for (ImageEntity imageEntity : entity.getImagesById()) {
//                        imageEntity.setOwnId(newsId);
//                        imageEntity.setType(MainConstants.STATUS_BIKE);
//                        imageRepository.saveAndFlush(imageEntity);
//                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage() + " " + entity.getTitle());
                }
            }
        }
    }
}
