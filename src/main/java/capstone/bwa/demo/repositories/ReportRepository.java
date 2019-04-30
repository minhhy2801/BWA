package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Integer> {
    ReportEntity findById(int id);

    List<ReportEntity> findAllByCreatorId(int creatorId);

    List<ReportEntity> findAllByStatus(String status);
}
