package CheersMate.cheersmate.food.service;

import CheersMate.cheersmate.food.dto.FoodDTO;
import CheersMate.cheersmate.food.entity.Food;
import CheersMate.cheersmate.food.entity.FoodCategory;
import CheersMate.cheersmate.food.repository.FoodCategoryRepository;
import CheersMate.cheersmate.food.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;
    private final FoodCategoryRepository foodCategoryRepository;

    // 전체 음식 리스트 조회
    public List<FoodDTO> getAllFoods() {
        return foodRepository.findAll().stream().map(food -> {
            FoodDTO dto = new FoodDTO();
            dto.setId(food.getFoodId());
            dto.setName(food.getName());
            dto.setDescription(food.getDescription());
            dto.setImage(food.getImage());
            return dto;
        }).collect(Collectors.toList());
    }

    // 개별 음식 조회
    public FoodDTO getFoodById(Long foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid food ID"));

        // Food 엔티티를 FoodDTO로 변환
        FoodDTO dto = new FoodDTO();
        dto.setId(food.getFoodId());
        dto.setName(food.getName());
        dto.setDescription(food.getDescription());
        dto.setImage(food.getImage());

        return dto;
    }

    // 카테고리별 음식 조회
    public List<FoodDTO> getFoodsByCategoryId(Long categoryId) {
        return foodRepository.findByCategory_FoodcateId(categoryId).stream().map(food -> {
            FoodDTO dto = new FoodDTO();
            dto.setId(food.getFoodId());
            dto.setName(food.getName());
            dto.setDescription(food.getDescription());
            dto.setImage(food.getImage());
            dto.setFoodCategoryId(food.getCategory().getFoodcateId());
            return dto;
        }).collect(Collectors.toList());
    }

    // 음식 추가
    @Transactional
    public void addFood(FoodDTO foodDTO) {
        FoodCategory category = foodCategoryRepository.findById(foodDTO.getFoodCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        Food food = new Food();
        food.setName(foodDTO.getName());
        food.setDescription(foodDTO.getDescription());
        food.setImage(foodDTO.getImage());
        food.setCategory(category);

        foodRepository.save(food);
    }

    // 음식 수정
    @Transactional
    public void updateFood(Long foodId, FoodDTO foodDTO) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid food ID"));

        FoodCategory category = foodCategoryRepository.findById(foodDTO.getFoodCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        food.setName(foodDTO.getName());
        food.setDescription(foodDTO.getDescription());
        food.setImage(foodDTO.getImage());
        food.setCategory(category);

        foodRepository.save(food);
    }

    // 음식 삭제
    @Transactional
    public void deleteFood(Long foodId) {
        foodRepository.deleteById(foodId);
    }
}
