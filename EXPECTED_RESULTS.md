# Expected Results

| Check | Expected |
|---|---|
| Gateway health | HTTP 200 / `UP` |
| Gateway registration | `API-GATEWAY` visible in Eureka |
| Account public route | HTTP 200 through port 8080 |
| Transfer public route | HTTP 200 through port 8080 |
| Unknown route | HTTP 404 and not forwarded |
| Account service stopped | Observable downstream failure, commonly HTTP 503 |
| Account service recovered | Same public URL returns HTTP 200 |
| Missing correlation ID | Gateway generates an ID |
| Safe correlation ID | Gateway preserves the supplied ID |
| Sensitive test strings | Must not appear in evidence |
