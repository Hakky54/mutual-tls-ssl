#!/usr/bin/env bash

cleanUpExistingCertificatesAndKeystores() {
    echo 'Cleaning up existing certificates and keystores'

    rm -fv client/src/test/resources/truststore.jks
    rm -fv server/src/main/resources/identity.jks
    rm -fv server/src/main/resources/server.cer

    echo 'Finished cleanup'
}

createCertificates() {
    echo 'Starting to create certificates...'

    keytool -genkeypair -keyalg RSA -keysize 2048 -alias server -dname "CN=Hakan,OU=Amsterdam,O=Luminis,C=NL" -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -validity 3650 -keystore server/src/main/resources/identity.jks -storepass secret -keypass secret -deststoretype pkcs12
    keytool -exportcert -keystore server/src/main/resources/identity.jks -storepass secret -alias server -rfc -file server/src/main/resources/server.cer
    keytool -keystore client/src/test/resources/truststore.jks -importcert -file server/src/main/resources/server.cer -alias server -storepass secret -noprompt
}

configureApplicationProperties() {
    echo
    echo 'Configuring application properties of the server'
    rm server/src/main/resources/application.yml
    echo -e 'spring:\n  banner:\n    location: classpath:banner.txt\n\nserver:\n  port: 8443\n  ssl:\n    enabled: true\n    key-store: classpath:identity.jks\n    key-password: secret\n    key-store-password: secret'  >> server/src/main/resources/application.yml

    echo 'Configuring application properties of the client'
    rm client/src/test/resources/application.yml
    echo -e 'spring:\n  main:\n    banner-mode: "off"\n\nclient:\n  ssl:\n    one-way-authentication-enabled: true\n    two-way-authentication-enabled: false\n    trust-store: truststore.jks\n    trust-store-password: secret'  >> client/src/test/resources/application.yml
}

configureClientRequestToUseHttps() {
    echo 'Configuring client to send request to HTTPS'
    sed -E -i.bak 's/http:\/\/localhost:8080/https:\/\/localhost:8443/g' client/src/main/java/nl/altindag/client/stepdefs/HelloStepDefs.java
}

cleanUpExistingCertificatesAndKeystores
createCertificates
configureApplicationProperties
configureClientRequestToUseHttps
