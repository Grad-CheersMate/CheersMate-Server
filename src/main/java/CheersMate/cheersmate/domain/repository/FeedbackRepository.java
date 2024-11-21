package CheersMate.cheersmate.domain.repository;

import CheersMate.cheersmate.domain.entity.Feedback;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("SELECT l.id, l.name, l.alcohol, l.imageLink, l.category " +
            "FROM Liquor l JOIN Feedback f ON l.id = f.liquor.id " +
            "GROUP BY l.id " +
            "ORDER BY AVG(f.rating) DESC")
    List<Object[]> findTopRatedLiquors(Pageable pageable);

    @Query("SELECT f.emotion, f.weatherCondition, " +
            "SUM(CASE WHEN f.rating >= 3 THEN 1 ELSE 0 END) AS positiveCount, " +
            "SUM(CASE WHEN f.rating < 3 THEN 1 ELSE 0 END) AS negativeCount " +
            "FROM Feedback f " +
            "GROUP BY f.emotion, f.weatherCondition")
    List<Object[]> getFeedbackStatistics();
}
