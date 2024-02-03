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
    @Query(value = "SELECT obj FROM UserPasswordKey obj WHERE obj.id = ?1 AND obj.userBucket.id = ?2")
    Optional<UserPasswordKey> findFirstByIdAndAndUserBucketUuid(Long keyId, String bucketUuid);
    @Query(value = "SELECT obj FROM UserPasswordKey obj WHERE obj.id = ?2 AND obj.userBucket.id = ?1")
    Optional<UserPasswordKey> findByBucketUuidAndId(String bucketUuid, Long id);

    @Query(value = "SELECT obj FROM UserPasswordKey obj WHERE obj.userBucket.id = ?1")
    Page<UserPasswordKey> findAllByUserRegistrationId(String userRegistrationId, Pageable page);

    @Query(value = "SELECT obj FROM UserPasswordKey obj WHERE obj.userBucket.id = ?1")
    Page<UserPasswordKey> findAllByBucketUuid(String bucketUuid, Pageable pageable);
}
