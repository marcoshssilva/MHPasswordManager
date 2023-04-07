package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Page<Account> getAccountsByRolesIn(Set<String> roles, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Account a SET a.password = :encode WHERE a.username = :username")
    void updatePasswordByUsername(String username, String encode);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Account a SET a.enabled = ?2 WHERE a.username = ?1")
    void updateEnabledByUsername(String username, Boolean enabled);
}
