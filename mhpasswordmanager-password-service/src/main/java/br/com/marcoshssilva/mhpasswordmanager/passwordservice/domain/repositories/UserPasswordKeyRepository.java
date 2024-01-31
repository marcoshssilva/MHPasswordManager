package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPasswordKeyRepository extends JpaRepository<UserPasswordKey, Long> {
    @Query(value = "SELECT obj FROM UserPasswordKey obj WHERE obj.id = ?2 AND obj.userBucket.name = ?1")
    Optional<UserPasswordKey> findByUserRegistrationEmailAndId(String email, Long id);

    @Query(value = "SELECT obj FROM UserPasswordKey obj WHERE obj.userBucket.id = ?1")
    Page<UserPasswordKey> findAllByUserRegistrationId(String userRegistrationId, Pageable page);

    @Query(value = "SELECT obj FROM UserPasswordKey obj WHERE obj.userBucket.name = ?1")
    Page<UserPasswordKey> findAllByUserRegistrationEmail(String email, Pageable pageable);
}
