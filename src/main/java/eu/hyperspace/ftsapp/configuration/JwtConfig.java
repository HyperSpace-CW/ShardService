package eu.hyperspace.ftsapp.configuration;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Configuration
@Slf4j
public class JwtConfig {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecretKey jwtSecretKey() {
        log.info("JWT secret (raw): {}", jwtSecret); // Выведет ключ в чистом виде
        log.info("JWT secret (bytes): {}", Arrays.toString(jwtSecret.getBytes(StandardCharsets.UTF_8)));
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
