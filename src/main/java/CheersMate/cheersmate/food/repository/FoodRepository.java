package CheersMate.cheersmate.food.repository;

import CheersMate.cheersmate.food.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByCategory_FoodcateId(Long foodcateId);
}
