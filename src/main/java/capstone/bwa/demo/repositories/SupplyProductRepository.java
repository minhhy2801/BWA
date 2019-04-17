package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.SupplyProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupplyProductRepository extends JpaRepository<SupplyProductEntity, Integer> {
    SupplyProductEntity findById(int id);

    SupplyProductEntity findByItemId(int id);

    List<SupplyProductEntity> findAllByStatusInOrderByIdDesc(List<String> status, Pageable pageable);

    List<SupplyProductEntity> findTop200ByStatusInOrderByIdDesc(List<String> status);

    List<SupplyProductEntity> findAllByStatusOrderByIdDesc(String status, Pageable pageable);

    List<SupplyProductEntity> findAllByStatusOrderByIdDesc(String status);

    List<SupplyProductEntity> findAllByOrderByIdDesc();

    List<SupplyProductEntity> findAllByCategoryIdAndStatusInOrderByIdDesc(int id, Pageable pageable, List<String> status);

    List<SupplyProductEntity> findAllByStatusAndCategoryIdOrderByIdDesc(String status, int id, Pageable pageable);

    List<SupplyProductEntity> findAllByCreatorIdOrderByIdDesc(int id);

    List<SupplyProductEntity> findAllByStatusInAndTitleContainingIgnoreCase(List<String> status, String value);

    @Query("SELECT e.id FROM SupplyProductEntity e WHERE e.status = :status AND e.creatorId = :id")
    List<Integer> findAllIdsByStatusAnAndCreatorId(@Param("status") String status, @Param("id") int id);

    int countAllByCreatorIdAndStatusIn(int id, List<String> status);

    int countAllByCreatorId(int id);

    int countAllByStatusIn(List<String> status);

    int countAllByStatus(String status);

    int countAllByCategoryId(int id);

    int countAllByCategoryIdAndStatus(int id, String status);


}
