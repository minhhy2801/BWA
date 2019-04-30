package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.TransactionDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface TransactionDetailRepository extends JpaRepository<TransactionDetailEntity, Integer> {
    TransactionDetailEntity findById(int id);

    TransactionDetailEntity findBySupplyProductIdAndStatus(int id, String status);

    List<TransactionDetailEntity> findAllBySupplyProductIdAndStatus(int id, String status);

    List<TransactionDetailEntity> findAllBySupplyProductId(int supProId);

    List<TransactionDetailEntity> findAllByInteractiveId(int id);

    @Query("SELECT e.id FROM TransactionDetailEntity e WHERE e.status = :status AND e.supplyProductId IN :id")
    List<Integer> findAllIdsByStatusAnAndSupplyPostIdIn(@Param("status") String status, @Param("id") List<Integer> id);

    boolean existsByInteractiveIdAndSupplyProductId(int userId, int supId);

    boolean existsDistinctBySupplyProductId(int id);
}
