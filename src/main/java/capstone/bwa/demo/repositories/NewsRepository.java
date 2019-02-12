package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<NewsEntity, Integer>  {
    NewsEntity findById(int id);
}
