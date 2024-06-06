package org.example.ray.circuitbreaker;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhoulei
 * @create 2024/6/5
 * @description:
 */
public class CircuitBreaker {

    private CircuitBreakStatus status;

    /**
     * cnt
     */
    private AtomicInteger      failureCount = new AtomicInteger(0);

    /**
     * cnt
     */
    private AtomicInteger      successCount = new AtomicInteger(0);

    /**
     * failure threshold
     */
    private final int          failureThresholdValue;

    /**
     * success threshold
     */
    private final int          successThresholdValue;

    private final Duration     recoveryTimeout;

    private Instant            lastFailureTime;

    public CircuitBreaker(int failureThresholdValue, int successThresholdValue, Duration recoveryTimeout) {
        this.status = CircuitBreakStatus.CLOSED;
        this.failureThresholdValue = failureThresholdValue;
        this.successThresholdValue = successThresholdValue;
        this.recoveryTimeout = recoveryTimeout;
    }

    /**
     * request permission
     */
    public synchronized boolean requestPermission() {
        if (status == CircuitBreakStatus.OPEN) {
            // judice if the time is out
            if (Duration.between(lastFailureTime, Instant.now()).compareTo(recoveryTimeout) > 0) {
                status = CircuitBreakStatus.HALF_OPEN;
                return true;
            }
        }
        return false;
    }

    /**
     * half open
     */
    public synchronized void successRequest() {
        if (status == CircuitBreakStatus.HALF_OPEN) {
            int successCnt = successCount.incrementAndGet();
            if (successCnt > successThresholdValue) {
                status = CircuitBreakStatus.CLOSED;
            }
        }
        resetCnt();
    }

    public synchronized void failureRequest() {
        int failureCnt = failureCount.incrementAndGet();
        lastFailureTime = Instant.now();
        if (transfer2Open(failureCnt)) {
            status = CircuitBreakStatus.OPEN;
        }
    }

    private void resetCnt() {
        this.successCount = new AtomicInteger(0);
        this.failureCount = new AtomicInteger(0);
    }

    private boolean transfer2Open(Integer failureCnt) {
        return status == CircuitBreakStatus.HALF_OPEN
            || (status == CircuitBreakStatus.CLOSED && failureCnt > failureThresholdValue);
    }

}
