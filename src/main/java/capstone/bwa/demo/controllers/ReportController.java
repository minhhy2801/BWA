package capstone.bwa.demo.controllers;


import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.ReportEntity;
import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.repositories.ReportRepository;
import capstone.bwa.demo.utils.DateTimeUtils;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReportController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ReportRepository reportRepository;

    @JsonView(View.IReport.class)
    @PostMapping("user/{userId}/report")
    public ResponseEntity createReport(@PathVariable int userId,
                                       @RequestBody Map<String, String> body) {


        AccountEntity accountEntity = accountRepository.findById(userId);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        String reason = body.get("desc");
        String accusedId = body.get("accusedId");
        Map<String, String> desc = new HashMap<>();
        desc.put("desc", reason);

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setCreatorId(userId);
        reportEntity.setCreatedTime(DateTimeUtils.getCurrentTime());
        reportEntity.setStatus(MainConstants.PENDING);
        reportEntity.setAccusedId(Integer.parseInt(accusedId));
        reportEntity.setReason(new Gson().toJson(desc));

        reportRepository.save(reportEntity);

        return new ResponseEntity(HttpStatus.OK);
    }

    @JsonView(View.IReport.class)
    @GetMapping("user/{userId}/reports")
    public ResponseEntity getReportsByUser(@PathVariable int userId) {
        AccountEntity accountEntity = accountRepository.findById(userId);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_USER))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<ReportEntity> reports = reportRepository.findAllByCreatorId(userId);

        return ResponseEntity.ok(reports);
    }

    @JsonView(View.IReport.class)
    @PutMapping("admin/{adminId}/report/{reportId}")
    public ResponseEntity updateStatusReport(@PathVariable int adminId, @PathVariable int reportId,
                                             @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(adminId);
        ReportEntity reportEntity = reportRepository.findById(reportId);

        if (accountEntity == null || reportEntity == null
                || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        String status = body.get("status");
        String reply = body.get("reply");
        String desc = new JSONObject(reportEntity.getReason()).getString("desc");

        Map<String, String> map = new HashMap<>();
        map.put("desc", desc);
        map.put("adminId", adminId + "");
        map.put("reply", reply);
        reportEntity.setReason(new Gson().toJson(map));
        reportEntity.setStatus(status);
        reportEntity.setEditedTime(DateTimeUtils.getCurrentTime());

        reportRepository.save(reportEntity);

        return new ResponseEntity(reportEntity, HttpStatus.OK);
    }


    @JsonView(View.IReport.class)
    @GetMapping("admin/{adminId}/reports")
    public ResponseEntity getAllReports(@PathVariable int adminId) {
        AccountEntity accountEntity = accountRepository.findById(adminId);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE) ||
                !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<ReportEntity> reports = reportRepository.findAll();

        return ResponseEntity.ok(reports);
    }

    @JsonView(View.IReport.class)
    @GetMapping("report/{id}")
    public ResponseEntity getAReport(@PathVariable int id) {
        ReportEntity reportEntity = reportRepository.findById(id);
        if (reportEntity == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Map<String, Object> map = new HashMap<>();
        map.put("report", reportEntity);
        map.put("reason", new JSONObject(reportEntity.getReason()).toMap());
        return ResponseEntity.ok(map);
    }

}
