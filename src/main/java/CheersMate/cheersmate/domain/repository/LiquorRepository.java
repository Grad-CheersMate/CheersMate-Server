package CheersMate.cheersmate.domain.repository;

import CheersMate.cheersmate.domain.entity.Liquor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LiquorRepository extends JpaRepository<Liquor, Long> {
    Page<Liquor> findByCategory(String category, Pageable pageable);
    Liquor findByNameAndCategoryAndAlcohol(String name, String category, Double alcohol);
    Optional<Liquor> findByName(String name);

    // 이름으로 검색하는 메서드 추가
    Page<Liquor> findByNameContaining(String keyword, Pageable pageable);
}
