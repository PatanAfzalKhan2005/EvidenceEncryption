package com.imageseal.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

class ImageStorageServiceTest {
    @TempDir Path temp;

    @Test void encryptsFilesAtRestAndReadsOriginalBytes() throws Exception {
        var storage = new ImageStorageService(temp.toString(), Base64.getEncoder().encodeToString(new byte[32]));
        byte[] image = "image bytes for the test".getBytes();
        storage.write("user/image.jpg", image);
        byte[] diskBytes = Files.readAllBytes(temp.resolve("user/image.jpg"));
        assertFalse(java.util.Arrays.equals(image, diskBytes));
        assertArrayEquals(image, storage.read("user/image.jpg"));
        storage.delete("user/image.jpg");
        assertFalse(Files.exists(temp.resolve("user/image.jpg")));
    }
}
