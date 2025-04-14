package learn.BasicAuthentication.service;

import jakarta.transaction.Transactional;
import learn.BasicAuthentication.exception.custom.AdminDoesNotExistException;
import learn.BasicAuthentication.exception.custom.EmailAlreadyExistException;
import learn.BasicAuthentication.model.Account;
import learn.BasicAuthentication.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String accountEmail) throws UsernameNotFoundException {
        return accountRepository.findByAccountEmail(accountEmail)
                .orElseThrow(()->new UsernameNotFoundException("Account with given email not found"));
    }

    @Transactional
    public void save(Account account){
        accountRepository.save(account);
    }

    public Optional<Account> findById(Long id){
        return accountRepository.findById(id);
    }

    public String checkDuplicateEmail(String accountEmail) throws EmailAlreadyExistException {
        try {
            Account account = (Account) loadUserByUsername(accountEmail);
        } catch (UsernameNotFoundException cause){
            return accountEmail;
        }
        throw new EmailAlreadyExistException("Account with given Email already exists");
    }

    public Account checkAccountAdmin(String adminEmail) throws AdminDoesNotExistException {
        try{
            return (Account) loadUserByUsername(adminEmail);
        } catch (UsernameNotFoundException e) {
            throw new AdminDoesNotExistException("Admin with given Email does not exist");
        }
    }

}