#!/usr/bin/env bash
set -euo pipefail

java -jar /app/shortie.jar &
KTOR_PID=$!

nginx -g "daemon off;" &
NGINX_PID=$!

# on sigterm, kill nginx and ktor - forward sigterm
trap 'kill $KTOR_PID $NGINX_PID; wait; exit 0' TERM INT
# wait until any process exits
wait -n "$KTOR_PID" "$NGINX_PID"

# stop other process, ignoring kill errors
kill "$KTOR_PID" "$NGINX_PID" 2>/dev/null || true
wait 2>/dev/null || true


exit 1
