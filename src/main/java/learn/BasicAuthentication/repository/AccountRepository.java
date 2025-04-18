package learn.BasicAuthentication.repository;

import learn.BasicAuthentication.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountEmail(String accountEmail);

    List<Account> findByAccountAdmin(Account admin);
}
