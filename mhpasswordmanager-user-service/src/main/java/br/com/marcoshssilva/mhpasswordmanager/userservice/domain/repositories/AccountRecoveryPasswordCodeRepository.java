package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountRecoveryPasswordCode;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountRecoveryPasswordCodePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AccountRecoveryPasswordCodeRepository extends JpaRepository<AccountRecoveryPasswordCode, AccountRecoveryPasswordCodePK> {
    @Query("SELECT obj FROM AccountRecoveryPasswordCode obj WHERE obj.id.code = ?1 AND obj.id.ipClient = ?2 AND obj.userAgentClient = ?3 AND obj.completed = false AND obj.expiresAt > ?4")
    Optional<AccountRecoveryPasswordCode> findValidCode(String code, String ipClient, String userAgentClient, LocalDateTime now);
}
