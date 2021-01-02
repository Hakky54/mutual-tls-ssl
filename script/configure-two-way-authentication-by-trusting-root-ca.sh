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
    rm -fv root-ca/identity.jks
    rm -fv root-ca/root-ca.key
    rm -fv root-ca/root-ca.p12
    rm -fv root-ca/root-ca.pem
    rm -fv root-ca/root-ca.srl
    rm -fv shared-server-resources/src/main/resources/identity.jks
    rm -fv shared-server-resources/src/main/resources/server.cer
    rm -fv shared-server-resources/src/main/resources/server.csr
    rm -fv shared-server-resources/src/main/resources/server.p12
    rm -fv shared-server-resources/src/main/resources/server-private.key
    rm -fv shared-server-resources/src/main/resources/server-signed.cer
    rm -fv shared-server-resources/src/main/resources/server-signed.p12
    rm -fv shared-server-resources/src/main/resources/truststore.jks

    echo 'Finished cleanup'
}

createCertificates() {
    echo 'Starting to create certificates...'
    keytool -genkeypair -keyalg RSA -keysize 2048 -alias root-ca -dname "CN=Root-CA,OU=Certificate Authority,O=Thunderberry,C=NL" -validity 3650 -keystore root-ca/identity.jks -storepass secret -keypass secret -deststoretype pkcs12 -ext KeyUsage=digitalSignature,keyCertSign -ext BasicConstraints=ca:true,PathLen:3
    keytool -genkeypair -keyalg RSA -keysize 2048 -alias server -dname "CN=Hakan,OU=Amsterdam,O=Thunderberry,C=NL" -validity 3650 -keystore shared-server-resources/src/main/resources/identity.jks -storepass secret -keypass secret -deststoretype pkcs12 -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth -ext SubjectAlternativeName:c=DNS:localhost,DNS:raspberrypi.local,IP:127.0.0.1
    keytool -genkeypair -keyalg RSA -keysize 2048 -alias client -dname "CN=$1,OU=Altindag,O=Altindag,C=NL" -validity 3650 -keystore client/src/test/resources/identity.jks -storepass secret -keypass secret -deststoretype pkcs12 -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth
    keytool -exportcert -keystore root-ca/identity.jks -storepass secret -alias root-ca -rfc -file root-ca/root-ca.pem
    keytool -exportcert -keystore client/src/test/resources/identity.jks -storepass secret -alias client -rfc -file client/src/test/resources/client.cer
    keytool -exportcert -keystore shared-server-resources/src/main/resources/identity.jks -storepass secret -alias server -rfc -file shared-server-resources/src/main/resources/server.cer
    keytool -certreq -keystore shared-server-resources/src/main/resources/identity.jks -alias server -keypass secret -storepass secret -keyalg rsa -file shared-server-resources/src/main/resources/server.csr
    keytool -certreq -keystore client/src/test/resources/identity.jks -alias client -keypass secret -storepass secret -keyalg rsa -file client/src/test/resources/client.csr
    keytool -keystore client/src/test/resources/identity.jks -importcert -file root-ca/root-ca.pem -alias root-ca -storepass secret -noprompt
    keytool -keystore shared-server-resources/src/main/resources/identity.jks -importcert -file root-ca/root-ca.pem -alias root-ca -storepass secret -noprompt
    keytool -gencert -keystore root-ca/identity.jks -storepass secret -alias root-ca -infile client/src/test/resources/client.csr -outfile client/src/test/resources/client-signed.cer -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth
    keytool -gencert -keystore root-ca/identity.jks -storepass secret -alias root-ca -infile shared-server-resources/src/main/resources/server.csr -outfile shared-server-resources/src/main/resources/server-signed.cer -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth -ext SubjectAlternativeName:c=DNS:localhost,DNS:raspberrypi.local,IP:127.0.0.1
    keytool -importcert -keystore client/src/test/resources/identity.jks -storepass secret -file client/src/test/resources/client-signed.cer -alias client
    keytool -importcert -keystore shared-server-resources/src/main/resources/identity.jks -storepass secret -file shared-server-resources/src/main/resources/server-signed.cer -alias server
    keytool -keystore client/src/test/resources/truststore.jks -importcert -file root-ca/root-ca.pem -alias root-ca -storepass secret -noprompt
    keytool -keystore shared-server-resources/src/main/resources/truststore.jks -importcert -file root-ca/root-ca.pem -alias root-ca -storepass secret -noprompt
    keytool -delete -keystore shared-server-resources/src/main/resources/identity.jks -alias root-ca -storepass secret
    keytool -delete -keystore client/src/test/resources/identity.jks -alias root-ca -storepass secret
}

configureApplicationProperties() {
    echo
    echo 'Configuring application properties of the server'
    rm server-with-spring-boot/src/main/resources/application.yml
    echo -e 'spring:\n  banner:\n    location: classpath:banner.txt\n\nserver:\n  port: 8443\n  ssl:\n    enabled: true\n    key-store: classpath:identity.jks\n    key-password: secret\n    key-store-password: secret\n    trust-store: classpath:truststore.jks\n    trust-store-password: secret\n    client-auth: need'  >> server-with-spring-boot/src/main/resources/application.yml

    echo 'Configuring application properties of the client'
    rm client/src/test/resources/application.yml
    echo -e 'spring:\n  main:\n    banner-mode: "off"\n    web-application-type: none\n\nlogging:\n  level:\n    nl.altindag.sslcontext: INFO\n\nclient:\n  ssl:\n    one-way-authentication-enabled: false\n    two-way-authentication-enabled: true\n    key-store: identity.jks\n    key-store-password: secret\n    trust-store: truststore.jks\n    trust-store-password: secret'  >> client/src/test/resources/application.yml
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
