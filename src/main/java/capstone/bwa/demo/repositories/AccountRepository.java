package capstone.bwa.demo.repositories;

import capstone.bwa.demo.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {


}
