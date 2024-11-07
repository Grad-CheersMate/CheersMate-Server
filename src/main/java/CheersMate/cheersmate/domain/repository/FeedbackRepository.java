package CheersMate.cheersmate.domain.repository;

import CheersMate.cheersmate.domain.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
