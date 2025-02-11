package CheersMate.cheersmate.domain.service;

import CheersMate.cheersmate.domain.dto.CommunityRequestDto;
import CheersMate.cheersmate.domain.dto.CommunityResponseDto;
import CheersMate.cheersmate.domain.entity.Community;
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
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommunityResponseDto createCommunity(CommunityRequestDto dto) {
        Community community = new Community();
        community.setTitle(dto.getTitle());
        community.setImageLink(dto.getImageLink());
        community.setDescription(dto.getDescription());

        // 작성자(Users) 조회
        Users user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));
        community.setUser(user);

        Community savedCommunity = communityRepository.save(community);
        return convertToDto(savedCommunity);
    }

    @Transactional
    public CommunityResponseDto updateCommunity(Long communityId, CommunityRequestDto dto) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found with id: " + communityId));

        community.setTitle(dto.getTitle());
        community.setImageLink(dto.getImageLink());
        community.setDescription(dto.getDescription());

        Community updatedCommunity = communityRepository.save(community);
        return convertToDto(updatedCommunity);
    }

    @Transactional(readOnly = true)
    public CommunityResponseDto getCommunity(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found with id: " + communityId));
        return convertToDto(community);
    }

    @Transactional
    public void deleteCommunity(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found with id: " + communityId));
        communityRepository.delete(community);
    }

    @Transactional(readOnly = true)
    public List<CommunityResponseDto> getAllCommunities() {
        List<Community> communities = communityRepository.findAll();
        return communities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private CommunityResponseDto convertToDto(Community community) {
        CommunityResponseDto dto = new CommunityResponseDto();
        dto.setCommunityId(community.getCommunityId());
        dto.setTitle(community.getTitle());
        dto.setImageLink(community.getImageLink());
        dto.setDescription(community.getDescription());
        dto.setCreatedAt(community.getCreatedAt());
        dto.setUpdatedAt(community.getUpdatedAt());

        if (community.getUser() != null) {
            dto.setUserId(community.getUser().getUserId());
        }
        return dto;
    }
}
