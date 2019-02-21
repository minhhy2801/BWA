package capstone.bwa.demo.controllers;


import capstone.bwa.demo.entities.CategoryEntity;
import capstone.bwa.demo.entities.ReferencesLinkEntity;
import capstone.bwa.demo.repositories.CategoryRepository;
import capstone.bwa.demo.repositories.ReferencesLinkRepository;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("ref_links")
public class ReferencesLinkController {

    @Autowired
    ReferencesLinkRepository referencesLinkRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping("admin/ref_links")
    public ResponseEntity getAllReferencesLink() {
        if (referencesLinkRepository.findAll().isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        return new ResponseEntity(referencesLinkRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping("admin/ref_links/create")
    public ResponseEntity createReferencesLink(@RequestBody Map<String, String> body) {
        String name = body.get("categoryName");
        if (categoryRepository.findByName(name) == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        CategoryEntity categoryEntity = categoryRepository.findByName(name);
        String url = body.get("url");
        String status = body.get("status");
        ReferencesLinkEntity referencesLinkEntity = new ReferencesLinkEntity();
        referencesLinkEntity.setUrl(url);
        referencesLinkEntity.setStatus(status);
        referencesLinkEntity.setCategoryId(categoryEntity.getId());
        referencesLinkEntity.setCategoryByCategoryId(categoryEntity);
        referencesLinkRepository.saveAndFlush(referencesLinkEntity);
        return new ResponseEntity(referencesLinkEntity, HttpStatus.CREATED);
    }

    @PutMapping("admin/ref_links/{id}")
    public ResponseEntity updateReferencesLink(@PathVariable int id, @RequestBody Map<String, String> body) {
        String name = body.get("categoryName");
        CategoryEntity categoryEntity = categoryRepository.findByName(name);
        if (categoryEntity == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        ReferencesLinkEntity referencesLinkEntity = referencesLinkRepository.findById(id);
        if (referencesLinkEntity == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        String url = body.get("url");
        String status = body.get("status");
        referencesLinkEntity.setCategoryId(categoryEntity.getId());
        referencesLinkEntity.setUrl(url);
        referencesLinkEntity.setStatus(status);
        referencesLinkEntity.setCategoryByCategoryId(categoryEntity);
        categoryRepository.saveAndFlush(categoryEntity);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("admin/ref_links/{id}")
    public ResponseEntity deleteReferencesLink(@PathVariable int id) {
        ReferencesLinkEntity referencesLinkEntity = referencesLinkRepository.findById(id);
        if (referencesLinkEntity != null) {
            referencesLinkEntity.setStatus("InActive");
            referencesLinkRepository.saveAndFlush(referencesLinkEntity);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("admin/ref_links/{id}")
    public ResponseEntity searchReferencesLinkByID(@PathVariable int id) {
        ReferencesLinkEntity referencesLinkEntity = referencesLinkRepository.findById(id);
        if (referencesLinkEntity != null) {
            return new ResponseEntity(referencesLinkEntity, HttpStatus.FOUND);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    private List<String> getListLinkAccessories(String categoryName) {
        List<String> listLinks = new ArrayList<>();
        switch (categoryName) {
            case ("Phụ kiện dán"):
                listLinks.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3840&filter_orderby=latest&filter_tax=3719");
                listLinks.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3829&filter_orderby=latest&filter_tax=3719");
                return listLinks;
            case ("Phụ kiện lắp thêm"):
                listLinks.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3828&filter_orderby=latest&filter_tax=3722");
                listLinks.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3861&filter_orderby=latest&filter_tax=3722");
                listLinks.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3840&filter_orderby=latest&filter_tax=3722");
                listLinks.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3829&filter_orderby=latest&filter_tax=3722");
                return listLinks;
            case ("Phụ kiện thay thế"):
                listLinks.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3828&filter_orderby=latest&filter_tax=3721");
                listLinks.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3861&filter_orderby=latest&filter_tax=3721");
                listLinks.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3829&filter_orderby=latest&filter_tax=3721");
                listLinks.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3840&filter_orderby=latest&filter_tax=3721");
                return listLinks;
            case ("Phụ kiện ốp"):
                listLinks.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3828&filter_orderby=latest&filter_tax=3720");
                listLinks.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3861&filter_orderby=latest&filter_tax=3720");
                listLinks.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3829&filter_orderby=latest&filter_tax=3720");
                listLinks.add("https://hondaxemay.com.vn/phukien/wp-admin/admin-ajax.php?action=action_get_html_accessories_tu_12_2016&security=c9c004c5be&type_car=3840&filter_orderby=latest&filter_tax=3720");
                return listLinks;
            default:
                return listLinks;
        }
    }

}
