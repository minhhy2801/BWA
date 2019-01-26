package capstone.bwa.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AccountController {

    @PostMapping("sign_up")
    public ResponseEntity signUpAccount(@RequestBody Map<String, String> body){
        
        return null;
    }



}
