package ltphat.cloudvault.backend.files.application.service;

import java.io.InputStream;

public interface IStorageService {
    String upload(String key, InputStream content, String contentType);
    InputStream download(String key);
    void delete(String key);
}
