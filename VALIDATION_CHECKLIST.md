# Trainer Validation Checklist

- [ ] All four Maven projects compile and tests pass.
- [ ] API-GATEWAY, ACCOUNT-SERVICE, and TRANSFER-SERVICE register in Eureka.
- [ ] Exactly two reviewed business routes are loaded.
- [ ] Both public routes match the direct synthetic results.
- [ ] Unknown route returns 404.
- [ ] Missing StripPrefix produces a controlled downstream failure.
- [ ] Safe correlation ID is preserved and unsafe input is replaced.
- [ ] Stopped service produces an observable failure and later recovers.
- [ ] Verifier passes.
- [ ] Evidence contains no secret or real banking data.
