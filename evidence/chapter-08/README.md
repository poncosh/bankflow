# Chapter 8 Evidence

Evidence captured on 2026-09-24 (Asia/Jakarta) from synthetic test data only.
Local filesystem paths, host addresses, correlation IDs, and credentials are omitted.

The healthy baseline uses the running API Gateway on `localhost:8080`. Timeout,
circuit-breaker, recovery, and retry classification use isolated Transfer Service
instances plus deterministic synthetic faults, so the two user-run instances remain
available. The isolated instances were not added to the production gateway route.

See `acceptance-checklist.md` for the result and the individual response, timing,
health, test, configuration, and scan files for supporting evidence.
