package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.CommentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> findAllByNewsIdOrderByIdDesc(int newsId, Pageable pageable);

    CommentEntity findById(int id);

    int countAllByCreatorId(int id);
}
