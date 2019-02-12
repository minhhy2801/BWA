package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.EventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Integer>{
    EventEntity findById(int id);
    List<EventEntity> findAllByStatus(String status, Pageable pageable);
//    List<EventEntity> findTopByStatusAndOrderByIdDesc(int limit ,String status);
}

