package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.RequestProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface RequestProductRepository extends JpaRepository<RequestProductEntity, Integer> {
    RequestProductEntity findById(int id);

    List<RequestProductEntity> findAllByStatus(String status);

    List<RequestProductEntity> findAllByCreatorId(int creatorId);

    @Query("SELECT e.id FROM RequestProductEntity e WHERE e.status = :status AND e.creatorId = :createId")
    List<Integer> findAllIdsByCreatorIdAndStatus(String status, Integer createId);


}
