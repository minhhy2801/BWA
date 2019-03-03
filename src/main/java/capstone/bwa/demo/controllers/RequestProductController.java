package capstone.bwa.demo.controllers;


import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.repositories.RequestProductRepository;
import capstone.bwa.demo.repositories.SupplyProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RequestProductController {
    @Autowired
    private RequestProductRepository requestProductRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SupplyProductRepository supplyProductRepository;

    @PostMapping("user/{id}/request_bike")
    public ResponseEntity createRequestBike(@PathVariable int id, @RequestBody Map<String, String> body) {

        return null;
    }


}
