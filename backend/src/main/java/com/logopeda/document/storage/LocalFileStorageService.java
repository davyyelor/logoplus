package com.logopeda.document.storage;

import com.logopeda.shared.config.AppProperties;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Stores document bytes on the local filesystem under the configured storage
 * root, partitioned by clinic id. Storage keys are server-generated UUIDs, so
 * client-supplied file names can never influence the path (no path traversal).
 * On load, the resolved path is verified to remain inside the storage root as a
 * defense-in-depth check.
 */
@Service
@Primary
public class LocalFileStorageService implements StoragePort {

    private final Path root;

    public LocalFileStorageService(AppProperties properties) {
        this.root = Path.of(properties.getStorage().getLocalRoot()).toAbsolutePath().normalize();
    }

    @Override
    public String store(String clinicId, byte[] content, String contentType) {
        String safeClinic = sanitizeSegment(clinicId);
        String fileName = UUID.randomUUID().toString();
        String key = safeClinic + "/" + fileName;
        Path target = resolveWithinRoot(key);
        try {
            Files.createDirectories(target.getParent());
            Files.write(target, content);
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to store document", ex);
        }
        return key;
    }

    @Override
    public byte[] load(String key) {
        Path target = resolveWithinRoot(key);
        try {
            return Files.readAllBytes(target);
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to read document", ex);
        }
    }

    @Override
    public void delete(String key) {
        Path target = resolveWithinRoot(key);
        try {
            Files.deleteIfExists(target);
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to delete document", ex);
        }
    }

    /** Resolves a key under the root and rejects anything escaping it. */
    private Path resolveWithinRoot(String key) {
        Path resolved = root.resolve(key).normalize();
        if (!resolved.startsWith(root)) {
            throw new IllegalArgumentException("Invalid storage key");
        }
        return resolved;
    }

    private String sanitizeSegment(String value) {
        if (value == null || value.isBlank()) {
            return "default";
        }
        return value.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
