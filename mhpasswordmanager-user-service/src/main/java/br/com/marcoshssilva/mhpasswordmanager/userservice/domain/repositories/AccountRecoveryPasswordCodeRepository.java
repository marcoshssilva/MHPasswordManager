package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountRecoveryPasswordCode;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountRecoveryPasswordCodePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRecoveryPasswordCodeRepository extends JpaRepository<AccountRecoveryPasswordCode, AccountRecoveryPasswordCodePK> {

}
