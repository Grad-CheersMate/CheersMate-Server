package CheersMate.cheersmate.domain.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    String storeImage(MultipartFile file) throws Exception;

    void deleteImage(String imageUrl) throws Exception;
}
