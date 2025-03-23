package com.project.dadn.utlls;

import com.project.dadn.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledTasks {

    private final TokenUtil tokenUtil;

    @Scheduled(cron = "0 0 0 * * *")
    public void removeExpiredTokens() {
        tokenUtil.removeNearExpiryToken();
    }

}
