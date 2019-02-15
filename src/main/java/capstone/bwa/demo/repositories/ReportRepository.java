package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportEntity, Integer> {
}
