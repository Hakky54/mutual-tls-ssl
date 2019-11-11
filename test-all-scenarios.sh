#!/usr/bin/env bash

mvn clean verify -P'without-authentication,with-one-way-authentication,with-two-way-authentication-by-trusting-each-other,with-two-way-authentication-by-trusting-root-ca'
