package br.com.marcoshssilva.mhpasswordmanager.userservice.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class ImageUtilsTest {
    enum ImageFilesEnum {
        IMG1("car1.jpg", "image/jpeg", 765, 480),
        IMG2("car2.jpg", "image/jpg", 1024, 641),
        IMG3("logo.png", "image/png", 1024, 1024),
        ;
        ImageFilesEnum(String fileName, String contentType, int width, int height) {
            this.fileName = fileName;
            this.contentType = contentType;
            this.width = width;
            this.height = height;
        }
        private final String fileName;
        private final String contentType;
        private final int width;
        private final int height;
    }


    @DisplayName("Should read an image from a MultipartFile and return a BufferedImage")
    @ParameterizedTest
    @EnumSource(ImageFilesEnum.class)
    void readImage(ImageFilesEnum imageFile) throws IOException {
        InputStream fileStream = new ClassPathResource(imageFile.fileName).getInputStream();
        BufferedImage image = ImageUtils.readImage(new MockMultipartFile("image", imageFile.fileName, imageFile.contentType, fileStream.readAllBytes()));
        assertThat(image).isNotNull();
        assertThat(image.getWidth()).isEqualTo(imageFile.width);
        assertThat(image.getHeight()).isEqualTo(imageFile.height);
        fileStream.close();
    }

    @DisplayName("Should resize an image and return a BufferedImage saved as Temp")
    @ParameterizedTest
    @EnumSource(ImageFilesEnum.class)
    void resizeImage(ImageFilesEnum imageFile) throws IOException {
        InputStream fileStream = new ClassPathResource(imageFile.fileName).getInputStream();
        BufferedImage image = ImageUtils.readImage(new MockMultipartFile("image", imageFile.fileName, imageFile.contentType, fileStream.readAllBytes()));
        BufferedImage resizedImage = ImageUtils.resizeImage(image, 200, 200);
        assertThat(resizedImage).isNotNull();
        assertThat(resizedImage.getWidth()).isEqualTo(200);
        assertThat(resizedImage.getHeight()).isEqualTo(200);

        File file = ImageUtils.saveImageAsTemp(resizedImage);
        assertThat(Files.exists(file.toPath())).isTrue();

        InputStream tempFile = new FileInputStream(file);
        assertThat(tempFile.readAllBytes()).isNotEmpty();

        tempFile.close();
        fileStream.close();
    }

    @DisplayName( "Should save BufferedImage as png file in output dir")
    @ParameterizedTest
    @EnumSource(ImageFilesEnum.class)
    void resizeAndSaveFileAsOutput(ImageFilesEnum imageFile) throws IOException {
        InputStream fileStream = new ClassPathResource(imageFile.fileName).getInputStream();
        BufferedImage image = ImageUtils.readImage(new MockMultipartFile("image", imageFile.fileName, imageFile.contentType, fileStream.readAllBytes()));
        String tmpDir = System.getProperty("java.io.tmpdir");

        File outputFile = new File(tmpDir.concat("/").concat(imageFile.fileName));
        outputFile.createNewFile();
        ImageUtils.saveImageToOutputFile(image, outputFile);

        InputStream tempFile = new FileInputStream(outputFile);
        assertThat(tempFile.readAllBytes()).isNotEmpty();
        outputFile.deleteOnExit();

        tempFile.close();
        fileStream.close();
    }
}
