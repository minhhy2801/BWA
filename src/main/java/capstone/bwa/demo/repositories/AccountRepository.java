package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.AccountEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {
    AccountEntity findByPhone(String phone);

    AccountEntity findById(int id);

    List<AccountEntity> findAllByOrderByIdDesc(Pageable pageable);

    List<AccountEntity> findAllByStatusOrderByRateDesc(String status);

//    @Query("select top 4 a from Account a where a.roleId != :adminRoleId ")
//    List<AccountEntity> findAllWithHighestRate(@Param("top") int top, @Param("adminRoleId") int adminRoleId);
}
