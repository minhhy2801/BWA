package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.SupplyProductEntity;
import capstone.bwa.demo.entities.TransactionDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionDetailRepository extends JpaRepository<TransactionDetailEntity, Integer> {
    TransactionDetailEntity findById(int id);

    TransactionDetailEntity findBySupplyProductId(int id);

    boolean existsByInteractiveIdAndSupplyProductId(int userId, int supId);

    TransactionDetailEntity findBySupplyProductIdAndStatus(int id, String status);

    List<TransactionDetailEntity> findAllBySupplyProductIdAndStatus(int id, String status);

    List<TransactionDetailEntity> findAllBySupplyProductId(int supProId);
}
