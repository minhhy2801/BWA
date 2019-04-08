package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImageEntity, Integer> {
    ImageEntity findById(int id);

    List<ImageEntity> findAllBySupplyProductByOwnId_IdAndType(int id, String type);

    List<ImageEntity> findAllByOwnIdAndType(int id, String type);

    @Query("SELECT e.id, e.url FROM ImageEntity e WHERE e.type = :type and e.ownId = :ownId")
    List<String> findAllByOwnIdAndType(@Param("type") String type, @Param("ownId") int ownId);
}
