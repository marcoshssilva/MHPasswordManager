#!/usr/bin/env bash

ensure_dokku_config_keystore() {
    local directory="${CONFIG_CONFIGSERVER_CRYPTO_DIRECTORY:-/app/config-crypto}"
    local keystore="$directory/keystore.p12"
    local lock="$directory/.keystore-generation-lock"
    local temp_directory=""
    local original_umask

    : "${CONFIG_CONFIGSERVER_CRYPTO_PASSWORD:?Set CONFIG_CONFIGSERVER_CRYPTO_PASSWORD in Dokku config}"
    if [ "${#CONFIG_CONFIGSERVER_CRYPTO_PASSWORD}" -lt 6 ]; then
        echo "[DOKKU-APP] CONFIG_CONFIGSERVER_CRYPTO_PASSWORD must contain at least 6 characters." >&2
        return 1
    fi
    command -v keytool >/dev/null 2>&1 || {
        echo "[DOKKU-APP] keytool is required to generate the Config Server keystore." >&2
        return 1
    }

    if [ ! -r /proc/self/mountinfo ] ||
        ! awk -v target="$directory" '$5 == target { found = 1 } END { exit !found }' /proc/self/mountinfo; then
        echo "[DOKKU-APP] Config Server keystore directory is not a mounted persistent volume: $directory" >&2
        return 1
    fi
    [ -w "$directory" ] || {
        echo "[DOKKU-APP] Config Server keystore directory is not writable: $directory" >&2
        return 1
    }

    if [ ! -f "$keystore" ]; then
        mkdir "$lock" 2>/dev/null || {
            echo "[DOKKU-APP] Config Server keystore generation is already in progress." >&2
            return 1
        }
        cleanup_keystore_generation() {
            if [ -n "$temp_directory" ]; then
                rm -f "$temp_directory/keystore.p12"
                rmdir "$temp_directory" 2>/dev/null || true
            fi
            rmdir "$lock" 2>/dev/null || true
        }
        if [ ! -f "$keystore" ]; then
            original_umask="$(umask)"
            umask 077
            temp_directory="$(mktemp -d "$directory/.keystore-generation.XXXXXX")" || {
                umask "$original_umask"
                cleanup_keystore_generation
                return 1
            }
            CONFIG_CONFIGSERVER_CRYPTO_PASSWORD="$CONFIG_CONFIGSERVER_CRYPTO_PASSWORD" keytool -genkeypair \
                -alias config-server \
                -keyalg RSA \
                -keysize 2048 \
                -sigalg SHA256withRSA \
                -validity 3650 \
                -dname "CN=MHPasswordManager Config Server" \
                -storetype PKCS12 \
                -keystore "$temp_directory/keystore.p12" \
                -storepass:env CONFIG_CONFIGSERVER_CRYPTO_PASSWORD \
                -keypass:env CONFIG_CONFIGSERVER_CRYPTO_PASSWORD \
                -noprompt || {
                    umask "$original_umask"
                    cleanup_keystore_generation
                    return 1
                }
            chmod 600 "$temp_directory/keystore.p12" && mv "$temp_directory/keystore.p12" "$keystore" || {
                umask "$original_umask"
                cleanup_keystore_generation
                return 1
            }
            umask "$original_umask"
        fi

        cleanup_keystore_generation
    fi

    [ -r "$keystore" ] || {
        echo "[DOKKU-APP] Config Server keystore is not readable: $keystore" >&2
        return 1
    }

    export -n CONFIG_CONFIGSERVER_CRYPTO_PASSWORD
    CONFIG_CONFIGSERVER_CRYPTO_ENABLED=true
    CONFIG_CONFIGSERVER_CRYPTO_LOCATION="$keystore"
}
