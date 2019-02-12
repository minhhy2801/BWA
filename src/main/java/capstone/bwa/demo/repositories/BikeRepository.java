package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.BikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikeRepository extends JpaRepository<BikeEntity,Integer> {
    BikeEntity findByHashBikeCode(String hashcode);
}
