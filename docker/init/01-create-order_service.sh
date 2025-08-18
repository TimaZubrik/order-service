#!/usr/bin/env bash
set -e


DB="$POSTGRES_DB"
USER="$POSTGRES_USER"


exists=$(psql -U "$USER" -tAc \
  "SELECT 1 FROM pg_database WHERE datname = '$DB'")

if [ "$exists" != "1" ]; then
  echo "Создаём базу $DB владельцем $USER"
  psql -U "$USER" -c "CREATE DATABASE \"$DB\" OWNER \"$USER\""
else
  echo "База $DB уже существует"
fi
