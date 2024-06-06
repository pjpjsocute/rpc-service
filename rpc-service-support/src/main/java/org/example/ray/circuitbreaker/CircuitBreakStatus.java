package org.example.ray.circuitbreaker;

/**
 * @author zhoulei
 * @create 2024/6/5
 * @description:
 *        +---------+               +--------+
 *         |         |  (failures)   |        |
 *         | CLOSED  +-------------->+  OPEN  |
 *         |         |               |        |
 *         +----+----+               +---+----+
 *           ^  |                        |   ^
 *  (success)|  | (timeout)              |   |
 *           |  v                        |   |
 *         +----+----+                   |   |(failures)
 *         |         |  (successes)      |   |
 *         | HALF-   +<------------------+   |
 *         |  OPEN   |-----------------------+
 *         |         |
 *         +----+----+
 *
 */
public enum CircuitBreakStatus {

    OPEN("open",1),

    CLOSED("closed",2),

    HALF_OPEN("half_open",3);

    private String status;
    private int code;

    CircuitBreakStatus(String status, int code) {
        this.status = status;
        this.code = code;
    }
}
