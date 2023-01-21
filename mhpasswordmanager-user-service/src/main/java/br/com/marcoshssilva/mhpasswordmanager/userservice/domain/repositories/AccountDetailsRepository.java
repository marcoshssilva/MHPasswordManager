package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailsRepository extends JpaRepository<AccountDetails, String> {
}
