package CheersMate.cheersmate.domain.service;

import CheersMate.cheersmate.domain.dto.CommunityCommentRequestDto;
import CheersMate.cheersmate.domain.dto.CommunityCommentResponseDto;
import CheersMate.cheersmate.domain.entity.Community;
import CheersMate.cheersmate.domain.entity.CommunityComment;
import CheersMate.cheersmate.domain.repository.CommunityCommentRepository;
import CheersMate.cheersmate.domain.repository.CommunityRepository;
import CheersMate.cheersmate.users.entity.Users;
import CheersMate.cheersmate.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {
    private final CommunityCommentRepository commentRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommunityCommentResponseDto createComment(Long userId, CommunityCommentRequestDto dto) {
        Community community = communityRepository.findById(dto.getCommunityId())
                .orElseThrow(() -> new RuntimeException("Community not found with id: " + dto.getCommunityId()));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        CommunityComment comment = new CommunityComment();
        comment.setCommunity(community);
        comment.setUser(user);
        comment.setContent(dto.getContent());

        CommunityComment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommunityCommentResponseDto> getCommentsByCommunity(Long communityId) {
        List<CommunityComment> comments = commentRepository.findAll()
                .stream()
                .filter(c -> c.getCommunity().getCommunityId().equals(communityId))
                .collect(Collectors.toList());

        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommunityCommentResponseDto updateComment(Long commentId, Long userId, CommunityCommentRequestDto dto) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("No permission to update this comment");
        }

        comment.setContent(dto.getContent());

        CommunityComment updatedComment = commentRepository.save(comment);
        return convertToDto(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("No permission to delete this comment");
        }
        commentRepository.delete(comment);
    }

    private CommunityCommentResponseDto convertToDto(CommunityComment comment) {
        CommunityCommentResponseDto dto = new CommunityCommentResponseDto();
        dto.setCommentId(comment.getCommentId());
        dto.setCommunityId(comment.getCommunity().getCommunityId());
        dto.setUserId(comment.getUser().getUserId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }
}