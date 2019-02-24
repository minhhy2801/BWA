package capstone.bwa.demo.controllers;

import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.repositories.RoleRepository;
import capstone.bwa.demo.services.SmsSender;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;
    private final String accountStatus = "ACTIVE";
    private final String roleUser = "USER";
    private final String roleAdmin = "ADMIN";
    private String phone;

    @PostMapping("send_verify_code")
    public ResponseEntity sendCode(@RequestBody Map<String, String> body) throws IOException {
        String phone = body.get("phone").trim();
        //create random number verify code
        String code = new Random().nextInt(9999 - 1000) + 1000 + "";
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
        String hashVerifyCode = bCrypt.encode(code);
//        System.out.println(code);
//        System.out.println(hashVerifyCode);
        //check Phone in db
        if (accountRepository.findByPhone(phone) != null)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        Map<String, Object> result = sendSMSAPI(code, hashVerifyCode, phone);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @PostMapping("send_verify_code_forgot")
    public ResponseEntity sendForgotCode(@RequestBody Map<String, String> body) throws IOException {
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
        String phone = body.get("phone").trim();
        //create random number verify code
        String code = new Random().nextInt(9999 - 1000) + 1000 + "";
        String hashVerifyCode = bCrypt.encode(code);
        //send SMS API
        if (accountRepository.findByPhone(phone) != null) {
            Map<String, Object> result = sendSMSAPI(code, hashVerifyCode, phone);
            return new ResponseEntity(result, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("forgot_password")
    public ResponseEntity sendForgotPassword(@RequestBody Map<String, String> body) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String phone = body.get("phone").trim();
        String newPassword = body.get("newPassword");

        AccountEntity accountEntity = accountRepository.findByPhone(phone);
//        System.out.println(accountEntity);

        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.NO_CONTENT);
        if (accountEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (!accountEntity.getStatus().equals(accountStatus)) return new ResponseEntity(HttpStatus.LOCKED);

        accountEntity.setPassword(bCryptPasswordEncoder.encode(newPassword));
        accountRepository.save(accountEntity);
        return new ResponseEntity(HttpStatus.OK);
    }


    @PostMapping("sign_up")
    public ResponseEntity signUpAccount(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        String name = body.get("name");
        String password = body.get("password");

        AccountEntity accountEntity = new AccountEntity();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(); //bcrypt pass
        if (accountRepository.findByPhone(phone) != null) return new ResponseEntity(HttpStatus.BAD_REQUEST);

        Date today = new Date(System.currentTimeMillis());
        accountEntity.setName(name);
        accountEntity.setCreatedTime(today.toString());
        accountEntity.setPhone(phone);
        accountEntity.setRoleId(1);
        accountEntity.setPassword(bCryptPasswordEncoder.encode(password));
        accountEntity.setStatus(accountStatus);
        accountEntity.setRate("0");
        accountRepository.saveAndFlush(accountEntity);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    //view user profile
    @JsonView(View.IAccountProfile.class)
    @GetMapping("user/{id}/profile")
    @PostAuthorize("returnObject.body.phone == authentication.name")
    public ResponseEntity getProfile(@PathVariable int id) {
        AccountEntity accountEntity = accountRepository.findById(id);
        if (accountEntity == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (!accountEntity.getStatus().equals(accountStatus)) return new ResponseEntity(HttpStatus.LOCKED);

        return new ResponseEntity(accountEntity, HttpStatus.OK);
    }

    @JsonView(View.IAccountProfile.class)
    @PutMapping("user/{id}/profile")
    public ResponseEntity editProfile(@RequestBody Map<String, String> body, @PathVariable int id) {
        AccountEntity accountEntity = accountRepository.findById(id);

        if (!accountEntity.getStatus().equals(accountStatus)) return new ResponseEntity(HttpStatus.LOCKED);
        if (accountEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.NO_CONTENT);

        Date date = new Date(System.currentTimeMillis());
        String name = body.get("name");
        String gender = body.get("gender");
        String address = body.get("address");
        String avatarUrl = body.get("avatarUrl");

        accountEntity.setName(name);
        accountEntity.setGender(gender);
        accountEntity.setAddress(address);
        accountEntity.setEditedTime(date.toString());
        accountEntity.setAvatarUrl(avatarUrl);

        accountRepository.save(accountEntity);

        return new ResponseEntity(accountEntity, HttpStatus.OK);
    }

    @JsonView(View.IAccountProfile.class)
    @PutMapping("admin/{adminId}/user/{id}")
    public ResponseEntity changeStatusByAdmin(@RequestBody Map<String, String> body, @PathVariable int id,
                                              @PathVariable int adminId) {
        AccountEntity accountAdminEntity = accountRepository.findById(adminId);
        AccountEntity accountUserEntity = accountRepository.findById(id);

        if (!accountAdminEntity.getRoleByRoleId().getName().equals(roleAdmin))
            return new ResponseEntity(HttpStatus.LOCKED);

        if (accountAdminEntity == null || accountUserEntity == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.NO_CONTENT);
        Date today = new Date(System.currentTimeMillis());
        String status = body.get("status");
        accountUserEntity.setEditedTime(today.toString());
        accountUserEntity.setStatus(status);
        accountRepository.save(accountUserEntity);
        return new ResponseEntity(HttpStatus.OK);
    }

    @JsonView(View.IAccountProfile.class)
    @PutMapping("user/{id}/change_pass")
    public ResponseEntity editPasswordByUser(@RequestBody Map<String, String> body, @PathVariable int id) {
        AccountEntity accountEntity = accountRepository.findById(id);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        if (accountEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (!accountEntity.getStatus().equals(accountStatus)) return new ResponseEntity(HttpStatus.LOCKED);
        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.NO_CONTENT);

        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        if (!bCryptPasswordEncoder.matches(oldPassword, accountEntity.getPassword()))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        accountEntity.setPassword(bCryptPasswordEncoder.encode(newPassword));
        accountRepository.save(accountEntity);
        return new ResponseEntity(HttpStatus.OK);
    }

    private Map<String, Object> sendSMSAPI(String code, String hashVerifyCode, String phone) throws IOException {
        SmsSender sender = new SmsSender("ywyaveS4F2ghLWONdhFX9izGa5qfPM2S");   //add accessToken
        String sms = sender.sendSmsToCreateAccount(phone, "Verify Code BWA: " + code, 2, "BWA");

        Map<String, Object> result = new HashMap<>();
        JSONObject jsonObj = new JSONObject(sms);
        result.put("verify", hashVerifyCode);
        result.put("result", jsonObj.toMap());
        return result;
    }
}
