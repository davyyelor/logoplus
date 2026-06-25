package com.logopeda.document.storage;

/**
 * Placeholder for an AWS S3 (or compatible) storage backend. Not wired in V1 —
 * {@link LocalFileStorageService} is the active {@code @Primary} implementation.
 * Implement these methods and mark this class {@code @Primary} (and configure
 * credentials/bucket) to switch to cloud storage in a later version.
 *
 * <p>Intentionally not a Spring bean to avoid accidental activation.</p>
 */
public class S3StorageService implements StoragePort {

    @Override
    public String store(String clinicId, byte[] content, String contentType) {
        throw new UnsupportedOperationException("S3 storage is not implemented in V1");
    }

    @Override
    public byte[] load(String key) {
        throw new UnsupportedOperationException("S3 storage is not implemented in V1");
    }

    @Override
    public void delete(String key) {
        throw new UnsupportedOperationException("S3 storage is not implemented in V1");
    }
}
