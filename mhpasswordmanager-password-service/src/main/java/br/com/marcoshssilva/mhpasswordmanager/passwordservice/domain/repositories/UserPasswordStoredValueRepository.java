package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordStoredValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPasswordStoredValueRepository extends JpaRepository<UserPasswordStoredValue, Long> {
}
