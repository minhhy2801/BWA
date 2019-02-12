package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.AccessoryEntity;
import capstone.bwa.demo.entities.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageEntity,Integer> {
    ImageEntity findByAccessoryByOwnId(AccessoryEntity accessoryEntity);
    ImageEntity findById(int id);
}
