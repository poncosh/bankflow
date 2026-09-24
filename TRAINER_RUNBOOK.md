# Trainer Runbook

## Before class

1. Verify JDK 21 and Maven dependency access.
2. Run all four Maven test suites.
3. Confirm ports 8761, 8080, 8081, and 8082 are free.
4. Import the Postman collection.
5. Keep starter and completed solution in separate folders.

## Demonstration order

1. Start Eureka, Account Service, Transfer Service, then Gateway.
2. Inspect Eureka and `/actuator/gateway/routes`.
3. Compare a direct call with the public gateway route.
4. Remove `StripPrefix=1`, demonstrate downstream 404, then restore it.
5. Demonstrate generated and preserved correlation IDs.
6. Stop Account Service, record failure, restart it, and prove recovery.

## Guardrails

- Use synthetic `ACC-1001` and `TRF-1001` only.
- Do not recommend discovery-locator routes for public exposure.
- Correlation ID is not authentication or customer identity.
- Chapter 7 observes failures; resilience patterns belong to Chapter 8.
