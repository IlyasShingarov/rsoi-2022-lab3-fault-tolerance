package ru.bmstu.gatewayservice.availability;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AvailabilityService {

    @Value("${breaker.threshold}")
    private Integer breakerThreshold;

    private final RestTemplate restTemplate;
    private final AvailabilityProperties properties;

    private enum ServiceStatus {
        OPEN, HALF_OPEN, CLOSED
    }

    @AllArgsConstructor @Getter
    private class ServiceState {
        private int retryCount;
        private ServiceStatus status;

        public void setStatus(ServiceStatus status) {
            this.status = status;
        }

        public void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
            if (retryCount > breakerThreshold) {
                setStatus(ServiceStatus.OPEN);
            }
        }
    }

    private final Map<String, ServiceState> retryMap = new HashMap<>(Map.of(
            "cars", new ServiceState(0, ServiceStatus.CLOSED),
            "rental", new ServiceState(0, ServiceStatus.CLOSED),
            "payments", new ServiceState(0, ServiceStatus.CLOSED)
    ));



    @Scheduled(fixedDelay = 1000)
    private void resetBreaker() {
        retryMap.forEach((name, state) -> {
            if (state.getStatus().equals(ServiceStatus.OPEN)) {
                handleOpenState(name, state);
            }
        });
    }

    private void handleOpenState(String name, ServiceState state) {
        try {
            if (state.retryCount >= breakerThreshold) {
                updateBreakerState(name);
                state.setStatus(ServiceStatus.HALF_OPEN);
            }
        } catch (RuntimeException e) {
            log.error("Get Health error", e);
        }
    }

    private void updateBreakerState(String name) {
        String url = properties.getUrls().get(name);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            retryMap.get(name).setRetryCount(0);
        }
    }

    public boolean checkAvailability(String serviceName) {
        ServiceState state = retryMap.get(serviceName);
//        if (retryMap.get(serviceName).getRetryCount() <= breakerThreshold) {
//            state.setStatus(ServiceStatus.CLOSED);
//        }

        return state.getStatus().equals(ServiceStatus.CLOSED) ||
                state.getStatus().equals(ServiceStatus.HALF_OPEN);
    }

    public void updateErrorCount(String serviceName) {
        ServiceState state = retryMap.get(serviceName);
        switch (state.getStatus()) {
            case HALF_OPEN -> state.setStatus(ServiceStatus.OPEN);
            case CLOSED -> state.setRetryCount(state.getRetryCount() + 1);
        }
    }

    public void setClosed(String serviceName) {
        ServiceState state = retryMap.get(serviceName);
        if (!state.getStatus().equals(ServiceStatus.CLOSED)) {
            state.setStatus(ServiceStatus.CLOSED);
        }
    }

}
