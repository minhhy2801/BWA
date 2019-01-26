package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.BikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BikeRepository extends JpaRepository<BikeEntity, Integer> {
}
