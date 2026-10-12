#!/usr/bin/env sh
set -u

usage() {
    echo "[APP] Usage: $0 PORT [APP] (default: GATEWAY)" >&2
    echo "[APP] Valid apps: GATEWAY, CONFIGSERVER, EMAIL-API, FILE-API, OAUTH2-SERVER, PASSWORD-API, EUREKA, USER-API" >&2
    exit 64
}

[ "$#" -ge 1 ] && [ "$#" -le 2 ] || usage

port="$1"
app="${2:-GATEWAY}"

case "$port" in
    ''|*[!0-9]*)
        echo "[APP] PORT must be a number between 1 and 65535." >&2
        usage
        ;;
esac
if ! [ "$port" -ge 1 ] 2>/dev/null || ! [ "$port" -le 65535 ] 2>/dev/null; then
    echo "[APP] PORT must be a number between 1 and 65535." >&2
    usage
fi

restart=false
case "$app" in
    GATEWAY)
        module=mhpasswordmanager-api-gateway
        artifact=mhpasswordmanager.apigateway
        ;;
    CONFIGSERVER)
        module=mhpasswordmanager-config-services
        artifact=mhpasswordmanager.configservices
        ;;
    EUREKA)
        module=mhpasswordmanager-service-registry
        artifact=mhpasswordmanager.serviceregistry
        ;;
    EMAIL-API)
        module=mhpasswordmanager-email-service
        artifact=mhpasswordmanager.emailservice
        restart=true
        ;;
    FILE-API)
        module=mhpasswordmanager-file-service
        artifact=mhpasswordmanager.fileservice
        restart=true
        ;;
    OAUTH2-SERVER)
        module=mhpasswordmanager-oauth2-server
        artifact=mhpasswordmanager.oauth2server
        restart=true
        ;;
    PASSWORD-API)
        module=mhpasswordmanager-password-service
        artifact=mhpasswordmanager.passwordservice
        restart=true
        ;;
    USER-API)
        module=mhpasswordmanager-user-service
        artifact=mhpasswordmanager.userservice
        restart=true
        ;;
    *)
        echo "[APP] Unknown app: $app" >&2
        usage
        ;;
esac

# Collect only application JARs, regardless of the version produced by Maven.
set --
for candidate in "$module"/target/"$artifact"-*.jar; do
    [ -f "$candidate" ] || continue
    case "$candidate" in
        *-sources.jar|*-javadoc.jar|*-tests.jar) continue ;;
    esac
    set -- "$@" "$candidate"
done

if [ "$#" -eq 0 ]; then
    echo "[APP] No application JAR found matching $module/target/$artifact-*.jar" >&2
    exit 1
fi
if [ "$#" -gt 1 ]; then
    echo "[APP] Multiple application JARs found. Run a clean build before starting:" >&2
    printf '  %s\n' "$@" >&2
    exit 1
fi

jar="$1"
if [ "$restart" = true ]; then
    PORT="$port"
    export PORT
    exec sh etc/scripts/restart-worker.sh java -jar "$jar" "--server.port=$port"
fi

exec java -jar "$jar" "--server.port=$port"
