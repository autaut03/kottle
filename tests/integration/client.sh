#!/bin/bash

LATEST_LOG_FILE="tests/integration/run/logs/latest.log"
DEBUG_LOG_FILE="tests/integration/run/logs/debug.log"
SUCCESS_MESSAGE="Kottle integration test was successfully loaded"

rm -f "$LATEST_LOG_FILE"

echo "Running client tests..."

! ./gradlew :tests:integration:runClient

if ! grep -q "$SUCCESS_MESSAGE" "$LATEST_LOG_FILE"; then
  echo "Client tests failed. latest.log: "
  echo "$(cat $LATEST_LOG_FILE)"
  echo "debug.log"
  echo "$(cat $DEBUG_LOG_FILE)"

  exit 101
fi

echo "Success! Client tests passed."

exit 0