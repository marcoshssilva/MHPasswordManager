package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKey;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordStoredValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserPasswordStoredValueRepository extends JpaRepository<UserPasswordStoredValue, Long> {
    Set<UserPasswordStoredValue> findAllByKeyId(UserPasswordKey id);
    Optional<UserPasswordStoredValue> findByKeyIdAndId(UserPasswordKey keyId, Long id);
}
