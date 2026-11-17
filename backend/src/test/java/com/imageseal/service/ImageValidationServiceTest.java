package com.imageseal.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import static org.junit.jupiter.api.Assertions.*;

class ImageValidationServiceTest {
    @Test void acceptsRealPngAndRejectsMismatchedFileContent() throws Exception {
        var output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB), "png", output);
        byte[] png = output.toByteArray();
        var validator = new ImageValidationService();
        validator.validate(new MockMultipartFile("file", "pixel.png", "image/png", png));
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(new MockMultipartFile("file", "spoof.jpg", "image/jpeg", png)));
    }
}
