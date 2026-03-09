#!/bin/sh
set -e

# Ищем JAR, исключая те, что заканчиваются на .original
JAR=$(ls /app/target/*.jar 2>/dev/null | grep -v ".original" | head -n 1)

if [ -z "$JAR" ]; then
  echo "No executable jar found in /app/target"
  exit 1
fi

echo "Starting application: $JAR"
exec java -jar "$JAR" "$@"

