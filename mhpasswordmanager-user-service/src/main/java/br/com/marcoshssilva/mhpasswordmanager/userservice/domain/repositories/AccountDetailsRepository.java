package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailsRepository extends JpaRepository<AccountDetails, String> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE AccountDetails ad SET ad.firstName = ?2, ad.lastName = ?3 WHERE ad.username = ?1")
    void updateAccountDetailsByUsername(String username, String firstName, String lastName);
}
