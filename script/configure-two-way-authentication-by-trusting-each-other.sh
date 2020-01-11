#!/usr/bin/env bash

cleanUpExistingCertificatesAndKeystores() {
    echo 'Cleaning up existing certificates and keystores'

    rm -fv client/src/test/resources/client.cer
    rm -fv client/src/test/resources/client.csr
    rm -fv client/src/test/resources/client.p12
    rm -fv client/src/test/resources/client-private.key
    rm -fv client/src/test/resources/client-signed.cer
    rm -fv client/src/test/resources/client-signed.p12
    rm -fv client/src/test/resources/identity.jks
    rm -fv client/src/test/resources/truststore.jks
    rm -fv root-ca/root-ca.key
    rm -fv root-ca/root-ca.p12
    rm -fv root-ca/root-ca.pem
    rm -fv root-ca/root-ca.srl
    rm -fv server/src/main/resources/identity.jks
    rm -fv server/src/main/resources/server.cer
    rm -fv server/src/main/resources/server.csr
    rm -fv server/src/main/resources/server.p12
    rm -fv server/src/main/resources/server-private.key
    rm -fv server/src/main/resources/server-signed.cer
    rm -fv server/src/main/resources/server-signed.p12
    rm -fv server/src/main/resources/truststore.jks

    echo 'Finished cleanup'
}

createCertificates() {
    echo 'Starting to create certificates...'

    keytool -genkeypair -keyalg RSA -keysize 2048 -alias server -dname "CN=Hakan,OU=Amsterdam,O=Thunderberry,C=NL" -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -validity 3650 -keystore server/src/main/resources/identity.jks -storepass secret -keypass secret -deststoretype pkcs12
    keytool -genkeypair -keyalg RSA -keysize 2048 -alias client -dname "CN=$1,OU=Altindag,O=Altindag,C=NL" -validity 3650 -keystore client/src/test/resources/identity.jks -storepass secret -keypass secret -deststoretype pkcs12
    keytool -exportcert -keystore client/src/test/resources/identity.jks -storepass secret -alias client -rfc -file client/src/test/resources/client.cer
    keytool -exportcert -keystore server/src/main/resources/identity.jks -storepass secret -alias server -rfc -file server/src/main/resources/server.cer
    keytool -keystore client/src/test/resources/truststore.jks -importcert -file server/src/main/resources/server.cer -alias server -storepass secret -noprompt
    keytool -keystore server/src/main/resources/truststore.jks -importcert -file client/src/test/resources/client.cer -alias client -storepass secret -noprompt
}

configureApplicationProperties() {
    echo
    echo 'Configuring application properties of the server'
    rm server/src/main/resources/application.yml
    echo -e 'spring:\n  banner:\n    location: classpath:banner.txt\n\nserver:\n  port: 8443\n  ssl:\n    enabled: true\n    key-store: classpath:identity.jks\n    key-password: secret\n    key-store-password: secret\n    trust-store: classpath:truststore.jks\n    trust-store-password: secret\n    client-auth: need'  >> server/src/main/resources/application.yml

    echo 'Configuring application properties of the client'
    rm client/src/test/resources/application.yml
    echo -e 'spring:\n  main:\n    banner-mode: "off"\n    web-application-type: none\n\nlogging:\n  level:\n    nl.altindag.client: INFO\n\nclient:\n  ssl:\n    one-way-authentication-enabled: false\n    two-way-authentication-enabled: true\n    key-store: identity.jks\n    key-store-password: secret\n    trust-store: truststore.jks\n    trust-store-password: secret'  >> client/src/test/resources/application.yml
}

configureClientRequestToUseHttps() {
    echo 'Configuring client to send request to HTTPS'
    sed -E -i.bak 's/http:\/\/localhost:8080/https:\/\/localhost:8443/g' client/src/main/java/nl/altindag/client/Constants.java
}

#Validate if provided argument is present
if [[ -z "$1" ]]; then
    echo "No common name is provided to create the Client Certificate"
else
    cleanUpExistingCertificatesAndKeystores
    createCertificates "$1"
    configureApplicationProperties
    configureClientRequestToUseHttps
fi
