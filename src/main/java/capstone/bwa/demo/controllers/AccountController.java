package capstone.bwa.demo.controllers;

import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("sign_up/verificationCode")
    public ResponseEntity sendCode(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        String code = new Random().nextInt(9999 - 1000) + 1000 + "";
//        String code = SmsSender.sendSmsToCreateAccount(phone);
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("code", code);
        return new ResponseEntity(hashMap, HttpStatus.ACCEPTED);
    }

    @PostMapping("sign_up")
    public ResponseEntity signUpAccount(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        String password = body.get("password");
        String name = body.get("name");

        if (accountRepository.findByPhone(phone) != null)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        AccountEntity accountEntity = new AccountEntity();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        accountEntity.setName(name);
        accountEntity.setPhone(phone);
        accountEntity.setRoleId(1);
        accountEntity.setPassword(bCryptPasswordEncoder.encode(password));
        accountEntity.setStatus("ACTIVE");
        accountRepository.save(accountEntity);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @JsonView(View.IAccountProfile.class)
    @GetMapping("user/{id}/profile")
    public ResponseEntity getProfile(@PathVariable int id){
        AccountEntity accountEntity = accountRepository.findById(id);
        if (accountEntity == null)
            return  new ResponseEntity(HttpStatus.NOT_FOUND);
        return new ResponseEntity(accountEntity, HttpStatus.OK);
    }

}
