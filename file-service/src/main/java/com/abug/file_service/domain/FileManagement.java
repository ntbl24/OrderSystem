package com.abug.file_service.domain;

public class FileManagement {
    private FileId id;
    private OwnerId ownerId;
    private String contentType;
    private long size;
    private String md5Checksum;
    private String path;

    private FileManagement(FileId id, OwnerId ownerId, String contentType, long size, String md5Checksum, String path) {
        this.id = id;
        this.ownerId = ownerId;
        this.contentType = contentType;
        this.size = size;
        this.md5Checksum = md5Checksum;
        this.path = path;
    }

    public static FileManagement create(FileId id, OwnerId ownerId, String contentType, long size, String md5Checksum, String path){
        return new FileManagement(id, ownerId, contentType,size, md5Checksum, path);
    }

    public FileId getId() {
        return id;
    }

    public OwnerId getOwnerId(){return ownerId;}

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }

    public String getMd5Checksum() {
        return md5Checksum;
    }

    public String getPath() {
        return path;
    }
}
