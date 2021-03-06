package capstone.bwa.demo.controllers;


import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.SupplyProductEntity;
import capstone.bwa.demo.entities.TransactionDetailEntity;
import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.repositories.SupplyProductRepository;
import capstone.bwa.demo.repositories.TransactionDetailRepository;
import capstone.bwa.demo.utils.DateTimeUtils;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*******************************************************************************
 * ::STATUS::
 * GOING
 * FINISHED
 *******************************************************************************/
@RestController
public class TransactionDetailController {

    @Autowired
    private SupplyProductRepository supplyProductRepository;
    @Autowired
    private TransactionDetailRepository transactionDetailRepository;
    @Autowired
    private AccountRepository accountRepository;

    @JsonView(View.ITransactionDetail.class)
    @GetMapping("user/{userId}/trans/{id}")
    public ResponseEntity getATransOfUser(@PathVariable int userId, @PathVariable int id) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        TransactionDetailEntity transactionDetailEntity = transactionDetailRepository.findById(id);

        if (accountEntity == null || transactionDetailEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (transactionDetailEntity.getInteractiveId().equals(userId))
            return new ResponseEntity(transactionDetailEntity, HttpStatus.OK);

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


    @JsonView(View.ITransactionDetail.class)
    @PostMapping("supply_post/{supProId}/trans")
    public ResponseEntity getSuccessTrans(@PathVariable int supProId) {
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(supProId);

        if (supplyProductEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_CLOSED)) {
            TransactionDetailEntity transactionDetailEntity =
                    transactionDetailRepository.findBySupplyProductIdAndStatus(supProId, MainConstants.TRANSACTION_SUCCESS);

            if (transactionDetailEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

            return new ResponseEntity(transactionDetailEntity, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @JsonView(View.ITransactions.class)
    @PostMapping("user/{userId}/supply_post/{supProId}/trans")
    public ResponseEntity getListTransByOwnId(@PathVariable int userId, @PathVariable int supProId) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(supProId);
        List<TransactionDetailEntity> list;

        if (supplyProductEntity == null || accountEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (supplyProductEntity.getCreatorId().equals(userId)) {
            list = transactionDetailRepository.findAllBySupplyProductId(supProId);
            if (list.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);
            return new ResponseEntity(list, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("user/{userId}/supply_post/{supProId}/transaction")
    public ResponseEntity createTrans(@PathVariable int userId, @PathVariable int supProId) {
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(supProId);
        AccountEntity accountEntity = accountRepository.findById(userId);

        if (supplyProductEntity == null || accountEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (transactionDetailRepository.existsByInteractiveIdAndSupplyProductId(userId, supProId))
            return new ResponseEntity(HttpStatus.LOCKED);

        if (accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                && !supplyProductEntity.getCreatorId().equals(accountEntity.getId())
                && supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_PUBLIC)) {
            TransactionDetailEntity transactionDetailEntity = new TransactionDetailEntity();

            transactionDetailEntity.setInteractiveId(userId);
            transactionDetailEntity.setStatus(MainConstants.PENDING);
            transactionDetailEntity.setSupplyProductId(supProId);
            transactionDetailEntity.setCreatedTime(DateTimeUtils.getCurrentTime());
            transactionDetailRepository.save(transactionDetailEntity);

            return new ResponseEntity(transactionDetailEntity, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


    @GetMapping("user/{userId}/supply_post/{supProId}/transaction/{id}/edit")
    public ResponseEntity openTransAfterFrozen(@PathVariable int userId, @PathVariable int supProId, @PathVariable int id) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(supProId);
        TransactionDetailEntity transactionDetailEntity = transactionDetailRepository.findById(id);

        if (!accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                || accountEntity == null || supplyProductEntity == null || transactionDetailEntity == null
                || !accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_USER)
                || !supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_PUBLIC))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

//        System.out.println(transactionDetailEntity.getInteractiveId() + " " + transactionDetailEntity.getSupplyProductId()
//                + " " + transactionDetailEntity.getStatus());

        if (transactionDetailEntity.getInteractiveId().equals(userId) &&
                transactionDetailEntity.getSupplyProductId().equals(supProId) &&
                transactionDetailEntity.getStatus().equals(MainConstants.TRANSACTION_FROZEN)) {

            transactionDetailEntity.setEditedTime(DateTimeUtils.getCurrentTime());
            transactionDetailEntity.setStatus(MainConstants.PENDING);
            transactionDetailRepository.save(transactionDetailEntity);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


    @JsonView(View.ITransactionDetail.class)
    @PutMapping("user/{userId}/supply_post/{supProId}/trans/{id}")
    public ResponseEntity changeStatusSuccessTransaction(@PathVariable int userId, @PathVariable int supProId,
                                                         @PathVariable int id, @RequestBody Map<String, String> body) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(supProId);
        TransactionDetailEntity transactionDetailEntity = transactionDetailRepository.findById(id);
        String status = body.get("status");

        if (supplyProductEntity == null || accountEntity == null || transactionDetailEntity == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (supplyProductEntity.getCreatorId().equals(userId) && supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_PUBLIC)) {
            transactionDetailEntity.setEditedTime(DateTimeUtils.getCurrentTime());
            transactionDetailEntity.setStatus(status);
            transactionDetailRepository.save(transactionDetailEntity);
            return new ResponseEntity(transactionDetailEntity, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @JsonView(View.ITransactions.class)
    @PutMapping("user/{userId}/close_supply_post/{supProId}/trans")
    public ResponseEntity closeSupplyPostTransactions(@PathVariable int userId, @PathVariable int supProId) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(supProId);

        if (supplyProductEntity == null || accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (!supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_PUBLIC))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        if (supplyProductEntity.getCreatorId().equals(userId) || accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN)) {

            supplyProductEntity.setStatus(MainConstants.SUPPLY_POST_CLOSED);
            supplyProductEntity.setClosedTime(DateTimeUtils.getCurrentTime());
            supplyProductRepository.saveAndFlush(supplyProductEntity);

            List<TransactionDetailEntity> list =
                    transactionDetailRepository.findAllBySupplyProductIdAndStatus(supProId, MainConstants.PENDING);

            List<TransactionDetailEntity> transactionDetailEntities = new ArrayList<>();

            if (list.size() > 0) {
                for (TransactionDetailEntity item : list) {
                    item.setStatus(MainConstants.TRANSACTION_FAIL);
                    item.setEditedTime(DateTimeUtils.getCurrentTime());
                    transactionDetailEntities.add(item);
                }

                transactionDetailRepository.saveAll(transactionDetailEntities);
            }
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @JsonView(View.ITransactions.class)
    @GetMapping("user/{id}/list_trans")
    public ResponseEntity getListTransOfUser(@PathVariable int id) {
        AccountEntity accountEntity = accountRepository.findById(id);

        if (accountEntity == null || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        List<TransactionDetailEntity> transactionDetailEntities = transactionDetailRepository.findAllByInteractiveId(id);

        if (transactionDetailEntities.size() < 1) return new ResponseEntity(HttpStatus.NO_CONTENT);
        return new ResponseEntity(transactionDetailEntities, HttpStatus.OK);
    }


}
