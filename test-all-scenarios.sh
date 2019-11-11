#!/usr/bin/env bash

mvn clean verify && \
mvn clean verify -P with-one-way-authentication && \
mvn clean verify -P with-two-way-authentication-by-trusting-each-other && \
mvn clean verify -P with-two-way-authentication-by-trusting-root-ca
