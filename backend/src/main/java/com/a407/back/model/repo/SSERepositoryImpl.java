package com.a407.back.model.repo;

import com.a407.back.config.SSEConfig;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
@RequiredArgsConstructor
public class SSERepositoryImpl implements SSERepository{

    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    @Override
    public Optional<SseEmitter> get(Long userId) {
        return Optional.ofNullable(emitterMap.get(userId));
    }

    @Override
    public void save(Long userId, SseEmitter sseEmitter) {
        emitterMap.put(userId, sseEmitter);
    }

    public void delete(Long userId) {
        emitterMap.remove(userId);
    }

    public SseEmitter getSseEmitter(Long userId) {
        return emitterMap.get(userId);
    }

}
