package capstone.bwa.demo.controllers;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.exceptions.CustomException;
import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.services.SmsSender;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("send_verify_code")
    public ResponseEntity sendSignUpCode(@RequestBody Map<String, String> body) {
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
    public ResponseEntity sendForgotCode(@RequestBody Map<String, String> body) {
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
        if (!accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.LOCKED);

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

        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        accountEntity.setName(name);
        accountEntity.setCreatedTime(dateFormat.format(date));
        accountEntity.setPhone(phone);
        accountEntity.setRoleId(1);
        accountEntity.setPassword(bCryptPasswordEncoder.encode(password));
        accountEntity.setStatus(MainConstants.ACCOUNT_ACTIVE);
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
        if (!accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.LOCKED);

        return new ResponseEntity(accountEntity, HttpStatus.OK);
    }

    @JsonView(View.IAccountProfile.class)
    @PutMapping("user/{id}/profile")
    public ResponseEntity editProfile(@RequestBody Map<String, String> body, @PathVariable int id) {
        AccountEntity accountEntity = accountRepository.findById(id);

        if (!accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.LOCKED);
        if (accountEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.NO_CONTENT);

        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        String name = body.get("name");
        String gender = body.get("gender");
        String address = body.get("address");
        String avatarUrl = body.get("avatarUrl");

        accountEntity.setName(name);
        accountEntity.setGender(gender);
        accountEntity.setAddress(address);
        accountEntity.setEditedTime(dateFormat.format(date));
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

        if (!accountAdminEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.LOCKED);

        if (accountAdminEntity == null || accountUserEntity == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.NO_CONTENT);
        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        String status = body.get("status");
        accountUserEntity.setEditedTime(dateFormat.format(date));
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
        if (!accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.LOCKED);
        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.NO_CONTENT);

        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        if (!bCryptPasswordEncoder.matches(oldPassword, accountEntity.getPassword()))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        accountEntity.setPassword(bCryptPasswordEncoder.encode(newPassword));
        accountRepository.save(accountEntity);
        return new ResponseEntity(HttpStatus.OK);
    }

    private Map<String, Object> sendSMSAPI(String code, String hashVerifyCode, String phone) {
        SmsSender sender = new SmsSender("");   //add accessToken
        try {
            String sms = sender.sendSmsToCreateAccount(phone, "Verify Code BWA: " + code, 2, "BWA");
            Map<String, Object> result = new HashMap<>();
            JSONObject jsonObj = new JSONObject(sms);
            result.put("verify", hashVerifyCode);
            result.put("result", jsonObj.toMap());
            return result;
        } catch (IOException e) {
            throw new CustomException("Add accessToken", HttpStatus.BAD_REQUEST);
        }
    }
}
