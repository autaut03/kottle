#!/bin/bash

LATEST_LOG_FILE="tests/integration/run/logs/latest.log"
DEBUG_LOG_FILE="tests/integration/run/logs/debug.log"
SUCCESS_MESSAGE="Kottle integration test was successfully loaded"

rm -f "$LATEST_LOG_FILE"

echo "Running server tests..."

! ./gradlew :tests:integration:runServer

if ! grep -q "$SUCCESS_MESSAGE" "$LATEST_LOG_FILE"; then
  echo "Server tests failed. latest.log: "
#  echo "$(cat $LATEST_LOG_FILE)"
  echo "debug.log"
#  echo "$(cat $DEBUG_LOG_FILE)"

  exit 102
fi

echo "Success! Server tests passed."

exit 0