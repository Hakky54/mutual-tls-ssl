#!/usr/bin/env bash

SECONDS=0

mvn clean verify -P without-authentication && \
mvn clean verify -P with-one-way-authentication && \
mvn clean verify -P with-two-way-authentication-by-trusting-each-other && \
mvn clean verify -P with-two-way-authentication-by-trusting-root-ca

durationInMinutes=$((SECONDS / 60))
remainingDurationInSeconds=$((SECONDS % 60))

printf "\nTest execution finished in %s minutes and %s seconds.\n" "$durationInMinutes" $remainingDurationInSeconds