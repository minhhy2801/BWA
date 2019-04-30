package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.ReferencesLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface ReferencesLinkRepository extends JpaRepository<ReferencesLinkEntity, Integer> {
    ReferencesLinkEntity findById(int id);

    boolean existsByUrl(String url);

}
