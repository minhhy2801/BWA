package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.CommentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> findAllByNewsIdAndStatusOrderByIdDesc(int newsId, String status, Pageable pageable);

    CommentEntity findById(int id);

    int countAllByCreatorId(int id);

    int countAllByNewsIdAndStatus(int id, String status);
}
