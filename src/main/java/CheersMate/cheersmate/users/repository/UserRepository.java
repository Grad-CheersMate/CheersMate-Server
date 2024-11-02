package CheersMate.cheersmate.users.repository;

import CheersMate.cheersmate.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByEmail(String email);
    Users findByNickname(String nickname);

    Users findByTellAndNickname(String tell, String nickname);

    Users findByEmailAndTell(String email, String tell);
}