package capstone.bwa.demo.controllers;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.entities.ImageEntity;
import capstone.bwa.demo.entities.NewsEntity;
import capstone.bwa.demo.entities.SupplyProductEntity;
import capstone.bwa.demo.repositories.EventRepository;
import capstone.bwa.demo.repositories.ImageRepository;
import capstone.bwa.demo.repositories.NewsRepository;
import capstone.bwa.demo.repositories.SupplyProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ImageController {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private SupplyProductRepository supplyProductRepository;
    @Autowired
    private NewsRepository newsRepository;

    @PostMapping("event/{id}/images")
    public ResponseEntity setEventListImages(@PathVariable int id, @RequestBody Map<String, List<String>> body) {
        EventEntity entity = eventRepository.findById(id);
        if (entity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<ImageEntity> list = imageRepository.findAllByOwnIdAndType(id, MainConstants.STATUS_EVENT);
        if (list.size() > 0) imageRepository.deleteAll(list);
        List<ImageEntity> tmp = setImagesForObj(body.get("images"), id, MainConstants.STATUS_EVENT);
        imageRepository.saveAll(tmp);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("news/{id}/images")
    public ResponseEntity setNewsListImages(@PathVariable int id, @RequestBody Map<String, List<String>> body) {
        NewsEntity newsEntity = newsRepository.findById(id);
        if (newsEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        List<ImageEntity> list = imageRepository.findAllByOwnIdAndType(id, MainConstants.STATUS_NEWS);
        if (list.size() > 0) imageRepository.deleteAll(list);
        List<ImageEntity> tmp = setImagesForObj(body.get("images"), id, MainConstants.STATUS_NEWS);
        imageRepository.saveAll(tmp);

        return new ResponseEntity(HttpStatus.OK);
    }


    @PostMapping("supply_post/{id}/images")
    public ResponseEntity setSupplyListImages(@PathVariable int id, @RequestBody Map<String, List<String>> body) {
        SupplyProductEntity entity = supplyProductRepository.findById(id);
        if (entity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        List<ImageEntity> list = imageRepository.findAllByOwnIdAndType(id, MainConstants.STATUS_SUPPLY_POST);

        if (list.size() > 0) imageRepository.deleteAll(list);

        List<ImageEntity> tmp = setImagesForObj(body.get("images"), id, MainConstants.STATUS_SUPPLY_POST);
        imageRepository.saveAll(tmp);

        return new ResponseEntity(HttpStatus.OK);
    }

    private List<ImageEntity> setImagesForObj(List<String> listImgs, int id, String type) {
        List<ImageEntity> tmp = new ArrayList<>();
        for (String item : listImgs) {
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setUrl(item);
            imageEntity.setOwnId(id);
            imageEntity.setType(type);
            tmp.add(imageEntity);
        }
        return tmp;
    }
}
