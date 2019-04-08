package capstone.bwa.demo.controllers;


import capstone.bwa.demo.constants.MainConstants;
import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.SupplyProductEntity;
import capstone.bwa.demo.entities.TransactionDetailEntity;
import capstone.bwa.demo.repositories.AccountRepository;
import capstone.bwa.demo.repositories.SupplyProductRepository;
import capstone.bwa.demo.repositories.TransactionDetailRepository;
import capstone.bwa.demo.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    /**
     * Return a transaction of user
     *
     * @param userId
     * @param id
     * @return
     */
    @JsonView(View.ITransactionDetail.class)
    @GetMapping("user/{userId}/trans/{id}")
    public ResponseEntity getATransOfUser(@PathVariable int userId, @PathVariable int id) {
        AccountEntity accountEntity = accountRepository.findById(userId);
        TransactionDetailEntity transactionDetailEntity = transactionDetailRepository.findById(id);
        if (accountEntity == null || transactionDetailEntity == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (transactionDetailEntity.getInteractiveId().equals(userId))
            return new ResponseEntity(transactionDetailEntity, HttpStatus.OK);

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


    /**
     * Return a transaction of supply post (Guest view) after finish supply post
     *
     * @param supProId
     * @return
     */
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

    /**
     * Return list transactions of user who create a supply post view
     *
     * @param userId
     * @param supProId
     * @return
     */

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


    /**
     * Return new transaction when click exchange supply post view
     *
     * @param userId
     * @param supProId
     * @return
     */
    @PostMapping("user/{userId}/supply_post/{supProId}/transaction")
    public ResponseEntity createTrans(@PathVariable int userId, @PathVariable int supProId) {
        SupplyProductEntity supplyProductEntity = supplyProductRepository.findById(supProId);
        AccountEntity accountEntity = accountRepository.findById(userId);


        if (supplyProductEntity == null || accountEntity == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (transactionDetailRepository.existsByInteractiveIdAndSupplyProductId(userId, supProId))
            return new ResponseEntity(HttpStatus.LOCKED);

        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        if (accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE)
                && !supplyProductEntity.getCreatorId().equals(accountEntity.getId())
                && supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_PUBLIC)) {
            TransactionDetailEntity transactionDetailEntity = new TransactionDetailEntity();
            transactionDetailEntity.setInteractiveId(userId);
            transactionDetailEntity.setStatus(MainConstants.PENDING);
            transactionDetailEntity.setSupplyProductId(supProId);
            transactionDetailEntity.setCreatedTime(dateFormat.format(date));
            transactionDetailRepository.save(transactionDetailEntity);
            return new ResponseEntity(transactionDetailEntity, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


    /**
     * Return list transactions when click close supply post. Update all transactions, update status
     *
     * @param userId
     * @param supProId
     * @param body
     * @return
     */
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


        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        if (supplyProductEntity.getCreatorId().equals(userId) && supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_PUBLIC)) {
            transactionDetailEntity.setEditedTime(dateFormat.format(date));
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
        if (supplyProductEntity == null || accountEntity == null
                || !accountEntity.getStatus().equals(MainConstants.ACCOUNT_ACTIVE))
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        if (!supplyProductEntity.getStatus().equals(MainConstants.SUPPLY_POST_PUBLIC))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
//        System.out.println(supplyProductEntity.getCreatorId().equals(userId));
//        System.out.println(accountEntity.getRoleByRoleId().getName());
        if (supplyProductEntity.getCreatorId().equals(userId)
                || accountEntity.getRoleByRoleId().getName().equals(MainConstants.ROLE_ADMIN)) {
            supplyProductEntity.setStatus(MainConstants.SUPPLY_POST_CLOSED);
            supplyProductEntity.setClosedTime(dateFormat.format(date));
            supplyProductRepository.saveAndFlush(supplyProductEntity);

            List<TransactionDetailEntity> list =
                    transactionDetailRepository.findAllBySupplyProductIdAndStatus(supProId, MainConstants.PENDING);
            System.out.println(list.size());
            List<TransactionDetailEntity> transactionDetailEntities = new ArrayList<>();
            if (list.size() > 0) {
                for (TransactionDetailEntity item : list) {
                    item.setStatus(MainConstants.TRANSACTION_FAIL);
                    item.setEditedTime(dateFormat.format(date));
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
