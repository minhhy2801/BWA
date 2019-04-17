package capstone.bwa.demo.controllers;

import capstone.bwa.demo.crawldata.AccessoryCrawler;
import capstone.bwa.demo.crawldata.BikeCrawler;
import capstone.bwa.demo.crawldata.NewsCrawler;
import capstone.bwa.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrawlController {
    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private AccessoryRepository accessoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private ReferencesLinkRepository referencesLinkRepository;

    @GetMapping("crawl/bike")
    public ResponseEntity crawlBikes() {

        BikeCrawler bikeCrawler = new BikeCrawler(categoryRepository, bikeRepository, imageRepository, referencesLinkRepository);
        bikeCrawler.crawlAndInsertDB();
        return new ResponseEntity("Success", HttpStatus.OK);
    }

    @GetMapping("crawl/accessory")
    public ResponseEntity crawlAccessories() {
        AccessoryCrawler accessoryCrawler = new AccessoryCrawler(categoryRepository, accessoryRepository, imageRepository, referencesLinkRepository);
        accessoryCrawler.crawAndInsertDB();
        return new ResponseEntity("Success", HttpStatus.OK);
    }

    @GetMapping("crawl/news")
    public ResponseEntity crawNews() {
        NewsCrawler newsCrawler = new NewsCrawler(referencesLinkRepository, newsRepository, imageRepository);
        newsCrawler.crawNewsJsoup();
        return new ResponseEntity("Success", HttpStatus.OK);
    }
}
