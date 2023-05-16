package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetails;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetailsPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountDetailsRepository extends JpaRepository<AccountDetails, AccountDetailsPK> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE AccountDetails ad SET ad.firstName = ?2, ad.lastName = ?3 WHERE ad.id.username = ?1")
    void updateAccountDetailsByUsername(String username, String firstName, String lastName);

    @Modifying(clearAutomatically = true)
    @Query("DELETE AccountDetails ad WHERE ad.id.username = ?1")
    void deleteAccountDetailsByIdUsername(String username);

    @Query("SELECT obj FROM AccountDetails obj WHERE obj.id.username = ?1")
    Optional<AccountDetails> getAccountDetailsByIdUsername(String username);
}
