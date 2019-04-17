package capstone.bwa.demo.controllers;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.entities.SupplyProductEntity;
import capstone.bwa.demo.exceptions.CustomException;
import capstone.bwa.demo.repositories.*;
import capstone.bwa.demo.services.SmsSender;
import capstone.bwa.demo.utils.DateTimeUtils;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SupplyProductRepository supplyProductRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private NewsRepository newsRepository;

    @PostMapping("send_verify_code")
    public ResponseEntity sendSignUpCode(@RequestBody Map<String, String> body) {
        String phone = body.get("phone").trim();
        //create random number verify code
        String code = new Random().nextInt(9999 - 1000) + 1000 + "";

        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
        String hashVerifyCode = bCrypt.encode(code);

        if (accountRepository.findByPhone(phone) != null) return new ResponseEntity(HttpStatus.BAD_REQUEST);

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

        if (accountRepository.findByPhone(phone) != null) return new ResponseEntity(HttpStatus.BAD_REQUEST);

        AccountEntity accountEntity = paramAccountEntityCreateRequest(name, phone, password);
        accountEntity.setRoleId(1);
        accountRepository.save(accountEntity);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    //view user profile
    @JsonView(View.IAccountProfile.class)
    @GetMapping("user/{id}/profile")
//    @PostAuthorize("returnObject.body.phone == authentication.name")
    public ResponseEntity getProfile(@PathVariable int id) {
        AccountEntity accountEntity = accountRepository.findById(id);
        if (accountEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        // if (!accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
        //     return new ResponseEntity(HttpStatus.LOCKED);

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

        String name = body.get("name");
        String gender = body.get("gender");
        String address = body.get("address");
        String avatarUrl = body.get("avatarUrl");

        accountEntity.setName(name);
        accountEntity.setGender(gender);
        accountEntity.setAddress(address);
        accountEntity.setEditedTime(DateTimeUtils.getCurrentTime());
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

        if (accountAdminEntity == null || accountUserEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (body.isEmpty() || body == null) return new ResponseEntity(HttpStatus.NO_CONTENT);

        String status = body.get("status");
        accountUserEntity.setEditedTime(DateTimeUtils.getCurrentTime());
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

    @GetMapping("user/profile/{id}")
    public ResponseEntity getViewProfile(@PathVariable int id) {
        AccountEntity accountEntity = accountRepository.findById(id);
        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        //compute rate
        if (accountEntity.getRoleByRoleId().getName().equalsIgnoreCase(MainConstants.ROLE_USER))
            calculateAndUpdateRatingPoint(accountEntity);

        List<String> status = new ArrayList<>();
        status.add(MainConstants.SUPPLY_POST_CLOSED);
        status.add(MainConstants.SUPPLY_POST_PUBLIC);

        Map<String, String> map = new HashMap<>();
        map.put("id", accountEntity.getId() + "");
        map.put("name", accountEntity.getName());
        map.put("url", accountEntity.getAvatarUrl());
        map.put("createdTime", accountEntity.getCreatedTime());
        map.put("rate", accountEntity.getRate());
        map.put("counterSupply", supplyProductRepository.countAllByCreatorIdAndStatusIn(id, status) + "");
        status.clear();
        status.add(MainConstants.EVENT_ONGOING);
        status.add(MainConstants.EVENT_FINISHED);
        status.add(MainConstants.EVENT_CLOSED);
        map.put("counterEvent", eventRepository.countAllByCreatorIdAndStatusIn(id, status) + "");

        return new ResponseEntity(map, HttpStatus.OK);
    }

    @JsonView(View.IAccounts.class)
    @GetMapping("admin/{id}/accounts/page/{pageId}/limit/{quantity}")
    public ResponseEntity getAccounts(@PathVariable int id, @PathVariable int pageId, @PathVariable int quantity) {
        Pageable pageWithElements = PageRequest.of(pageId, quantity);

        AccountEntity accountEntity = accountRepository.findById(id);
        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !accountEntity.getRoleByRoleId().getName().equalsIgnoreCase(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<AccountEntity> accounts = accountRepository.findAllByOrderByIdDesc(pageWithElements);
        List<Object> objects = new ArrayList<>();
        for (AccountEntity e : accounts) {
            Map<String, Object> map = new HashMap<>();
            map.put("account", e);
            map.put("countComts", commentRepository.countAllByCreatorId(e.getId()));
            map.put("countPosts", eventRepository.countAllByCreatorId(e.getId())
                    + supplyProductRepository.countAllByCreatorId(e.getId())
                    + newsRepository.countAllByCreatorId(e.getId()));
            objects.add(map);
        }
        if (objects.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(objects, HttpStatus.OK);
    }


    @JsonView(View.IAccountProfile.class)
    @PostMapping("admin/{id}/new_admin")
    public ResponseEntity createAccount(@PathVariable int id,
                                        @RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        String name = body.get("name");
        String password = body.get("password");
        AccountEntity accountEntity = accountRepository.findById(id);
        if (accountRepository.findByPhone(phone) != null
                || !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN)
                || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        AccountEntity entity = paramAccountEntityCreateRequest(name, phone, password);
        entity.setRoleId(2);
        accountRepository.save(entity);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    private void calculateAndUpdateRatingPoint(AccountEntity account) {
        //compute event rate
        List<EventEntity> events = account.getEventsById().stream()
                .filter(e -> e.getStatus().equalsIgnoreCase(MainConstants.EVENT_FINISHED))
                .collect(Collectors.toList());
        double eventTotalRatePointFinished = events.stream().map(e -> Double.parseDouble(e.getTotalRate()))
                .mapToDouble(Double::doubleValue).sum();
        long totalEventFinished = events.stream().count();
        double eventRate = totalEventFinished == 0 ? 0 : eventTotalRatePointFinished / totalEventFinished;

        //compute supply post rate
        List<SupplyProductEntity> products = account.getSupplyProductsById().stream()
                .filter(p -> p.getStatus().equalsIgnoreCase(MainConstants.SUPPLY_POST_CLOSED))
                .collect(Collectors.toList());
        double closedProduct = products.stream().map(p -> Double.parseDouble(p.getRate()))
                .mapToDouble(Double::doubleValue).sum();

        double totalSupplyPostClosed = products.stream().count();
        double supplyPostRate = totalSupplyPostClosed == 0 ? 0 : closedProduct / totalSupplyPostClosed;

        //compute rate
        double rate = (eventRate + supplyPostRate) / 2;

        account.setRate(rate + "");

        accountRepository.save(account);
    }


    private AccountEntity paramAccountEntityCreateRequest(String name, String phone, String password) {
        AccountEntity accountEntity = new AccountEntity();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(); //bcrypt pass

        accountEntity.setName(name);
        accountEntity.setCreatedTime(DateTimeUtils.getCurrentTime());
        accountEntity.setPhone(phone);
        accountEntity.setPassword(bCryptPasswordEncoder.encode(password));
        accountEntity.setStatus(MainConstants.ACCOUNT_ACTIVE);
        accountEntity.setRate("0");
        return accountEntity;
    }


    private Map<String, Object> sendSMSAPI(String code, String hashVerifyCode, String phone) {
        SmsSender sender = new SmsSender("ij1ZWqR2ai2PeTPhJ3UWxlPa027caHCs");   //add accessToken
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
