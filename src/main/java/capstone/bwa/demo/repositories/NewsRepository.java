package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface NewsRepository extends JpaRepository<NewsEntity, Integer>  {
    NewsEntity findById(int id);
    
    boolean existsByTitle(String title);
    List<NewsEntity> findAllByStatusInOrderByIdDesc(List<String> status, Pageable pageable);

    List<NewsEntity> findAllByStatusOrderByIdDesc(String status, Pageable pageable);

    List<NewsEntity> findAllByCategoryIdInOrderByIdDesc(int id, Pageable pageable);
}
