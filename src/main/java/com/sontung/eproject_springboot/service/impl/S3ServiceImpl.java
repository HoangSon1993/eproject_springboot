package com.sontung.eproject_springboot.service.impl;

import com.sontung.eproject_springboot.service.S3Service;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:aws-credentials.properties")
public class S3ServiceImpl implements S3Service {
    @Value("${aws.s3.bucket}")
    private String buketName;

    private final S3Client s3Client;

    @Override
    public void uploadFile(MultipartFile file, String uniqueFilename) throws IOException,
            AwsServiceException,
            SdkClientException,
            S3Exception {

        // Resize Image before uploadting.
        Path tempFile = Files.createTempFile("resized-", "." + getFileExtension(file)); // create file tmp.
        resizeImage(file, tempFile); // Call function resize image.

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(buketName)
                            .key(uniqueFilename)
                            .contentType("image/jpg")
                            .build()
                    , tempFile
            );
        } finally {
            Files.deleteIfExists(tempFile); // Delete file tmp
        }

    }

    @Override
    public void deleteFile(String image) throws AwsServiceException,
            SdkClientException,
            S3Exception {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(buketName)
                .key(image)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    @Override
    public void updateFile(String fileName, InputStream newFileContent) throws Exception {
        try {
            // Tải lên hình ảnh mới (ghi đè nếu tồn tại)
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(buketName)
                    .key(fileName)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(newFileContent, newFileContent.available()));
        } catch (SdkClientException e) {
            throw new Exception("SDK Client Exception: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new Exception("IOException: " + e.getMessage(), e);
        }
    }

    /**
     * get file extention
     * Used in ProductController
     **/
    @Override
    public String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        }
        return "jpg"; // Mặc định là jpg nếu không tìm thấy đuôi file
    }

    /**
     * Generate Name file
     * Used in ProductController
     **/
    @Override
    public String generateUniqueFilename(MultipartFile file) {
        String extension = getFileExtension(file);
        return UUID.randomUUID().toString() + "." + extension;
    }

    // =====  Private Function =====

    /**
     * Resize Image with library Thumbnails
     **/
    private void resizeImage(MultipartFile file, Path outputPath) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);

            if (image == null) {
                throw new IOException("Unable to read image from the provided file.");
            }

            int width = image.getWidth();
            int height = image.getHeight();

            if (width > 2048 || height > 2048) {
                Thumbnails.of(image)
                        .size(2048, 2048)
                        .keepAspectRatio(true)
                        .outputFormat(getFileExtension(file))
                        .toFile(outputPath.toFile());
            } else {
                Files.copy(file.getInputStream(), outputPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
    //   ===== End Private Function =====
}
