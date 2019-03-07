package capstone.bwa.demo.controllers;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.entities.ImageEntity;
import capstone.bwa.demo.repositories.EventRepository;
import capstone.bwa.demo.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("event/{id}/images")
    public ResponseEntity setEventListImages(@PathVariable int id, @RequestBody Map<String, List<String>> body) {
        EventEntity entity = eventRepository.findById(id);
        if (entity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        List<String> listImgs = body.get("images");
        List<ImageEntity> list = imageRepository.findAllByEventByOwnId_IdAndType(id, MainConstants.STATUS_EVENT);
        if (list.size() > 0)
            imageRepository.deleteAll(list);
        list = new ArrayList<>();
        for (String item : listImgs) {
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setUrl(item);
            imageEntity.setOwnId(id);
            imageEntity.setType(MainConstants.STATUS_EVENT);
            list.add(imageEntity);
        }
        imageRepository.saveAll(list);
        return new ResponseEntity(HttpStatus.OK);
    }
}
