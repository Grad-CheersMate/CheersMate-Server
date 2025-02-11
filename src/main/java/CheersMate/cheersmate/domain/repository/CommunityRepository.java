package CheersMate.cheersmate.domain.repository;

import CheersMate.cheersmate.domain.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
}
