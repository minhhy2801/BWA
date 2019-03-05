package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.AccessoryEntity;
import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.entities.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImageEntity, Integer> {
    ImageEntity findById(int id);

    ImageEntity findByAccessoryByOwnId(AccessoryEntity accessoryEntity);

    List<ImageEntity> findAllBySupplyProductByOwnId_IdAndType(int id, String type);

    List<ImageEntity> findAllByEventByOwnId_IdAndType(int id, String type);

    @Query("SELECT e.url FROM ImageEntity e WHERE e.type = :type and e.ownId = :ownId")
    String findAllByNewsByOwnId_IdAndType(@Param("type")String type, @Param("ownId")int ownId);
}
