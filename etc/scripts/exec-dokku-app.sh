#!/usr/bin/env bash
# Starts every Java app (same set as exec-app.sh), each one wrapped by
# restart-worker.sh, in dependency-safe stages. Apps of the same stage start in
# parallel; the next stage only starts after every app of the previous one is
# healthy. Output (2>&1) is appended to per-app logs via tee -a and all logs
# are followed live with tail.
# Usage (from the repository root): bash etc/scripts/exec-dokku-app.sh [PORT] [APP]
#   PORT: public (Dokku proxy) port, applied only to the GATEWAY via --server.port.
#         Defaults to $PORT from the environment, then 8080.
#   APP:  accepted for Procfile compatibility ($JAVA_APP_STARTUP) and ignored,
#         since all apps are started.
set -u

# ---- Configuration -------------------------------------------------------
RESTART_WORKER="etc/scripts/restart-worker.sh"
LOG_DIR="${LOG_DIR:-logs}"
JAVA_BIN="${JAVA_BIN:-java}"
HEALTH_HOST="${HEALTH_HOST:-127.0.0.1}"
HEALTH_PATH="${HEALTH_PATH:-/actuator/health}" # appended to each app context path
HEALTH_TIMEOUT_SECONDS="${HEALTH_TIMEOUT_SECONDS:-300}"
HEALTH_INTERVAL_SECONDS="${HEALTH_INTERVAL_SECONDS:-5}"

# Gateway public port (Dokku proxy) and its actuator port (management.server.port).
GATEWAY_PORT="${1:-${PORT:-8080}}"
GATEWAY_MANAGEMENT_PORT="${MANAGEMENT_PORT:-8080}"

# NAME|MODULE|ARTIFACT|PORT|HEALTH_PORT|PASS_PORT_ARG|CONTEXT_PATH
# PASS_PORT_ARG=true adds --server.port=PORT; otherwise the app's default port is used.
APPS=(
    "EUREKA|mhpasswordmanager-service-registry|mhpasswordmanager.serviceregistry|8761|8761|false|"
    "CONFIGSERVER|mhpasswordmanager-config-services|mhpasswordmanager.configservices|8888|8888|false|"
    "GATEWAY|mhpasswordmanager-api-gateway|mhpasswordmanager.apigateway|$GATEWAY_PORT|$GATEWAY_MANAGEMENT_PORT|true|"
    "OAUTH2-SERVER|mhpasswordmanager-oauth2-server|mhpasswordmanager.oauth2server|12010|12010|false|/mypass-manager/auth"
    "USER-API|mhpasswordmanager-user-service|mhpasswordmanager.userservice|12011|12011|false|/mypass-manager/users"
    "PASSWORD-API|mhpasswordmanager-password-service|mhpasswordmanager.passwordservice|12012|12012|false|/mypass-manager/passwords"
    "EMAIL-API|mhpasswordmanager-email-service|mhpasswordmanager.emailservice|12013|12013|false|/mypass-manager/emails"
    "FILE-API|mhpasswordmanager-file-service|mhpasswordmanager.fileservice|12014|12014|false|/mypass-manager/files"
)

# Startup stages, in order. Each entry is a space-separated list of app names.
STAGES=(
    "EUREKA"
    "CONFIGSERVER"
    "GATEWAY"
    "OAUTH2-SERVER USER-API PASSWORD-API EMAIL-API FILE-API"
)
# --------------------------------------------------------------------------

PIDS=()
TAIL_PID=""
LAST_PID=""

log() {
    echo "[DOKKU-APP] $*"
}

# Prints the single application JAR of a module, ignoring sources/javadoc/tests.
find_jar() {
    local module="$1" artifact="$2" candidate
    local -a jars=()
    for candidate in "$module"/target/"$artifact"-*.jar; do
        [ -f "$candidate" ] || continue
        case "$candidate" in
            *-sources.jar|*-javadoc.jar|*-tests.jar) continue ;;
        esac
        jars+=("$candidate")
    done
    if [ "${#jars[@]}" -ne 1 ]; then
        log "Expected exactly 1 JAR for $module/target/$artifact-*.jar, found ${#jars[@]}." >&2
        return 1
    fi
    echo "${jars[0]}"
}

# Prints the APPS entry for a name.
app_entry() {
    local entry
    for entry in "${APPS[@]}"; do
        [ "${entry%%|*}" = "$1" ] && { echo "$entry"; return 0; }
    done
    return 1
}

