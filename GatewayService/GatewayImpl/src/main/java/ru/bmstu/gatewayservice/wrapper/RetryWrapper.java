package ru.bmstu.gatewayservice.wrapper;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

@Data
public class RetryWrapper {
    public static final int TIMEOUT = 30;
    private LocalDateTime timoutTimestamp;
    private LocalDateTime lastCall;
    private Supplier<Boolean> runnable;
    private int fullHash;

    public RetryWrapper(Supplier<Boolean> runnable, int fullHash) {
        timoutTimestamp = LocalDateTime.now().plusSeconds(TIMEOUT);
        this.fullHash = fullHash;
        this.runnable = runnable;
    }
}
