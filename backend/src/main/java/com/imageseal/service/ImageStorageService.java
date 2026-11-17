package com.imageseal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class ImageStorageService {
    private static final int IV_SIZE = 12;
    private final Path root;
    private final SecretKeySpec key;
    private final SecureRandom random = new SecureRandom();

    public ImageStorageService(@Value("${imageseal.storage-dir}") String dir,
                               @Value("${imageseal.encryption-key:}") String encodedKey) throws Exception {
        if (encodedKey == null || encodedKey.isBlank())
            throw new IllegalStateException("IMAGE_ENCRYPTION_KEY must be set to a Base64-encoded 32-byte key");
        byte[] raw = Base64.getDecoder().decode(encodedKey);
        if (raw.length != 32) throw new IllegalStateException("IMAGE_ENCRYPTION_KEY must decode to exactly 32 bytes");
        this.key = new SecretKeySpec(raw, "AES");
        this.root = Path.of(dir).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    public void write(String relativePath, byte[] plainBytes) throws Exception {
        Path target = resolve(relativePath);
        Files.createDirectories(target.getParent());
        byte[] iv = new byte[IV_SIZE]; random.nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
        byte[] encrypted = cipher.doFinal(plainBytes);
        byte[] stored = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, stored, 0, iv.length);
        System.arraycopy(encrypted, 0, stored, iv.length, encrypted.length);
        Files.write(target, stored, StandardOpenOption.CREATE_NEW);
    }

    public byte[] read(String relativePath) throws Exception {
        byte[] stored = Files.readAllBytes(resolve(relativePath));
        if (stored.length < IV_SIZE + 16) throw new IllegalStateException("Stored image data is invalid");
        byte[] iv = java.util.Arrays.copyOfRange(stored, 0, IV_SIZE);
        byte[] encrypted = java.util.Arrays.copyOfRange(stored, IV_SIZE, stored.length);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
        return cipher.doFinal(encrypted);
    }

    public void delete(String relativePath) throws Exception { Files.deleteIfExists(resolve(relativePath)); }

    private Path resolve(String relativePath) {
        Path target = root.resolve(relativePath).normalize();
        if (!target.startsWith(root)) throw new SecurityException("Invalid storage path");
        return target;
    }
}
