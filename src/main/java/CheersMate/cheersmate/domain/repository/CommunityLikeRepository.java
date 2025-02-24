package CheersMate.cheersmate.domain.repository;

import CheersMate.cheersmate.domain.entity.Community;
import CheersMate.cheersmate.domain.entity.CommunityLike;
import CheersMate.cheersmate.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {
    Optional<CommunityLike> findByCommunityAndUser(Community community, Users user);
    Long countByCommunity(Community community);
}
