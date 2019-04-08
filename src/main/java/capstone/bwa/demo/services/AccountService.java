package capstone.bwa.demo.services;

import capstone.bwa.demo.entities.AccountEntity;
import capstone.bwa.demo.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class AccountService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    // dao check login
    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        AccountEntity accountEntity = accountRepository.findByPhone(phone);
//        System.out.println("phone: " + phone);

        if (accountEntity == null) throw new UsernameNotFoundException(phone);
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        GrantedAuthority role = new SimpleGrantedAuthority(accountEntity.getRoleByRoleId().getName());
        grantedAuthorityList.add(role);
        return new User(accountEntity.getId() + ";" + accountEntity.getPhone(), accountEntity.getPassword(), grantedAuthorityList);
    }

}
