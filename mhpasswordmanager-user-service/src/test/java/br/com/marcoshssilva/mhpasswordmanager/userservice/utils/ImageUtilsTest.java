package br.com.marcoshssilva.mhpasswordmanager.userservice.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

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
}
