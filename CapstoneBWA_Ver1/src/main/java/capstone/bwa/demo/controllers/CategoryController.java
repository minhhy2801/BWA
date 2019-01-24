package capstone.bwa.demo.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CategoryController {


    /**
     * Return categories by type
     * @param type
     * @return 404 if not found
     */
    @GetMapping("categories/type")
    public ResponseEntity getListCategoriesByType(@RequestBody Map<String, String> type){
        return null;
    }
}
