package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.SupplyProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplyProductRepository extends JpaRepository<SupplyProductEntity, Integer> {
}
