package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.AccessoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface AccessoryRepository extends JpaRepository<AccessoryEntity, Integer> {
    AccessoryEntity findById(int id);

    AccessoryEntity findByHashAccessoryCode(String code);

    List<AccessoryEntity> findAllByUrlIsNotNull();

    @Query("SELECT a " +
            "FROM SupplyProductEntity p join AccessoryEntity a on p.itemId = a.id " +
            "WHERE p.status = :status")
    List<AccessoryEntity> findAllAccessoryWhichSupplyPostStillPublic(@Param("status") String status);

    @Query(value = "SELECT distinct a.brand FROM AccessoryEntity a")
    List<Object> getAllBrands();


}
