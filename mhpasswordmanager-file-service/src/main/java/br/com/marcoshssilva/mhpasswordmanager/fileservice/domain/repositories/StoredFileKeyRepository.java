package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.entities.StoredFileKey;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoredFileKeyRepository extends MongoRepository<StoredFileKey, String> {
    Optional<StoredFileKey> findByUuidAndBucket(String id, String bucket);
}
