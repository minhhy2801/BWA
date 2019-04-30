package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Integer> {
    FeedbackEntity findById(int id);

    FeedbackEntity findByTransactionDetailByOwnId_IdAndStatus(int id, String type);

    @Query("SELECT e FROM FeedbackEntity e WHERE e.ownId IN :eventRegisterIds AND e.status = :status")
    List<FeedbackEntity> findAllByEventRegisteredIdsIn(@Param("eventRegisterIds") List<Integer> list, String status);

    boolean existsByOwnIdAndStatus(int id, String status);
}
