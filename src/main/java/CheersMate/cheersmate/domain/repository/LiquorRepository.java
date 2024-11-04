package CheersMate.cheersmate.domain.repository;

import CheersMate.cheersmate.domain.entity.Liquor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiquorRepository extends JpaRepository<Liquor, Long> {
}
