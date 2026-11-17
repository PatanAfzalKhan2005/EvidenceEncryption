package com.imageseal.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

@Service
public class ImageValidationService {
    private static final long MAX_BYTES = 15L * 1024 * 1024;
    private static final long MAX_PIXELS = 100_000_000L;

    public void validate(MultipartFile file) throws Exception {
        if (file.isEmpty() || file.getSize() > MAX_BYTES)
            throw new IllegalArgumentException("Choose a JPG or PNG image up to 15 MB.");
        String mime = file.getContentType();
        if (mime == null || !Set.of("image/jpeg", "image/png").contains(mime))
            throw new IllegalArgumentException("Only JPG and PNG images are supported.");
        byte[] bytes = file.getBytes();
        boolean png = bytes.length >= 8 && (bytes[0] & 255) == 137 && bytes[1] == 80 && bytes[2] == 78 && bytes[3] == 71 && bytes[4] == 13 && bytes[5] == 10 && bytes[6] == 26 && bytes[7] == 10;
        boolean jpeg = bytes.length >= 3 && (bytes[0] & 255) == 255 && (bytes[1] & 255) == 216 && (bytes[2] & 255) == 255;
        if (("image/png".equals(mime) && !png) || ("image/jpeg".equals(mime) && !jpeg))
            throw new IllegalArgumentException("Image content does not match its JPG or PNG file type.");
        try (ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw new IllegalArgumentException("The selected file is not a readable image.");
            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                long pixels = (long) reader.getWidth(0) * reader.getHeight(0);
                if (pixels <= 0 || pixels > MAX_PIXELS) throw new IllegalArgumentException("Image dimensions exceed the supported limit.");
                if (reader.read(0) == null) throw new IllegalArgumentException("The selected file is not a readable image.");
            } finally { reader.dispose(); }
        } catch (IOException e) { throw new IllegalArgumentException("The selected file is not a readable JPG or PNG image."); }
    }
}
