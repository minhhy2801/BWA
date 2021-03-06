package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.NewsEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
@Repository
public interface NewsRepository extends JpaRepository<NewsEntity, Integer> {
    NewsEntity findById(int id);

    boolean existsByTitle(String title);

    List<NewsEntity> findAllByStatusOrderByIdDesc(String status, Pageable pageable);

    List<NewsEntity> findAllByStatusOrderByIdDesc(String status);

    List<NewsEntity> findTop200ByStatusOrderByIdDesc(String status);

    List<NewsEntity> findAllByOrderByIdDesc();

    List<NewsEntity> findAllByStatusAndTitleContainingIgnoreCase(String status, String value);

    int countAllByCreatorId(int id);

    int countAllByStatus(String status);

    @Query(value = "select n.id, n.createdTime, n.title, t.totalOfComts from news n inner join (select top 5 n.id, COUNT(c.id) as 'totalOfComts' from News n inner join Comment c on n.id = c.newsId group by n.id order by totalOfComts desc) t on n.id = t.id and n.status = 'PUBLIC' ",
            nativeQuery = true)
    List<Map<String, Object>> getTop5NewsHavingManyComments();


}
