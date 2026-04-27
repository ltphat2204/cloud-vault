package ltphat.cloudvault.backend.iam.infrastructure.persistence.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.iam.domain.model.RefreshToken;
import ltphat.cloudvault.backend.iam.domain.repository.IRefreshTokenRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements IRefreshTokenRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String RT_PREFIX = "rt:";
    private static final String AT_RT_PREFIX = "at_rt:";
    private static final String USER_RT_PREFIX = "user_rt:";

    @Override
    public void save(RefreshToken refreshToken) {
        try {
            String rtJson = objectMapper.writeValueAsString(refreshToken);
            long ttl = Duration.between(LocalDateTime.now(), refreshToken.getExpiryDate()).toSeconds();
            
            if (ttl > 0) {
                // Main RT record
                redisTemplate.opsForValue().set(RT_PREFIX + refreshToken.getToken(), rtJson, Duration.ofSeconds(ttl));
                
                // Link AT to RT for quick lookup
                redisTemplate.opsForValue().set(AT_RT_PREFIX + refreshToken.getAccessToken(), refreshToken.getToken(), Duration.ofSeconds(ttl));
                
                // Track RTs by user for global logout/revocation
                redisTemplate.opsForSet().add(USER_RT_PREFIX + refreshToken.getUserEmail(), refreshToken.getToken());
                redisTemplate.expire(USER_RT_PREFIX + refreshToken.getUserEmail(), Duration.ofSeconds(ttl));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize RefreshToken", e);
        }
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        String rtJson = redisTemplate.opsForValue().get(RT_PREFIX + token);
        if (rtJson == null) return Optional.empty();
        
        try {
            return Optional.of(objectMapper.readValue(rtJson, RefreshToken.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteByToken(String token) {
        Optional<RefreshToken> rt = findByToken(token);
        if (rt.isPresent()) {
            redisTemplate.delete(RT_PREFIX + token);
            redisTemplate.delete(AT_RT_PREFIX + rt.get().getAccessToken());
            redisTemplate.opsForSet().remove(USER_RT_PREFIX + rt.get().getUserEmail(), token);
        }
    }

    @Override
    public void deleteByUserEmail(String userEmail) {
        Set<String> tokens = redisTemplate.opsForSet().members(USER_RT_PREFIX + userEmail);
        if (tokens != null) {
            for (String token : tokens) {
                redisTemplate.delete(RT_PREFIX + token);
                // Note: We don't easily have the AT here unless we stored it in the set too, 
                // but we can just clean up the main RT and user link.
            }
        }
        redisTemplate.delete(USER_RT_PREFIX + userEmail);
    }

    @Override
    public boolean existsByAccessToken(String accessToken) {
        return redisTemplate.hasKey(AT_RT_PREFIX + accessToken);
    }

    @Override
    public Optional<RefreshToken> findByAccessToken(String accessToken) {
        String token = redisTemplate.opsForValue().get(AT_RT_PREFIX + accessToken);
        if (token == null) return Optional.empty();
        return findByToken(token);
    }
}
