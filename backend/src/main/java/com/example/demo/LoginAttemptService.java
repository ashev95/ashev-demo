package com.example.demo;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class LoginAttemptService {

    private final int MAX_ATTEMPT = 3;
    private final int DISABLE_HOURS = 3;

    private Map<String, LimitInfo> USER_AND_LIMIT;

    @PostConstruct
    public void init() {
        USER_AND_LIMIT = new ConcurrentHashMap<>();
    }

    public void onSuccess(String username) {
        LimitInfo limitInfo = USER_AND_LIMIT.get(username);
        // Очищаем, если до этого не был превышен лимит.
        if (limitInfo != null && limitInfo.getAttempts() < MAX_ATTEMPT) {
            USER_AND_LIMIT.remove(username);
        }
    }

    public void onFail(String username) {
        LimitInfo limitInfo = USER_AND_LIMIT.get(username);
        if (limitInfo == null) {
            limitInfo = new LimitInfo();
            limitInfo.setAttempts(0);
            USER_AND_LIMIT.put(username, limitInfo);
        }
        // Не наращиваем, чтобы избежать переполнения
        if (limitInfo.getAttempts() < MAX_ATTEMPT) {
            limitInfo.setAttempts(limitInfo.getAttempts() + 1);
            if (limitInfo.getAttempts() >= MAX_ATTEMPT) {
                limitInfo.setAdded(Instant.now());
                log.info("{} was PUNISHED", username);
            }
        }
    }

    public boolean isAvailable(String username) {
        final LimitInfo limitInfo = USER_AND_LIMIT.get(username);
        return limitInfo == null || limitInfo.getAttempts() < MAX_ATTEMPT;
    }

    @Scheduled(cron = "${custom.login_attempt_service.clearing.cron}")
    public void checkAndClear() {
        final Instant now = Instant.now();
        for(Iterator<Map.Entry<String, LimitInfo>> it = USER_AND_LIMIT.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, LimitInfo> entry = it.next();
            final String username = entry.getKey();
            final LimitInfo limitInfo = entry.getValue();
            if (limitInfo.getAttempts() >= MAX_ATTEMPT &&
                    ChronoUnit.HOURS.between(limitInfo.getAdded(), now) > DISABLE_HOURS) {
                log.info("{} was FORGIVEN", username);
                it.remove();
            }
        }
    }

    @Getter
    @Setter
    private class LimitInfo {
        private Integer attempts;
        private Instant added;

    }

}