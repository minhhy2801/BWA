package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.RequestProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestProductRepository extends JpaRepository<RequestProductEntity, Integer> {
    RequestProductEntity findById(int id);

    List<RequestProductEntity> findAllByStatus(String status);

    List<RequestProductEntity> findAllByCreatorId(int creatorId);
}
