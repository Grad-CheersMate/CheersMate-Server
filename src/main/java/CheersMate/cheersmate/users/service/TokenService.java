package CheersMate.cheersmate.users.service;

import CheersMate.cheersmate.users.entity.Tokens;
import CheersMate.cheersmate.users.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
    private final TokenRepository tokenRepository;

    // Refresh Token 저장
    @Transactional
    public void saveRefreshToken(String email, String refreshToken, LocalDateTime expirationTime) {
        // 기존 토큰 삭제 후 새로 저장
        tokenRepository.deleteById(email);

        Tokens token = new Tokens();
        token.setEmail(email);
        token.setRefreshToken(refreshToken);
        token.setExpirationTime(expirationTime);

        tokenRepository.save(token);
    }

    // Refresh Token 검증
    @Transactional
    public boolean validateRefreshToken(String email, String refreshToken) {
        return tokenRepository.findById(email)
                .filter(t -> t.getRefreshToken().equals(refreshToken) && t.getExpirationTime().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    // Refresh Token 삭제
    @Transactional
    public void deleteRefreshToken(String email) {
        if (email != null) {
            tokenRepository.deleteById(email);
            log.info("Deleted refresh token for email: {}", email);
        } else {
            log.error("Attempted to delete refresh token for null email");
        }
    }
}
