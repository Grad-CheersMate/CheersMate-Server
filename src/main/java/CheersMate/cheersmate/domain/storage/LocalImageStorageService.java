package CheersMate.cheersmate.domain.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
public class LocalImageStorageService implements ImageStorageService {
    private static final Logger log = LoggerFactory.getLogger(LocalImageStorageService.class);

    // 허용할 이미지 확장자 목록
    private final List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");

    // 이미지 파일 저장 경로 (필요에 따라 외부 설정으로 변경 가능)
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String storeImage(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new ImageStorageException("잘못된 파일 형식입니다: 확장자가 없습니다.");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!allowedExtensions.contains(extension)) {
            throw new ImageStorageException("지원되지 않는 파일 형식입니다: " + extension);
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ImageStorageException("파일이 이미지가 아닙니다: Content-Type=" + contentType);
        }
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new ImageStorageException("유효한 이미지 파일이 아닙니다.");
        }
        String fileName = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = Paths.get(uploadDir, fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());
        log.info("이미지 저장 성공: {}", filePath.toString());
        // 운영 시 완전한 URL (예: https://yourdomain.com/uploads/파일명) 반환 고려
        return "/uploads/" + fileName;
    }

    @Override
    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }
        String fileName = imageUrl.replaceFirst("/uploads/", "");
        Path filePath = Paths.get(uploadDir, fileName);

        int retries = 3;
        for (int attempt = 1; attempt <= retries; attempt++) {
            try {
                Files.deleteIfExists(filePath);
                log.info("이미지 삭제 성공: {}", filePath.toString());
                break;
            } catch (IOException e) {
                log.error("이미지 삭제 실패 (시도 {}): {}", attempt, e.getMessage());
                if (attempt == retries) {
                    throw new ImageStorageException("이미지 삭제에 실패했습니다: " + filePath.toString(), e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new ImageStorageException("이미지 삭제 재시도 중 인터럽트되었습니다.", ie);
                }
            }
        }
    }
}
