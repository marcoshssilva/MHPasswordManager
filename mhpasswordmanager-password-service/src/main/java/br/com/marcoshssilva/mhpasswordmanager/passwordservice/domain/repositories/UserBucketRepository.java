package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserBucket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserBucketRepository extends JpaRepository<UserBucket, String> {
    Optional<UserBucket> findByIdAndOwnerName(String id, String ownerName);
    Set<UserBucket> findAllByOwnerName(String ownerName);
    Page<UserBucket> findAllByOwnerName(String ownerName, Pageable pageable);
}
