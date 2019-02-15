package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.RequestNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestNotificationRepository extends JpaRepository<RequestNotificationEntity, Integer> {
}
