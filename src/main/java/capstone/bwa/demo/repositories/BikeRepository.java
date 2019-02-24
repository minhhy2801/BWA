package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.BikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikeRepository extends JpaRepository<BikeEntity,Integer> {
    BikeEntity findById(int id);

    BikeEntity findByVersion(String version);

    BikeEntity findByHashBikeCode(String hashcode);

    boolean existsByHashBikeCode(String hashCode);
}
