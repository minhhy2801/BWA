package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Integer> {
}
