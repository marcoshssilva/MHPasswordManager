package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc;

import java.util.Map;

public class StoredFile {
    private String id;
    private String bucket;
    private Map<String, String> metadata;
    private byte[] data;
}
