$ErrorActionPreference = "Continue"; $root = Split-Path $PSScriptRoot -Parent; Set-Location $root; $failed = $false
function Check($label,$path,$pattern) { if (Select-String -Path $path -Pattern $pattern -Quiet) { Write-Host "PASS: $label" } else { Write-Host "FAIL: $label"; $script:failed=$true } }
$client="transfer-service/src/main/java/com/multimatics/bankflow/transfer/integration/account/RestAccountClient.java"
$filter="api-gateway/src/main/java/com/multimatics/bankflow/gateway/TransferApiRateLimitFilter.java"
Check "Boot 4 resilience dependency" "transfer-service/pom.xml" "resilience4j-spring-boot4"
Check "Circuit Breaker" $client "@CircuitBreaker"; Check "Retry" $client "@Retry"
Check "Safe fallback" $client "Account validation is temporarily unavailable"
Check "HTTP 429" $filter "RATE_LIMIT_EXCEEDED"; Check "Retry-After" $filter "Retry-After"
foreach($m in @("service-registry","account-service","transfer-service","notification-service","api-gateway")) { Push-Location $m; mvn -q test; if($LASTEXITCODE-ne 0){$failed=$true}; Pop-Location }
if($failed){exit 1}; Write-Host "PASS: Chapter 8 structure and Maven tests."
