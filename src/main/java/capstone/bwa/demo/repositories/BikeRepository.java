package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.BikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface BikeRepository extends JpaRepository<BikeEntity, Integer> {
    BikeEntity findById(int id);

    BikeEntity findByHashBikeCode(String hashcode);

    List<BikeEntity> findAll();

    List<BikeEntity> findAllByUrlIsNotNull();

    @Query("SELECT b " +
            "FROM  BikeEntity b join SupplyProductEntity p on p.status = :status and b.id = p.itemId ")
    List<BikeEntity> findAllBikesWhichSupplyPostStillPublic(@Param("status") String status);

    @Query(value = "SELECT distinct b.brand FROM BikeEntity b")
    List<Object> getAllBrands();


}
