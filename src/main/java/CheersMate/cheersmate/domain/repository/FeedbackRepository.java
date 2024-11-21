package CheersMate.cheersmate.domain.repository;

import CheersMate.cheersmate.domain.entity.Feedback;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("SELECT f.liquor.id AS liquorId, f.liquor.name AS liquorName, f.liquor.imageLink AS liquorImage, AVG(f.rating) AS avgRating " +
            "FROM Feedback f " +
            "GROUP BY f.liquor.id, f.liquor.name, f.liquor.imageLink " +
            "ORDER BY AVG(f.rating) DESC")
    List<Object[]> findTopRatedLiquors(Pageable pageable);

    @Query("SELECT f.emotion, f.weatherCondition, " +
            "SUM(CASE WHEN f.rating >= 3 THEN 1 ELSE 0 END) AS positiveCount, " +
            "SUM(CASE WHEN f.rating < 3 THEN 1 ELSE 0 END) AS negativeCount " +
            "FROM Feedback f " +
            "GROUP BY f.emotion, f.weatherCondition")
    List<Object[]> getFeedbackStatistics();
}
