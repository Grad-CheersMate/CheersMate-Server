package CheersMate.cheersmate.domain.service;

import CheersMate.cheersmate.domain.dto.FoodDTO;
import CheersMate.cheersmate.domain.entity.Food;
import CheersMate.cheersmate.domain.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;

    // 전체 음식 리스트 조회
    public List<FoodDTO> getAllFoods() {
        return foodRepository.findAll().stream().map(food -> {
            FoodDTO dto = new FoodDTO();
            dto.setId(food.getFoodId());
            dto.setName(food.getName());
            dto.setImageLink(food.getImageLink());
            dto.setCategory(food.getCategory());
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
        dto.setImageLink(food.getImageLink());
        dto.setCategory(food.getCategory());
        return dto;
    }

    // 카테고리별 음식 조회
    public List<FoodDTO> getFoodsByCategory(String categoryName) {
        return foodRepository.findByCategory(categoryName).stream().map(food -> {
            FoodDTO dto = new FoodDTO();
            dto.setId(food.getFoodId());
            dto.setName(food.getName());
            dto.setImageLink(food.getImageLink());
            dto.setCategory(food.getCategory());
            return dto;
        }).collect(Collectors.toList());
    }

    // 음식 추가
    @Transactional
    public void addFood(FoodDTO foodDTO) {
        Food food = new Food();
        food.setName(foodDTO.getName());
        food.setImageLink(foodDTO.getImageLink());
        food.setCategory(foodDTO.getCategory());

        foodRepository.save(food);
    }

    // 음식 수정
    @Transactional
    public void updateFood(Long foodId, FoodDTO foodDTO) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid food ID"));

        food.setName(foodDTO.getName());
        food.setImageLink(foodDTO.getImageLink());
        food.setCategory(foodDTO.getCategory()); // category는 String 타입

        foodRepository.save(food);
    }

    // 음식 삭제
    @Transactional
    public void deleteFood(Long foodId) {
        foodRepository.deleteById(foodId);
    }
}
