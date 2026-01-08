#!/usr/bin/env bash
set -euo pipefail

# replace dynamic variables in dashboard/index.html file
export APP_API_PROXY_PORT="${APP_API_PROXY_PORT:-80}"
envsubst '$APP_API_PROXY_PORT' < dashboard/index.html > dashboard/index.out.html
mv dashboard/index.out.html dashboard/index.html

nginx -g "daemon off;" &
NGINX_PID=$!
echo "nginx started in background (pid=$NGINX_PID)"

# Define a function to catch ANY signal and send TERM to Java
terminate() {
  echo "Signal received, terminating api"
  kill -TERM "$KTOR_PID" 2>/dev/null
  # Wait a moment for Ktor to finish
  wait "$KTOR_PID"
  exit 0
}

trap terminate TERM INT QUIT # catch quit and int signals
java -jar /app/shortie.jar &
KTOR_PID=$!

echo "api started (pid=$KTOR_PID)"
wait "$KTOR_PID"
