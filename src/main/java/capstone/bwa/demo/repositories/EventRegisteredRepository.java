package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.entities.EventRegisteredEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRegisteredRepository extends JpaRepository<EventRegisteredEntity, Integer> {
    EventRegisteredEntity findById(int id);

    List<EventRegisteredEntity> findAllByEventByEventId(EventEntity eventEntity);

    @Query("SELECT e.id FROM EventRegisteredEntity e WHERE e.status = :status AND e.eventId = :eventId")
    List<Integer> findAllIdByEventId(String status, int eventId);

//    boolean existsDistinctByAccountByRegisteredId_IdAndEventByEventId_Id(int accountId, int eventId);
    EventRegisteredEntity findDistinctFirstByAccountByRegisteredId_IdAndEventByEventId_Id(int accountId, int eventId);
}
