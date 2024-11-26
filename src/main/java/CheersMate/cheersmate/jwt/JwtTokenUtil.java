package CheersMate.cheersmate.jwt;

import CheersMate.cheersmate.users.entity.Users;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtTokenUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Users user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuer("CheersMate")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Users user) {
        long expiration = System.currentTimeMillis() + refreshExpiration;
        log.info("Refresh Token expiration set to: {}", new Date(expiration));
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuer("CheersMate")
                .setIssuedAt(new Date())
                .setExpiration(new Date(expiration))
                .signWith(key)
                .compact();
    }

    public String getEmail(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            Date expiration = claimsJws.getBody().getExpiration();
            return !expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우 처리
            return false;
        } catch (JwtException e) {
            // 서명 오류, 잘못된 토큰 형식 등의 처리
            throw new TokenValidationException("Invalid token", e);
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            Date expiration = claimsJws.getBody().getExpiration();
            return !expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            throw new TokenValidationException("Refresh token expired", e);
        } catch (JwtException e) {
            throw new TokenValidationException("Invalid refresh token", e);
        }
    }

    public String extractUsername(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return getClaimFromToken(token, claims -> claims.get("role", String.class));
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}