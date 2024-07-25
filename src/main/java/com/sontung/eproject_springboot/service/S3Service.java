package com.sontung.eproject_springboot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class S3Service {
    @Value("${aws.s3.bucket}")
    private String buketName;

    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadFile(MultipartFile file) throws IOException,
            AwsServiceException,
            SdkClientException,
            S3Exception {
        Path tempFile = Files.createTempFile(file.getOriginalFilename(), ".tmp");
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(buketName)
                            .key(file.getOriginalFilename())
                            .contentType("image/jpg")
                            .build()
                    , tempFile
            );
        } finally {
            Files.deleteIfExists(tempFile);
        }

    }

    public void deleteFile(String image) throws AwsServiceException,
    SdkClientException,
    S3Exception{
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(buketName)
                .key(image)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    public void updateFile(String fileName, InputStream newFileContent) throws Exception {
        try {
            // Tải lên hình ảnh mới (ghi đè nếu tồn tại)
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(buketName)
                    .key(fileName)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(newFileContent, newFileContent.available()));
        }catch (SdkClientException e) {
            throw new Exception("SDK Client Exception: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new Exception("IOException: " + e.getMessage(), e);
        }
    }
}
