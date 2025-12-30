#!/bin/sh
set -e
# substitute env variables
envsubst < env.template.js > dist/env.js

