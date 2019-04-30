package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.AccountEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {
    AccountEntity findByPhone(String phone);

    AccountEntity findById(int id);

    List<AccountEntity> findAllByOrderByIdDesc(Pageable pageable);

    List<AccountEntity> findAllByStatusOrderByRateDesc(String status);
}
