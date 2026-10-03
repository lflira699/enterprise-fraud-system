#!/usr/bin/env bash
set -Eeuo pipefail

: "${POSTGRES_USER:?POSTGRES_USER_REQUIRED}"
: "${POSTGRES_DB:?POSTGRES_DB_REQUIRED}"
: "${DB_USERNAME:?DB_USERNAME_REQUIRED}"
: "${DB_PASSWORD:?DB_PASSWORD_REQUIRED}"

if [ "${DB_USERNAME}" = "${POSTGRES_USER}" ]; then
    echo "Runtime database principal must differ from the migration/owner principal." >&2
    exit 1
fi

psql \
    -v ON_ERROR_STOP=1 \
    --username "${POSTGRES_USER}" \
    --dbname "${POSTGRES_DB}" \
    --set=runtime_user="${DB_USERNAME}" \
    --set=runtime_password="${DB_PASSWORD}" <<'EOSQL'

SELECT format(
    'CREATE ROLE %I LOGIN PASSWORD %L',
    :'runtime_user',
    :'runtime_password'
)
WHERE NOT EXISTS (
    SELECT 1
    FROM pg_roles
    WHERE rolname = :'runtime_user'
)
\gexec

EOSQL