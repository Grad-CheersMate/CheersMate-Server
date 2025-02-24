package CheersMate.cheersmate.domain.controller;

import CheersMate.cheersmate.domain.dto.CommunityCommentRequestDto;
import CheersMate.cheersmate.domain.dto.CommunityCommentResponseDto;
import CheersMate.cheersmate.domain.service.CommunityCommentService;
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

@RestController
@RequestMapping("/api/community/comments")
@RequiredArgsConstructor
@Slf4j
public class CommunityCommentController {
    private final CommunityCommentService commentService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<CommunityCommentResponseDto> createComment(
            @RequestBody CommunityCommentRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            log.error("User not found for email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CommunityCommentResponseDto responseDto = commentService.createComment(user.getUserId(), dto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{communityId}")
    public ResponseEntity<List<CommunityCommentResponseDto>> getCommentsByCommunity(@PathVariable Long communityId) {
        List<CommunityCommentResponseDto> comments = commentService.getCommentsByCommunity(communityId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommunityCommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommunityCommentRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            log.error("User not found for email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CommunityCommentResponseDto responseDto = commentService.updateComment(commentId, user.getUserId(), dto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            log.error("User not found for email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        commentService.deleteComment(commentId, user.getUserId());
        return ResponseEntity.noContent().build();
    }
}
