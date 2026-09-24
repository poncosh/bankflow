# BankFlow Chapter 8 Student Starter

Starting tag: `bankflow-ch07-completed`. Target tag: `bankflow-ch08-completed`.

Use Java 21, Spring Boot 4.0.8, Spring Cloud 2025.1.3, Kafka 4.1.2, and the pinned
Resilience4j 2.4.0 Boot 4 integration. Complete every `TODO CH08`, then run the
Chapter 8 verifier. Use only synthetic IDs `ACC-1001` and `TRF-1001`.

Fault controls under `/internal/lab/faults` are training-only. Never copy them
to a production service. A fallback must never invent balance, approval, account
state, or transfer status `COMPLETED`.
