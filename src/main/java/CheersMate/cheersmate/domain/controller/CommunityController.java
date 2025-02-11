package CheersMate.cheersmate.domain.controller;

import CheersMate.cheersmate.domain.dto.CommunityRequestDto;
import CheersMate.cheersmate.domain.dto.CommunityResponseDto;
import CheersMate.cheersmate.domain.service.CommunityService;
import CheersMate.cheersmate.users.entity.Users;
import CheersMate.cheersmate.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/community")
public class CommunityController {
    private final CommunityService communityService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<CommunityResponseDto> createCommunity(@RequestBody CommunityRequestDto dto,
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
        CommunityResponseDto responseDto = communityService.createCommunity(dto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityResponseDto> getCommunity(@PathVariable("id") Long communityId) {
        CommunityResponseDto responseDto = communityService.getCommunity(communityId);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommunityResponseDto> updateCommunity(@PathVariable("id") Long communityId,
                                                                @RequestBody CommunityRequestDto dto,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        // 인증된 사용자의 정보로 업데이트 권한 검증(추가 로직 필요 시 구현)
        String email = userDetails.getUsername();
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 필요한 경우, 현재 사용자와 게시물 작성자의 일치 여부를 체크
        dto.setUserId(user.getUserId());
        CommunityResponseDto responseDto = communityService.updateCommunity(communityId, dto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable("id") Long communityId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        // 삭제 권한 검증 로직(추가 필요) 후 삭제 처리
        String email = userDetails.getUsername();
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        communityService.deleteCommunity(communityId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CommunityResponseDto>> getAllCommunities() {
        List<CommunityResponseDto> list = communityService.getAllCommunities();
        return ResponseEntity.ok(list);
    }
}
