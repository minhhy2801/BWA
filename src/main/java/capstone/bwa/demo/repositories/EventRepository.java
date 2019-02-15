package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.EventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Integer> {
    EventEntity findById(int id);

    List<EventEntity> findAllByStatusOrderByIdDesc(String status, Pageable pageable);

    List<EventEntity> findAllByCreatorIdAndStatusOrderByIdDesc(int creatorId, Pageable pageable, String status);
    List<EventEntity> findAllByCreatorIdOrderByIdDesc(int creatorId, Pageable pageable);
}

