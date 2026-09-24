$ErrorActionPreference = "Stop"
foreach ($c in @("java", "mvn", "docker", "curl.exe")) {
    if (-not (Get-Command $c -ErrorAction SilentlyContinue)) { throw "Missing command: $c" }
}
if (-not ((java -version 2>&1 | Select-Object -First 1) -match '"21')) { throw "JDK 21 required" }
docker compose version | Out-Null
Write-Host "PASS: Chapter 8 prerequisites are available."
