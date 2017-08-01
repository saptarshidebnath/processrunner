#!/usr/bin/env bash

set -x

./gradlew clean check
__exit_code=$?
if [[ ${__exit_code} -eq 0 ]]; then
    echo "Showing test coverage"
    google-chrome ./build/reports/coverage/index.html &
else
    echo "Test cases failed. Showing Test results"
    google-chrome ./build/reports/tests/test/index.html
fi
