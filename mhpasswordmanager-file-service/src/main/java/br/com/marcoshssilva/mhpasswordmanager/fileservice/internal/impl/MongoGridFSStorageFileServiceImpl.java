package br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.impl;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc.StoredFile;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.IStorageFileService;
import br.com.marcoshssilva.mhpasswordmanager.fileservice.internal.exceptions.StorageErrorException;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MongoGridFSStorageFileServiceImpl implements IStorageFileService {

    private GridFsTemplate gridFsTemplate;
    private GridFsOperations gridFsOperations;

    public MongoGridFSStorageFileServiceImpl(GridFsTemplate gridFsTemplate, GridFsOperations gridFsOperations) {
        this.gridFsTemplate = gridFsTemplate;
        this.gridFsOperations = gridFsOperations;
    }

    @Override
    public StoredFile saveFileInStorage(byte[] data, String bucket, Map<String, String> metadata) throws StorageErrorException {
        throw new StorageErrorException("Not implemented yet.");
    }

    @Override
    public StoredFile getFileInStorage(String id, String bucket) throws StorageErrorException {
        throw new StorageErrorException("Not implemented yet.");
    }

    @Override
    public Boolean deleteFileInStorage(String id, String bucket) throws StorageErrorException {
        throw new StorageErrorException("Not implemented yet.");
    }
}
