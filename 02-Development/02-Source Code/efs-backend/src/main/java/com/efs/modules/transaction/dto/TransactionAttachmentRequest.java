package com.efs.modules.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class TransactionAttachmentRequest {

    @NotBlank
    @Size(max = 255)
    private String fileName;

    @NotBlank
    @Size(max = 60)
    private String fileType;

    @Size(max = 120)
    private String mimeType;

    private Long fileSize;

    @NotBlank
    @Size(max = 500)
    private String storageUri;

    @Size(max = 64)
    private String checksumSha256;

    private UUID uploadedBy;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getStorageUri() {
        return storageUri;
    }

    public void setStorageUri(String storageUri) {
        this.storageUri = storageUri;
    }

    public String getChecksumSha256() {
        return checksumSha256;
    }

    public void setChecksumSha256(String checksumSha256) {
        this.checksumSha256 = checksumSha256;
    }

    public UUID getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(UUID uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
}