package CheersMate.cheersmate.domain.controller;

import CheersMate.cheersmate.domain.dto.CommunityLikeRequestDto;
import CheersMate.cheersmate.domain.dto.CommunityLikeResponseDto;
import CheersMate.cheersmate.domain.service.CommunityLikeService;
import CheersMate.cheersmate.users.entity.Users;
import CheersMate.cheersmate.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/likes")
@RequiredArgsConstructor
@Slf4j
public class CommunityLikeController {
    private final CommunityLikeService communityLikeService;
    private final UserRepository userRepository;

    /**
     * 인증된 사용자가 좋아요 버튼을 누르면, 이미 좋아요한 경우 좋아요를 취소하고,
     * 아직 좋아요하지 않은 경우에는 좋아요를 추가합니다.
     */
    @PostMapping
    public ResponseEntity<CommunityLikeResponseDto> likeOrUnlikeCommunity(
            @RequestBody CommunityLikeRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CommunityLikeResponseDto responseDto = communityLikeService.likeOrUnlikeCommunity(user.getUserId(), dto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
