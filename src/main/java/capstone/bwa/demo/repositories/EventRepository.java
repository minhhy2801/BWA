package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.EventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Integer> {
    EventEntity findById(int id);

    @Query("SELECT e.title FROM EventEntity e WHERE e.status = :status1 OR e.status = :status2")
    List<EventEntity> findAllTitle(@Param("status1") String status1, @Param("status2") String status2);

    @Query("SELECT e.id, e.publicTime, e.endRegisterTime FROM EventEntity e WHERE e.status = :status1 OR e.status = :status2")
    List<Object[]> findAllPublicTimeAndEndRegisterTime(@Param("status1") String status1, @Param("status2") String status2);

    List<EventEntity> findAllByStatusOrStatusOrderByIdDesc(String status1, String status2, Pageable pageable);

    List<EventEntity> findAllByStatusOrderByIdDesc(String status, Pageable pageable);

    List<EventEntity> findAllByCreatorIdAndStatusOrderByIdDesc(int creatorId, Pageable pageable, String status);

    List<EventEntity> findAllByCreatorIdOrderByIdDesc(int creatorId, Pageable pageable);
}

