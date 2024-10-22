package com.sontung.eproject_springboot.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

public interface S3Service {
    void uploadFile(MultipartFile file, String uniqueFilename)
            throws IOException, AwsServiceException, SdkClientException, S3Exception;

    void deleteFile(String image) throws AwsServiceException, SdkClientException, S3Exception;

    void updateFile(String fileName, InputStream newFileContent) throws Exception;

    String getFileExtension(MultipartFile file);

    String generateUniqueFilename(MultipartFile file);
}
