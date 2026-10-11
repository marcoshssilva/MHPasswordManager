package br.com.marcoshssilva.mhpasswordmanager.userservice.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import static org.assertj.core.api.Assertions.*;

class AvatarImageUtilsTest {
    static MockMultipartFile file(BufferedImage image, String format) throws IOException {
        var output = new ByteArrayOutputStream();
        assertThat(ImageIO.write(image, format, output)).isTrue();
        return new MockMultipartFile("file", "ignored.extension", "application/octet-stream", output.toByteArray());
    }

    @ParameterizedTest
    @ValueSource(strings = {"jpeg", "png", "gif", "bmp", "wbmp", "tiff"})
    void recognizesContentAndProducesPng(String format) throws Exception {
        int type = format.equals("wbmp") ? BufferedImage.TYPE_BYTE_BINARY : BufferedImage.TYPE_INT_RGB;
        byte[] png = ImageUtils.createAvatar(file(new BufferedImage(40, 80, type), format));
        assertThat(png).startsWith((byte)137, (byte)80, (byte)78, (byte)71);
        var result = ImageIO.read(new ByteArrayInputStream(png));
        assertThat(result.getWidth()).isEqualTo(256);
        assertThat(result.getHeight()).isEqualTo(256);
    }

    @ParameterizedTest
    @ValueSource(ints = {32, 512})
    void cropsCenterWithoutDistortingForBothOrientations(int side) {
        for (boolean portrait : new boolean[]{false, true}) {
            var original = new BufferedImage(portrait ? side : side * 3, portrait ? side * 3 : side, BufferedImage.TYPE_INT_RGB);
            var g = original.createGraphics();
            g.setColor(Color.RED);
            g.fillRect(0, 0, original.getWidth(), original.getHeight());
            g.setColor(Color.BLUE);
            g.fillRect(portrait ? 0 : side, portrait ? side : 0, side, side);
            g.dispose();
            var avatar = ImageUtils.centerCropAvatar(original);
            assertThat(avatar.getWidth()).isEqualTo(256);
            assertThat(avatar.getHeight()).isEqualTo(256);
            assertThat(avatar.getRGB(0, 0)).isEqualTo(Color.BLUE.getRGB());
            assertThat(avatar.getRGB(255, 255)).isEqualTo(Color.BLUE.getRGB());
        }
    }

    @Test
    void preservesTransparency() throws Exception {
        var result = ImageIO.read(new ByteArrayInputStream(ImageUtils.createAvatar(
                file(new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB), "png"))));
        assertThat(result.getRGB(128, 128) >>> 24).isZero();
    }

    @Test
    void validatesFileSizeInclusive() throws Exception {
        byte[] png = file(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB), "png").getBytes();
        byte[] exact = java.util.Arrays.copyOf(png, (int) ImageUtils.MAX_AVATAR_BYTES);
        assertThat(ImageUtils.createAvatar(new MockMultipartFile("file", exact))).isNotEmpty();
        assertStatus(new MockMultipartFile("file", new byte[exact.length + 1]), 413);
        assertStatus(new MockMultipartFile("file", new byte[0]), 400);
        assertStatus(new MockMultipartFile("file", new byte[]{1, 2, 3}), 415);
    }

    @Test
    void validatesPixelsInclusiveBeforeDecoding() throws Exception {
        assertThat(ImageUtils.createAvatar(file(new BufferedImage(5000, 5000, BufferedImage.TYPE_BYTE_BINARY), "png"))).isNotEmpty();
        assertStatus(file(new BufferedImage(5001, 5000, BufferedImage.TYPE_BYTE_BINARY), "png"), 413);
    }

    @Test
    void rejectsRecognizedButTruncatedImage() {
        assertStatus(new MockMultipartFile("file", new byte[]{(byte)137, 80, 78, 71, 13, 10, 26, 10}), 400);
    }

    @Test
    void readsOnlyFirstFrame() throws Exception {
        var writer = ImageIO.getImageWritersByFormatName("gif").next();
        var bytes = new ByteArrayOutputStream();
        try (var output = ImageIO.createImageOutputStream(bytes)) {
            writer.setOutput(output);
            writer.prepareWriteSequence(null);
            for (Color color : new Color[]{Color.RED, Color.BLUE}) {
                var frame = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
                var graphics = frame.createGraphics();
                graphics.setColor(color);
                graphics.fillRect(0, 0, 10, 10);
                graphics.dispose();
                writer.writeToSequence(new javax.imageio.IIOImage(frame, null, null), null);
            }
            writer.endWriteSequence();
        } finally {
            writer.dispose();
        }
        var result = ImageIO.read(new ByteArrayInputStream(ImageUtils.createAvatar(new MockMultipartFile("file", bytes.toByteArray()))));
        assertThat(result.getRGB(128, 128)).isEqualTo(Color.RED.getRGB());
    }

    private static void assertStatus(MockMultipartFile file, int status) {
        assertThatThrownBy(() -> ImageUtils.createAvatar(file)).isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getRawStatusCode()).isEqualTo(status));
    }
}
