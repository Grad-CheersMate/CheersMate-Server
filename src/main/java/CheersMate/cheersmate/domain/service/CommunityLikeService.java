package CheersMate.cheersmate.domain.service;

import CheersMate.cheersmate.domain.dto.CommunityLikeRequestDto;
import CheersMate.cheersmate.domain.dto.CommunityLikeResponseDto;
import CheersMate.cheersmate.domain.entity.Community;
import CheersMate.cheersmate.domain.entity.CommunityLike;
import CheersMate.cheersmate.domain.repository.CommunityLikeRepository;
import CheersMate.cheersmate.domain.repository.CommunityRepository;
import CheersMate.cheersmate.users.entity.Users;
import CheersMate.cheersmate.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityLikeService {
    private final CommunityLikeRepository likeRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommunityLikeResponseDto likeOrUnlikeCommunity(Long userId, CommunityLikeRequestDto dto) {
        // 게시물 조회
        Community community = communityRepository.findById(dto.getCommunityId())
                .orElseThrow(() -> new RuntimeException("Community not found with id: " + dto.getCommunityId()));
        // 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // 이미 좋아요한 상태인지 확인
        Optional<CommunityLike> existingLike = likeRepository.findByCommunityAndUser(community, user);
        if (existingLike.isPresent()) {
            // 이미 좋아요한 경우 -> 좋아요 취소(삭제)
            likeRepository.delete(existingLike.get());
            CommunityLikeResponseDto responseDto = new CommunityLikeResponseDto();
            responseDto.setLikeId(null);
            responseDto.setCommunityId(community.getCommunityId());
            responseDto.setUserId(userId);
            responseDto.setCreatedAt(null);
            return responseDto;
        } else {
            // 좋아요하지 않은 경우 -> 좋아요 추가
            CommunityLike like = new CommunityLike();
            like.setCommunity(community);
            like.setUser(user);
            CommunityLike savedLike = likeRepository.save(like);
            return convertToDto(savedLike);
        }
    }

    @Transactional(readOnly = true)
    public long countLikes(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found with id: " + communityId));
        return likeRepository.countByCommunity(community);
    }

    private CommunityLikeResponseDto convertToDto(CommunityLike like) {
        CommunityLikeResponseDto dto = new CommunityLikeResponseDto();
        dto.setLikeId(like.getLikeId());
        dto.setCommunityId(like.getCommunity().getCommunityId());
        dto.setUserId(like.getUser().getUserId());
        dto.setCreatedAt(like.getCreatedAt());
        return dto;
    }
}