package eu.hyperspace.ftsapp.security.service;

import eu.hyperspace.ftsapp.security.authentication.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {
    private final SecretKey signKey;

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(signKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public AuthenticatedUser getUserFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String userId = claims.get("ID", String.class);
        String email = claims.getSubject();

        return new AuthenticatedUser(userId, email);
    }

    @Override
    public String generateJwtToken() {
        Date expirationTime = Date.from(
                Instant.now().plusSeconds(15 * 60)
        );

        Claims claims = Jwts.claims()
                .subject("test@mail.ru")
                .expiration(expirationTime)
                .add("ID", "1")
                .build();

        return Jwts.builder()
                .claims(claims)
                .signWith(signKey, Jwts.SIG.HS256)
                .compact();
    }
}
