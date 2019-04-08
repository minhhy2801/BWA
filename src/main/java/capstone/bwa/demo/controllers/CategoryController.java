package capstone.bwa.demo.controllers;


import capstone.bwa.demo.entities.CategoryEntity;
import capstone.bwa.demo.repositories.CategoryRepository;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Return categories by type
     *
     * @param body (type)
     * @return 404 if not found
     */

    @JsonView(View.ICategories.class)
    @PostMapping("categories/type")
    public ResponseEntity getListCategoriesByType(@RequestBody Map<String, String> body) {
        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        String type = body.get("type");
        String status = body.get("status");

        List<CategoryEntity> list = categoryRepository.findAllByTypeAndStatus(type, status);
        if (list.size() < 1)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        return new ResponseEntity(list, HttpStatus.OK);
    }

}

