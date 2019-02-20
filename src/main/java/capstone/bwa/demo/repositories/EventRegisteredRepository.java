package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.entities.EventRegisteredEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRegisteredRepository extends JpaRepository<EventRegisteredEntity, Integer> {
    List<EventRegisteredEntity> findAllByEventByEventId(EventEntity eventEntity);

}
