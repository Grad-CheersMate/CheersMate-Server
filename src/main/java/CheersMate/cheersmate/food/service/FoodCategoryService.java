package CheersMate.cheersmate.food.service;

import CheersMate.cheersmate.food.dto.FoodCategoryDTO;
import CheersMate.cheersmate.food.entity.FoodCategory;
import CheersMate.cheersmate.food.repository.FoodCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodCategoryService {
    private final FoodCategoryRepository foodCategoryRepository;

    // 전체 카테고리 조회
    public List<FoodCategoryDTO> getAllCategories() {
        return foodCategoryRepository.findAll().stream().map(foodcate -> {
            FoodCategoryDTO dto = new FoodCategoryDTO();
            dto.setId(foodcate.getFoodcateId());
            dto.setName(foodcate.getName());
            return dto;
        }).collect(Collectors.toList());
    }

    // 개별 카테고리 조회
    public FoodCategoryDTO getCategoryById(Long categoryId) {
        FoodCategory foodcate = foodCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        // FoodCategory 엔티티를 FoodCategoryDTO로 변환
        FoodCategoryDTO dto = new FoodCategoryDTO();
        dto.setId(foodcate.getFoodcateId());
        dto.setName(foodcate.getName());

        return dto;
    }

    // 카테고리 추가
    @Transactional
    public void addCategory(FoodCategoryDTO categoryDTO) {
        FoodCategory category = new FoodCategory();
        category.setName(categoryDTO.getName());

        foodCategoryRepository.save(category);
    }

    // 카테고리 수정
    @Transactional
    public void updateCategory(Long categoryId, FoodCategoryDTO categoryDTO) {
        FoodCategory category = foodCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        category.setName(categoryDTO.getName());

        foodCategoryRepository.save(category);
    }

    // 카테고리 삭제
    @Transactional
    public void deleteCategory(Long categoryId) {
        foodCategoryRepository.deleteById(categoryId);
    }
}
