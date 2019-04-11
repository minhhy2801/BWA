package capstone.bwa.demo.controllers;

import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.*;
import capstone.bwa.demo.repositories.*;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class DashboardController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SupplyProductRepository supplyProductRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private ReportRepository reportRepository;

    @JsonView(View.IAccountProfile.class)
    @GetMapping("admin/{adminId}/dashboard/top-account/{top}")
    public ResponseEntity getTopUserByHighestUserRate(@PathVariable int adminId, @PathVariable int top) {

        AccountEntity accountEntity = accountRepository.findById(adminId);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !accountEntity.getRoleByRoleId().getName().equalsIgnoreCase(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (top < 1) return new ResponseEntity(HttpStatus.BAD_REQUEST);

        List<AccountEntity> accounts = accountRepository.findAllByStatusOrderByRateDesc(MainConstants.ACCOUNT_ACTIVE);

        if (accounts.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        accounts = accounts.subList(0, top); //get top n account in list

        return ResponseEntity.ok(accounts);
    }

    @JsonView(View.INews.class)
    @GetMapping("admin/{adminId}/dashboard/new_news/{top}")
    public ResponseEntity getNewsHasJustPosted(@PathVariable int adminId, @PathVariable int top) {

        AccountEntity accountEntity = accountRepository.findById(adminId);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !accountEntity.getRoleByRoleId().getName().equalsIgnoreCase(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (top < 1) return new ResponseEntity(HttpStatus.BAD_REQUEST);

        List<NewsEntity> news = newsRepository.findAllByStatusOrderByIdDesc(MainConstants.NEWS_PUBLIC);

        if (news.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        news = news.subList(0, top);

        return ResponseEntity.ok(news);
    }

    @JsonView(View.IEventsAdmin.class)
    @GetMapping("admin/{adminId}/dashboard/new_event/{top}")
    public ResponseEntity getEventHasJustPosted(@PathVariable int adminId, @PathVariable int top) {

        AccountEntity accountEntity = accountRepository.findById(adminId);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !accountEntity.getRoleByRoleId().getName().equalsIgnoreCase(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (top < 1) return new ResponseEntity(HttpStatus.BAD_REQUEST);

        List<EventEntity> events = eventRepository.findAllByStatusOrderByIdDesc(MainConstants.EVENT_ONGOING);

        if (events.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        events = events.subList(0, top);

        return ResponseEntity.ok(events);
    }

    @JsonView(View.IEventsAdmin.class)
    @GetMapping("admin/{adminId}/dashboard/events/approve")
    public ResponseEntity getEventNeedToApprove(@PathVariable int adminId) {

        AccountEntity accountEntity = accountRepository.findById(adminId);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !accountEntity.getRoleByRoleId().getName().equalsIgnoreCase(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<EventEntity> events = eventRepository.findAllByStatusOrderByIdDesc(MainConstants.PENDING);
        if (events.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return ResponseEntity.ok(events);
    }

    @JsonView(View.ISupplyPostsAdmin.class)
    @GetMapping("admin/{adminId}/dashboard/supply_posts/approve")
    public ResponseEntity getSupplyPostNeedToApprove(@PathVariable int adminId) {

        AccountEntity accountEntity = accountRepository.findById(adminId);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !accountEntity.getRoleByRoleId().getName().equalsIgnoreCase(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<SupplyProductEntity> supplyProductEntities = supplyProductRepository.findAllByStatusOrderByIdDesc(MainConstants.PENDING);

        if (supplyProductEntities.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return ResponseEntity.ok(supplyProductEntities);
    }


    @JsonView(View.IReport.class)
    @GetMapping("admin/{adminId}/dashboard/report/unreply")
    public ResponseEntity getReportNeedToReply(@PathVariable int adminId) {

        AccountEntity accountEntity = accountRepository.findById(adminId);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !accountEntity.getRoleByRoleId().getName().equalsIgnoreCase(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<ReportEntity> reports = reportRepository.findAllByStatus(MainConstants.PENDING);
        if (reports.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(reports);
    }


    @JsonView(View.IEventsAdmin.class)
    @GetMapping("admin/{adminId}/dashboard/top_events/{top}")
    public ResponseEntity getMuchTicketEvents(@PathVariable int adminId, @PathVariable int top) {

        AccountEntity accountEntity = accountRepository.findById(adminId);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !accountEntity.getRoleByRoleId().getName().equalsIgnoreCase(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (top < 1) return new ResponseEntity(HttpStatus.BAD_REQUEST);

        List<EventEntity> events = eventRepository.findAllByOrderByTotalSoldTicketDesc();

        if (events.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        events = events.subList(0, top);

        return ResponseEntity.ok(events);
    }

    @JsonView(View.INews.class)
    @GetMapping("admin/{adminId}/dashboard/top_news")
    public ResponseEntity getHighestComment(@PathVariable int adminId) {

        AccountEntity accountEntity = accountRepository.findById(adminId);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || !accountEntity.getRoleByRoleId().getName().equalsIgnoreCase(MainConstants.ROLE_ADMIN))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<Map<String, Object>> list = newsRepository.getTop5NewsHavingManyComments();
        if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return ResponseEntity.ok(list);
    }
}
