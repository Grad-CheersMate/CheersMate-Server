package CheersMate.cheersmate.domain.repository;

import CheersMate.cheersmate.domain.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
}
