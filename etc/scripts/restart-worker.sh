#!/usr/bin/env sh

set -u

if [ "$#" -eq 0 ]; then
  echo "[restart-worker] No command provided."
  exit 64
fi

is_uint() {
  case "$1" in
    ''|*[!0-9]*)
      return 1
      ;;
    *)
      return 0
      ;;
  esac
}

initial_delay="${WORKER_RESTART_INITIAL_DELAY_SECONDS:-15}"
max_delay="${WORKER_RESTART_MAX_DELAY_SECONDS:-60}"
max_attempts="${WORKER_RESTART_MAX_ATTEMPTS:-3}"

if ! is_uint "$initial_delay"; then
  echo "[restart-worker] Invalid WORKER_RESTART_INITIAL_DELAY_SECONDS='$initial_delay'; using 15."
  initial_delay=15
fi

if ! is_uint "$max_delay"; then
  echo "[restart-worker] Invalid WORKER_RESTART_MAX_DELAY_SECONDS='$max_delay'; using 60."
  max_delay=60
fi

if ! is_uint "$max_attempts"; then
  echo "[restart-worker] Invalid WORKER_RESTART_MAX_ATTEMPTS='$max_attempts'; using 3."
  max_attempts=3
fi

if [ "$max_delay" -lt "$initial_delay" ]; then
  max_delay="$initial_delay"
fi

attempt=1
delay="$initial_delay"
child_pid=""

terminate() {
  echo "[restart-worker] Termination requested."
  if [ -n "$child_pid" ]; then
    kill -TERM "$child_pid" 2>/dev/null || true
    wait "$child_pid" 2>/dev/null || true
  fi
  exit 143
}

trap terminate INT TERM

while :; do
  echo "[restart-worker] Starting attempt $attempt: $*"

  "$@" &
  child_pid="$!"
  wait "$child_pid"
  exit_code="$?"
  child_pid=""

  if [ "$exit_code" -eq 0 ]; then
    echo "[restart-worker] Command finished successfully."
    exit 0
  fi

  if [ "$max_attempts" -gt 0 ] && [ "$attempt" -ge "$max_attempts" ]; then
    echo "[restart-worker] Command failed with exit code $exit_code. Maximum attempts reached."
    exit "$exit_code"
  fi

  echo "[restart-worker] Command failed with exit code $exit_code. Restarting in ${delay}s."
  sleep "$delay"

  attempt=$((attempt + 1))
  if [ "$delay" -lt "$max_delay" ]; then
    delay=$((delay * 2))
    if [ "$delay" -gt "$max_delay" ]; then
      delay="$max_delay"
    fi
  fi
done
