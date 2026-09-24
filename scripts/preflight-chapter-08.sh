#!/usr/bin/env bash
set -euo pipefail
for command_name in java mvn docker curl; do command -v "$command_name" >/dev/null || { echo "ERROR: missing $command_name"; exit 1; }; done
java -version 2>&1 | head -1 | grep -q '"21' || { echo 'ERROR: JDK 21 required'; exit 1; }
docker compose version >/dev/null
echo 'PASS: Chapter 8 prerequisites are available.'
