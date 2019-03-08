package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.entities.EventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Integer> {
    EventEntity findById(int id);

    @Query("SELECT e.id, e.title FROM EventEntity e WHERE e.status IN :status")
    List<EventEntity> findAllTitle(@Param("status") List<String> status);

    @Query("SELECT e.id, e.publicTime, e.endRegisterTime, e.endTime FROM EventEntity e WHERE e.status IN :status")
    List<Object[]> findAllPublicTimeAndEndRegisterTime(@Param("status") List<String> status);

    List<EventEntity> findAllByStatusInOrderByIdDesc(List<String> status, Pageable pageable);

    List<EventEntity> findAllByStatusOrderByIdDesc(String status, Pageable pageable);

    List<EventEntity> findAllByCreatorIdAndStatusOrderByIdDesc(int creatorId, Pageable pageable, String status);

    List<EventEntity> findAllByCreatorIdOrderByIdDesc(int creatorId, Pageable pageable);

    List<EventEntity> findAllByCategoryId(int categoryId);

    int countAllByCreatorIdAndStatusIn(int id, List<String> status);
}

