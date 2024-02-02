package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserBucket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserBucketRepository extends JpaRepository<UserBucket, String> {
    Set<UserBucket> findAllByOwnerName(String ownerName);
    Page<UserBucket> findAllByOwnerName(String ownerName, Pageable pageable);
}
