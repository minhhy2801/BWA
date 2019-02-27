package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.entities.SupplyProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplyProductRepository extends JpaRepository<SupplyProductEntity, Integer> {
    SupplyProductEntity findById(int id);

    List<SupplyProductEntity> findAllByStatusInOrderByIdDesc(List<String> status, Pageable pageable);

    List<SupplyProductEntity> findAllByStatusOrderByIdDesc(String status, Pageable pageable);


}
