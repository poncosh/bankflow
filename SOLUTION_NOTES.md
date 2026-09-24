# Solution Notes

The gateway uses explicit reviewed routes instead of exposing every discovered
service. `lb://account-service` and `lb://transfer-service` defer instance
selection to Spring Cloud LoadBalancer. `StripPrefix=1` converts the stable public
paths to the lab-only downstream fixture paths.

The global filter accepts only a bounded safe correlation-ID alphabet. Missing or
unsafe input is replaced with a UUID. This metadata supports traceability but does
not authorize a request or identify a customer.
