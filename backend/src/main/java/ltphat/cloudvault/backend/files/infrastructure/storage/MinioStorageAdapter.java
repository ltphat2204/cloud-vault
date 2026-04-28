package ltphat.cloudvault.backend.files.infrastructure.storage;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltphat.cloudvault.backend.files.application.service.IStorageService;
import ltphat.cloudvault.backend.files.domain.exception.FileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioStorageAdapter implements IStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public String upload(String key, InputStream content, String contentType) {
        try {
            ensureBucketExists();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .stream(content, content.available(), -1)
                            .contentType(contentType)
                            .build()
            );
            return key;
        } catch (Exception e) {
            log.error("Failed to upload file to MinIO: {}", key, e);
            throw new FileException("Failed to upload file to storage");
        }
    }

    @Override
    public InputStream download(String key) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to download file from MinIO: {}", key, e);
            throw new FileException("Failed to download file from storage");
        }
    }

    @Override
    public void delete(String key) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to delete file from MinIO: {}", key, e);
            throw new FileException("Failed to delete file from storage");
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }
}
