# Final Acceptance Checklist

## Functional

- [x] Healthy gateway routes work (`baseline-account.txt`, `baseline-transfer.txt`).
- [x] Timeout returns an explicit dependency error (`timeout-response.json`).
- [x] Breaker opens at threshold (`circuitbreakers.json`).
- [x] Open calls fail without remote I/O (`open-timing.txt`).
- [x] Half-open recovery closes breaker (`circuit-recovery-events.json`).
- [x] Retry handles one transient failure (`retry-evidence.json`).
- [x] Permanent outcomes are not retried (`retry-evidence.json`).
- [x] Rate limiter returns stable 429 (`rate-limit-response.json`).

## Engineering

- [x] All Maven tests pass (`maven-tests.txt`).
- [x] Names are `accountService` and `transferApi` (`engineering-evidence.txt`).
- [x] Fallback never fabricates financial success (`engineering-evidence.txt`).
- [x] Configuration is externalized (`engineering-evidence.txt`).
- [x] Metrics have bounded labels (`engineering-evidence.txt`).
- [x] Evidence is synthetic and sanitized (`README.md`).
- [x] No hard-coded secret was found in the supplied workspace (`engineering-evidence.txt`).

Note: the no-secret result covers the supplied workspace. Commit-history verification
and a starting commit hash are unavailable because this directory is not a Git working
tree; see `starting-commit.txt`.
