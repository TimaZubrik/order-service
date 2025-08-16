DO
$do$
BEGIN
   IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'order_service') THEN
      EXECUTE 'CREATE DATABASE order_service OWNER postgres';
   END IF;
END
$do$;