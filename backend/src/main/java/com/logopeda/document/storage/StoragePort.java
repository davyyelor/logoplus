package com.logopeda.document.storage;

/**
 * Storage abstraction for binary document content. V1 ships a local-disk
 * implementation; an S3 implementation is stubbed for a future cloud deployment.
 * Implementations must treat {@code key} as opaque and never derive it from
 * untrusted client input.
 */
public interface StoragePort {

    /**
     * Persists the given content and returns an opaque storage key that can later
     * be passed to {@link #load(String)} / {@link #delete(String)}.
     *
     * @param clinicId    owning clinic, used to partition stored objects
     * @param content     the bytes to store
     * @param contentType MIME type (for backends that record it)
     * @return opaque storage key
     */
    String store(String clinicId, byte[] content, String contentType);

    /** Reads previously stored content. */
    byte[] load(String key);

    /** Removes stored content. Implementations should be idempotent. */
    void delete(String key);
}
