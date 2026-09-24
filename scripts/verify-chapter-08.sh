#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "$0")/.." && pwd)"; cd "$root"; failed=0
check(){ if grep -q "$3" "$2"; then echo "PASS: $1"; else echo "FAIL: $1"; failed=1; fi; }
client=transfer-service/src/main/java/com/multimatics/bankflow/transfer/integration/account/RestAccountClient.java
filter=api-gateway/src/main/java/com/multimatics/bankflow/gateway/TransferApiRateLimitFilter.java
check 'Boot 4 resilience dependency' transfer-service/pom.xml 'resilience4j-spring-boot4'
check 'Circuit Breaker annotation' "$client" '@CircuitBreaker'
check 'Retry annotation' "$client" '@Retry'
check 'Safe fallback' "$client" 'Account validation is temporarily unavailable'
check 'HTTP 429 contract' "$filter" 'RATE_LIMIT_EXCEEDED'
check 'Retry-After header' "$filter" 'Retry-After'
for module in service-registry account-service transfer-service notification-service api-gateway; do
  echo "TEST: $module"; (cd "$module" && mvn -q test) || failed=1
done
((failed==0)) || exit 1
echo 'PASS: Chapter 8 structure and Maven tests.'
