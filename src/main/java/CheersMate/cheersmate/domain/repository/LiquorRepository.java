package CheersMate.cheersmate.domain.repository;

import CheersMate.cheersmate.domain.entity.Liquor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiquorRepository extends JpaRepository<Liquor, Long> {
    Page<Liquor> findByCategory(String category, Pageable pageable);
}
