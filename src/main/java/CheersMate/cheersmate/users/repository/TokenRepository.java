package CheersMate.cheersmate.users.repository;

import CheersMate.cheersmate.users.entity.Tokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Tokens, String> {
}
