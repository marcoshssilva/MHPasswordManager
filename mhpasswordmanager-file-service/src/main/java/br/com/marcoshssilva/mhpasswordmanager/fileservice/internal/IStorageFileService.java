package br.com.marcoshssilva.mhpasswordmanager.fileservice.internal;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc.StoredFile;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.exceptions.StorageErrorException;

import java.util.Map;

public interface IStorageFileService {
    StoredFile saveFileInStorage(byte[] data, String bucket, Map<String, String> metadata) throws StorageErrorException;
    StoredFile getFileInStorage(String id, String bucket) throws StorageErrorException;
    Boolean deleteFileInStorage(String id, String bucket) throws StorageErrorException;
}
