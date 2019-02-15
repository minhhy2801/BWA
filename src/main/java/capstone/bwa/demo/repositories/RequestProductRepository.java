package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.RequestProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestProductRepository extends JpaRepository<RequestProductEntity, Integer> {
}