# Starts one app under restart-worker.sh in background, appending stdout+stderr to its log.
start_app() {
    local name="$1" module="$2" artifact="$3" port="$4" pass_port="$5" jar
    local -a args=(-jar)
    jar="$(find_jar "$module" "$artifact")" || return 1
    args+=("$jar")
    [ "$pass_port" = true ] && args+=("--server.port=$port")

    log "Starting $name on port $port ($jar)"
    PORT="$port" MANAGEMENT_PORT="$GATEWAY_MANAGEMENT_PORT" sh "$RESTART_WORKER" "$JAVA_BIN" "${args[@]}" \
        > >(tee -a "$LOG_DIR/$name.log" >/dev/null) 2>&1 &
    PIDS+=("$!")
    LAST_PID="$!"
}

is_healthy() {
    curl -fsS --max-time 5 "http://$HEALTH_HOST:$1$2$HEALTH_PATH" 2>/dev/null | grep -q '"status" *: *"UP"'
}

# Waits until the app reports UP; fails on timeout or if the worker process died.
wait_healthy() {
    local name="$1" port="$2" context="$3" pid="$4" waited=0
    log "Waiting for $name health check (http://$HEALTH_HOST:$port$context$HEALTH_PATH)..."
    while ! is_healthy "$port" "$context"; do
        if ! kill -0 "$pid" 2>/dev/null; then
            log "$name exited before becoming healthy." >&2
            return 1
        fi
        if [ "$waited" -ge "$HEALTH_TIMEOUT_SECONDS" ]; then
            log "$name not healthy after ${HEALTH_TIMEOUT_SECONDS}s." >&2
            return 1
        fi
        sleep "$HEALTH_INTERVAL_SECONDS"
        waited=$((waited + HEALTH_INTERVAL_SECONDS))
    done
    log "$name is UP."
}

# Starts all apps of a stage in parallel, then waits for all of them to be healthy.
run_stage() {
    local app entry name module artifact port health_port pass_port context
    local -a names=() ports=() contexts=() pids=()
    for app in $1; do
        entry="$(app_entry "$app")" || { log "Unknown app in stage: $app" >&2; return 1; }
        IFS='|' read -r name module artifact port health_port pass_port context <<< "$entry"
        start_app "$name" "$module" "$artifact" "$port" "$pass_port" || return 1
        names+=("$name"); ports+=("$health_port"); contexts+=("$context"); pids+=("$LAST_PID")
    done
    local i
    for i in "${!names[@]}"; do
        wait_healthy "${names[$i]}" "${ports[$i]}" "${contexts[$i]}" "${pids[$i]}" || return 1
    done
}

start_all() {
    local stage
    for stage in "${STAGES[@]}"; do
        log "=== Stage: $stage ==="
        run_stage "$stage" || return 1
    done
}

prepare_logs() {
    local entry
    mkdir -p "$LOG_DIR"
    for entry in "${APPS[@]}"; do
        : >> "$LOG_DIR/${entry%%|*}.log"
    done
}

follow_logs() {
    tail -n 0 -F "$LOG_DIR"/*.log 2>&1 &
    TAIL_PID="$!"
}

# restart-worker.sh forwards TERM to its Java child.
stop_all() {
    local pid exit_code="${1:-143}"
    trap '' INT TERM
    log "Stopping all apps..."
    for pid in "${PIDS[@]}"; do
        kill -TERM "$pid" 2>/dev/null
    done
    wait "${PIDS[@]}" 2>/dev/null
    [ -n "$TAIL_PID" ] && kill "$TAIL_PID" 2>/dev/null
    exit "$exit_code"
}

validate_port() {
    case "$GATEWAY_PORT" in
        ''|*[!0-9]*) log "PORT must be a number between 1 and 65535." >&2; exit 64 ;;
    esac
    if [ "$GATEWAY_PORT" -lt 1 ] || [ "$GATEWAY_PORT" -gt 65535 ]; then
        log "PORT must be a number between 1 and 65535." >&2
        exit 64
    fi
}

main() {
    [ "$#" -le 2 ] || { log "Usage: $0 [PORT] [APP]" >&2; exit 64; }
    validate_port
    [ -f "$RESTART_WORKER" ] || { log "$RESTART_WORKER not found. Run from the repository root." >&2; exit 1; }
    prepare_logs
    trap stop_all INT TERM
    follow_logs
    if ! start_all; then
        log "Startup aborted (see messages above)." >&2
        stop_all 1
    fi

    [ "${#PIDS[@]}" -gt 0 ] && wait "${PIDS[@]}"
    [ -n "$TAIL_PID" ] && kill "$TAIL_PID" 2>/dev/null
    return 0
}

main "$@"

