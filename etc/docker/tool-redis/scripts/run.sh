#!/bin/bash

LOG_LEVEL=${REDIS_LOG_LEVEL:-warning}
REQUIRE_PASS=${REDIS_REQUIRE_PASS:-P@ssword}
SAVE=${REDIS_SAVE:-20 1}
USE_PASS=${REDIS_USE_PASS:-0}

if [ "$USE_PASS" -eq 1 ]; then
    redis-server --save "$SAVE" --loglevel "$LOG_LEVEL" --requirepass "$REQUIRE_PASS"
else
    redis-server --save "$SAVE" --loglevel "$LOG_LEVEL"
fi