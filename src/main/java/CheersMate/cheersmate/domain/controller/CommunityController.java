package CheersMate.cheersmate.domain.controller;

import CheersMate.cheersmate.domain.dto.CommunityRequestDto;
import CheersMate.cheersmate.domain.dto.CommunityResponseDto;
import CheersMate.cheersmate.domain.service.CommunityService;
import CheersMate.cheersmate.domain.storage.ImageStorageService;
import CheersMate.cheersmate.users.entity.Users;
import CheersMate.cheersmate.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/community")
public class CommunityController {
    private final CommunityService communityService;
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;

    // 커뮤니티 생성 시, CommunityRequestDto와 선택적 이미지 파일을 함께 업로드
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<CommunityResponseDto> createCommunity(
            @RequestPart("data") CommunityRequestDto dto,
            @RequestPart(value = "image", required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 인증된 사용자의 이메일 추출
        String email = userDetails.getUsername();
        // 이메일로 사용자 조회
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 클라이언트가 전달한 userId 대신 인증된 사용자 ID로 설정
        dto.setUserId(user.getUserId());

        // 이미지 파일이 존재하면 이미지 저장 후 URL을 DTO에 설정
        if (file != null && !file.isEmpty()) {
            try {
                String imageUrl = imageStorageService.storeImage(file);
                dto.setImageLink(imageUrl);
            } catch (Exception e) {
                log.error("이미지 저장 실패", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        CommunityResponseDto responseDto = communityService.createCommunity(dto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityResponseDto> getCommunity(@PathVariable("id") Long communityId) {
        CommunityResponseDto responseDto = communityService.getCommunity(communityId);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<CommunityResponseDto> updateCommunity(
            @PathVariable("id") Long communityId,
            @RequestPart("data") CommunityRequestDto dto,
            @RequestPart(value = "image", required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 인증된 사용자의 이메일 추출
        String email = userDetails.getUsername();
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        dto.setUserId(user.getUserId());

        // 기존 커뮤니티 정보를 가져와서 현재 저장된 이미지 URL 확인
        CommunityResponseDto existingCommunity = communityService.getCommunity(communityId);
        String existingImageUrl = existingCommunity.getImageLink();

        // 새 이미지 파일이 존재하면, 기존 이미지 삭제 후 새 이미지 저장
        if (file != null && !file.isEmpty()) {
            try {
                if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                    imageStorageService.deleteImage(existingImageUrl);
                }
                String newImageUrl = imageStorageService.storeImage(file);
                dto.setImageLink(newImageUrl);
            } catch (Exception e) {
                log.error("이미지 업데이트 실패", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            // 새 이미지가 없으면 기존 이미지 URL을 유지할 수 있도록 dto에 설정
            dto.setImageLink(existingImageUrl);
        }

        CommunityResponseDto responseDto = communityService.updateCommunity(communityId, dto);
        return ResponseEntity.ok(responseDto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable("id") Long communityId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        // 인증된 사용자 검증
        String email = userDetails.getUsername();
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 삭제 전 기존 커뮤니티 정보를 가져와 이미지 URL 확인
        CommunityResponseDto existingCommunity = communityService.getCommunity(communityId);
        String existingImageUrl = existingCommunity.getImageLink();

        // 이미지가 존재하면 삭제
        if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
            try {
                imageStorageService.deleteImage(existingImageUrl);
            } catch (Exception e) {
                // 이미지 삭제 실패 시 로깅 후, 선택적으로 실패 응답 처리하거나 계속 진행할 수 있음
                log.error("이미지 삭제 실패: {}", e.getMessage(), e);
                // 예시로 삭제 실패 시 내부 서버 에러 응답을 반환할 수 있음
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        // 커뮤니티 게시물 삭제 처리
        communityService.deleteCommunity(communityId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    public ResponseEntity<List<CommunityResponseDto>> getAllCommunities() {
        List<CommunityResponseDto> list = communityService.getAllCommunities();
        return ResponseEntity.ok(list);
    }
}
