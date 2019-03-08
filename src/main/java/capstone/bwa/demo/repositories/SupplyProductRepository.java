package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.entities.SupplyProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupplyProductRepository extends JpaRepository<SupplyProductEntity, Integer> {
    SupplyProductEntity findById(int id);

    @Query("SELECT e.id FROM SupplyProductEntity e WHERE e.status = :status AND e.creatorId = :id")
    List<Integer> findAllIdsByStatusAnAndCreatorId(@Param("status") String status, @Param("id") int id);

    List<SupplyProductEntity> findAllByStatusInOrderByIdDesc(List<String> status, Pageable pageable);

    List<SupplyProductEntity> findAllByStatusOrderByIdDesc(String status, Pageable pageable);

    List<SupplyProductEntity> findAllByOrderByIdDesc(Pageable pageable);

    List<SupplyProductEntity> findAllByCreatorIdOrderByIdDesc(int id);

    int countAllByCreatorIdAndStatusIn(int id, List<String> status);
}
