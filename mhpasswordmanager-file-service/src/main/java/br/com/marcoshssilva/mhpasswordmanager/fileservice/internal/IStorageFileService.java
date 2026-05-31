package br.com.marcoshssilva.mhpasswordmanager.fileservice.internal;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc.StoredFile;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.exceptions.StorageErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface IStorageFileService {
    StoredFile saveFileInStorage(MultipartFile file, String bucket, Map<String, String> metadata) throws StorageErrorException;
    byte[] getFileInStorage(String id, String bucket) throws StorageErrorException;
    StoredFile getMetadataInStorage(String id, String bucket) throws StorageErrorException;
    Boolean deleteFileInStorage(String id, String bucket) throws StorageErrorException;
}
