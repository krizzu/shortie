#!/bin/sh

set -e

# run api in background
# todo: make sure to restart if stopped
java -jar /app/shortie.jar &

# detached nginx
exec nginx -g "daemon off;"