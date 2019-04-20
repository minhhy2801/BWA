package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.RequestNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequestNotificationRepository extends JpaRepository<RequestNotificationEntity, Integer> {

    @Query("select n from RequestNotificationEntity n join RequestProductEntity r on n.requestProductId = r.id where r.id = :requestProductId")
    List<RequestNotificationEntity> findAllByRequestProductId(@Param("requestProductId") int requestProductId);

    List<RequestNotificationEntity> findAllByRequestProductIdInAndStatus(List<Integer> ids, String status);

    List<RequestNotificationEntity> findAllByRequestProductIdAndStatus(List<Integer> ids, String status);

    List<RequestNotificationEntity> findAllByTypeAndStatusAndTransactionByTransactionId_InteractiveId(String type, String status, int id);

    RequestNotificationEntity findById(int id);
}
