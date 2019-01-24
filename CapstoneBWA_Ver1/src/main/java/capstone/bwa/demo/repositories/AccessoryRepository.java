package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.AccessoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessoryRepository extends JpaRepository<AccessoryEntity, Integer> {
}
