package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.ReferencesLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferencesLinkRepository extends JpaRepository<ReferencesLinkEntity,Integer> {
    ReferencesLinkEntity findById(int id);

    boolean existsByUrl(String url);

    List<ReferencesLinkEntity> findByCategoryId(int categoryId);
    
    ReferencesLinkEntity findByUrl(String url);
}
