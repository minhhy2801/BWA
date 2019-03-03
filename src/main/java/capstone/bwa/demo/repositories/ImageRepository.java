package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.AccessoryEntity;
import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.entities.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImageEntity, Integer> {
    ImageEntity findById(int id);

    ImageEntity findByAccessoryByOwnId(AccessoryEntity accessoryEntity);

    List<ImageEntity> findAllBySupplyProductByOwnId_IdAndType(int id, String type);

    List<ImageEntity> findAllByEventByOwnId_IdAndType(int id, String type);
}
