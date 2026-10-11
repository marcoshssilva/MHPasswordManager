package br.com.marcoshssilva.mhpasswordmanager.userservice.utils;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public final class ImageUtils {
    private ImageUtils() { }

    public static final long MAX_AVATAR_BYTES = 8L * 1024 * 1024;
    public static final long MAX_AVATAR_PIXELS = 25_000_000L;

    public static BufferedImage readImage(MultipartFile imageProfile) throws IOException {
        try (var input = imageProfile.getInputStream()) {
            return ImageIO.read(input);
        }
    }

    public static byte[] createAvatar(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty image");
        }
        if (file.getSize() > MAX_AVATAR_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image exceeds 8 MiB");
        }
        try (var input = file.getInputStream();
            var imageInput = new MemoryCacheImageInputStream(input)) {
            var readers = ImageIO.getImageReaders(imageInput);

            if (!readers.hasNext()) {
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported image format");
            }

            var reader = readers.next();

            try(var output = new ByteArrayOutputStream()) {
                reader.setInput(imageInput);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width <= 0 || height <= 0) {
                    throw new IOException("Invalid dimensions");
                }
                if ((long) width * height > MAX_AVATAR_PIXELS) {
                    throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image exceeds 25000000 pixels");
                }
                ImageIO.write(centerCropAvatar(reader.read(0)), "png", output);
                return output.toByteArray();
            } finally {
                reader.dispose();
            }

        } catch (IOException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Corrupt image", e);
        }
    }

    public static BufferedImage centerCropAvatar(BufferedImage original) {
        int side = Math.min(original.getWidth(), original.getHeight());
        int x = (original.getWidth() - side) / 2;
        int y = (original.getHeight() - side) / 2;
        BufferedImage result = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.drawImage(original, 0, 0, 256, 256, x, y, x + side, y + side, null);
        } finally {
            graphics.dispose();
        }
        return result;
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    public static File saveImageAsTemp(BufferedImage image) throws IOException {
        File outputFile = File.createTempFile("image", ".png");
        ImageIO.write(image, "png", outputFile);
        return outputFile;
    }

    public static void saveImageToOutputFile(BufferedImage image, File outputFile) throws IOException {
        ImageIO.write(image, "png", outputFile);
    }
}
