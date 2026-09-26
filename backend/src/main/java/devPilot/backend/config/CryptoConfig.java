package devPilot.backend.config;

import devPilot.backend.utils.TextEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoConfig {
    @Bean
    public TextEncoder textEncoder(
            @Value("${app.token-encrypter-password}") String password,
            @Value("${app.token-encryptor-salt}") String salt) {
        return new TextEncoder(password, salt);
    }
}