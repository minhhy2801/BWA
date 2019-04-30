package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.EventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface EventRepository extends JpaRepository<EventEntity, Integer> {
    EventEntity findById(int id);

    @Query("SELECT e.id, e.publicTime, e.endRegisterTime, e.endTime FROM EventEntity e WHERE e.status IN :status")
    List<Object[]> findAllPublicTimeAndEndRegisterTime(@Param("status") List<String> status);

    List<EventEntity> findAllByStatusInOrderByIdDesc(List<String> status, Pageable pageable);

    List<EventEntity> findTop200ByStatusInOrderByIdDesc(List<String> status);

    List<EventEntity> findAllByStatusOrderByIdDesc(String status, Pageable pageable);

    List<EventEntity> findAllByStatusOrderByIdDesc(String status);

    List<EventEntity> findAllByCreatorIdAndStatusOrderByIdDesc(int creatorId, Pageable pageable, String status);

    List<EventEntity> findAllByCreatorIdAndStatusInOrderByIdDesc(int creatorId, Pageable pageable, List<String> status);

    List<EventEntity> findAllByCreatorIdOrderByIdDesc(int creatorId, Pageable pageable);

    List<EventEntity> findAllByOrderById(Pageable pageable);

    List<EventEntity> findAllByOrderByTotalSoldTicketDesc();

    List<EventEntity> findAllByStatusInAndTitleContainingIgnoreCase(List<String> status, String value);

    int countAllByCreatorIdAndStatusIn(int id, List<String> status);

    int countAllByCreatorId(int id);

    int countAllByStatusIn(List<String> status);

    int countAllByStatus(String status);
}

