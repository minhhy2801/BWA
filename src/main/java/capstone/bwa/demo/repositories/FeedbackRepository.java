package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Integer> {
}
