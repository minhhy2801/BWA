package capstone.bwa.demo.controllers;


import capstone.bwa.demo.crawlmodel.DBSetup;
import capstone.bwa.demo.entities.CategoryEntity;
import capstone.bwa.demo.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController("category")
public class CategoryController {
    @Autowired
    CategoryRepository categoryRepository;

    private final String statusActive = "ACTIVE";
    private final String typeBike = "BIKE";
    private final String typeAccessory = "ACCESSORY";

    @GetMapping("admin/categories")
    public ResponseEntity getAllCategory(){
        if (categoryRepository.findAll().isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        return new ResponseEntity(categoryRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping("admin/category/create")
    public ResponseEntity createCategory(@RequestBody Map<String,String> body){
        String name = body.get("name");
        if(categoryRepository.findByName(name) != null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        String type = body.get("type");
        String status = body.get("status");
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(name);
        categoryEntity.setType(type);
        categoryEntity.setStatus(status);
        categoryRepository.saveAndFlush(categoryEntity);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping("admin/category/{id}")
    public ResponseEntity updateCategory(@PathVariable int id, @RequestBody Map<String,String> body){
        String name = body.get("name");
        String type = body.get("type");
        String status = body.get("status");
        CategoryEntity categoryEntity = categoryRepository.findById(id);
        if (categoryEntity == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        categoryEntity.setName(name);
        categoryEntity.setType(type);
        categoryEntity.setStatus(status);
        categoryRepository.saveAndFlush(categoryEntity);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("admin/category/{id}")
    public  ResponseEntity deleteCategory(@PathVariable int id){
        CategoryEntity categoryEntity = categoryRepository.findById(id);
        if (categoryEntity != null) {
            categoryEntity.setStatus("InActive");
            categoryRepository.saveAndFlush(categoryEntity);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("admin/category/{id}")
    public ResponseEntity searchCategoryByID(@PathVariable int id){
        CategoryEntity categoryEntity = categoryRepository.findById(id);
        if (categoryEntity != null) {
            return new ResponseEntity(categoryEntity,HttpStatus.FOUND);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

}
