package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<ReportEntity, Integer> {
    ReportEntity findById(int id);

    List<ReportEntity> findAllByCreatorId(int creatorId);

    List<ReportEntity> findAllByStatus(String status);
}
