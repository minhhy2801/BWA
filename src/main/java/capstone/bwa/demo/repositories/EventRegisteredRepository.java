package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.entities.EventRegisteredEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Repository
public interface EventRegisteredRepository extends JpaRepository<EventRegisteredEntity, Integer> {
    EventRegisteredEntity findById(int id);

    List<EventRegisteredEntity> findAllByEventIdOrderByIdDesc(int id);

    List<EventRegisteredEntity> findAllByEventId(int id);

    List<EventRegisteredEntity> findAllByAccountByRegisteredId_Id(int id);

    EventRegisteredEntity findDistinctFirstByAccountByRegisteredId_IdAndEventByEventId_Id(int accountId, int eventId);

    @Query("SELECT e.id FROM EventRegisteredEntity e WHERE e.status = :status AND e.eventId = :eventId")
    List<Integer> findAllIdByEventId(String status, int eventId);

    boolean existsDistinctByEventIdAndRegisteredId(int eventId, int userId);

    boolean existsByTicketCode(String code);
}
