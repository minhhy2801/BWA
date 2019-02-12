package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.EventEntity;
import capstone.bwa.demo.entities.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImageEntity, Integer> {
    public List<ImageEntity> findAllByEventByOwnId_IdAndType(int id, String type);
}
