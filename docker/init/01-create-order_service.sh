set -e

if [ -z "$ORDER_DB_NAME" ] || [ -z "$DB_USER" ]; then
  echo "ERROR: ORDER_DB_NAME and DB_USER must be set"
  exit 1
fi

exists=$(psql -U "$DB_USER" -tAc \
  "SELECT 1 FROM pg_database WHERE datname = '$ORDER_DB_NAME'")

if [ "$exists" != "1" ]; then
  echo "Creating database \"$ORDER_DB_NAME\" owned by \"$DB_USER\""
  psql -U "$DB_USER" -c \
    "CREATE DATABASE \"$ORDER_DB_NAME\" OWNER \"$DB_USER\""
else
  echo "Database \"$ORDER_DB_NAME\" already exists, skipping"
fi
