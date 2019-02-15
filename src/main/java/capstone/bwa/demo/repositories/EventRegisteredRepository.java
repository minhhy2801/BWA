package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.EventRegisteredEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRegisteredRepository extends JpaRepository<EventRegisteredEntity, Integer> {
}
