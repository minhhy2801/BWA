package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    CategoryEntity findById(int id);

    CategoryEntity findByNameIgnoreCaseAndType(String name, String type);

    List<CategoryEntity> findAllByTypeAndStatus(String type, String status);
}
