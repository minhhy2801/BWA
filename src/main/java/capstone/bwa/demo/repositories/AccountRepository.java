package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {
    AccountEntity findByPhoneAndPasswordAndStatus(String phone, String password, String status);

    AccountEntity findByPhone(String phone);

    AccountEntity findById(int id);
}
