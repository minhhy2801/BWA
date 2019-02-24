package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    CategoryEntity findByName(String name);
    CategoryEntity findById(int id);
    boolean existsByName(String name);
}
