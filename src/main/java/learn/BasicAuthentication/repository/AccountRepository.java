package learn.BasicAuthentication.repository;

import learn.BasicAuthentication.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountEmail(String accountEmail);

    List<Account> findByAccountAdmin(Account admin);
}
