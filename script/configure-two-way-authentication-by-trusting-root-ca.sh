#!/usr/bin/env bash

cleanUpExistingCertificatesAndKeystores() {
    echo 'Cleaning up existing certificates and keystores'

    rm -fv client/src/test/resources/client.cer
    rm -fv client/src/test/resources/client.csr
    rm -fv client/src/test/resources/client-signed.cer
    rm -fv client/src/test/resources/identity.jks
    rm -fv client/src/test/resources/truststore.jks
    rm -fv root-ca/identity.jks
    rm -fv root-ca/root-ca.pem
    rm -fv shared-server-resources/src/main/resources/identity.jks
    rm -fv shared-server-resources/src/main/resources/server.cer
    rm -fv shared-server-resources/src/main/resources/server.csr
    rm -fv shared-server-resources/src/main/resources/server-signed.cer
    rm -fv shared-server-resources/src/main/resources/truststore.jks

    echo 'Finished cleanup'
}

createCertificates() {
    echo 'Starting to create certificates...'
    keytool -v -genkeypair -dname "CN=Root-CA,OU=Certificate Authority,O=Thunderberry,C=NL" -keystore root-ca/identity.jks -storepass secret -keypass secret -keyalg RSA -keysize 2048 -alias root-ca -validity 3650 -deststoretype pkcs12 -ext KeyUsage=digitalSignature,keyCertSign -ext BasicConstraints=ca:true,PathLen:3
    keytool -v -genkeypair -dname "CN=Hakan,OU=Amsterdam,O=Thunderberry,C=NL" -keystore shared-server-resources/src/main/resources/identity.jks -storepass secret -keypass secret -keyalg RSA -keysize 2048 -alias server -validity 3650 -deststoretype pkcs12 -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth -ext SubjectAlternativeName:c=DNS:localhost,DNS:raspberrypi.local,IP:127.0.0.1
    keytool -v -genkeypair -dname "CN=$1,OU=Altindag,O=Altindag,C=NL" -keystore client/src/test/resources/identity.jks -storepass secret -keypass secret -keyalg RSA -keysize 2048 -alias client -validity 3650 -deststoretype pkcs12 -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth
    keytool -v -exportcert -file root-ca/root-ca.pem -alias root-ca -keystore root-ca/identity.jks -storepass secret -rfc
    keytool -v -exportcert -file client/src/test/resources/client.cer -alias client -keystore client/src/test/resources/identity.jks -storepass secret -rfc
    keytool -v -exportcert -file shared-server-resources/src/main/resources/server.cer -alias server -keystore shared-server-resources/src/main/resources/identity.jks -storepass secret -rfc
    keytool -v -certreq -file shared-server-resources/src/main/resources/server.csr -keystore shared-server-resources/src/main/resources/identity.jks -alias server -keypass secret -storepass secret -keyalg rsa
    keytool -v -certreq -file client/src/test/resources/client.csr -keystore client/src/test/resources/identity.jks -alias client -keypass secret -storepass secret -keyalg rsa
    keytool -v -importcert -file root-ca/root-ca.pem -alias root-ca -keystore client/src/test/resources/identity.jks -storepass secret -noprompt
    keytool -v -importcert -file root-ca/root-ca.pem -alias root-ca -keystore shared-server-resources/src/main/resources/identity.jks -storepass secret -noprompt
    keytool -v -gencert -infile client/src/test/resources/client.csr -outfile client/src/test/resources/client-signed.cer -keystore root-ca/identity.jks -storepass secret -alias root-ca -validity 3650 -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth -rfc
    keytool -v -gencert -infile shared-server-resources/src/main/resources/server.csr -outfile shared-server-resources/src/main/resources/server-signed.cer -keystore root-ca/identity.jks -storepass secret -alias root-ca -validity 3650 -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth -ext SubjectAlternativeName:c=DNS:localhost,DNS:raspberrypi.local,IP:127.0.0.1 -rfc
    keytool -v -importcert -file client/src/test/resources/client-signed.cer -alias client -keystore client/src/test/resources/identity.jks -storepass secret
    keytool -v -importcert -file shared-server-resources/src/main/resources/server-signed.cer -alias server -keystore shared-server-resources/src/main/resources/identity.jks -storepass secret
    keytool -v -importcert -file root-ca/root-ca.pem -alias root-ca -keystore client/src/test/resources/truststore.jks -storepass secret -noprompt
    keytool -v -importcert -file root-ca/root-ca.pem -alias root-ca -keystore shared-server-resources/src/main/resources/truststore.jks -storepass secret -noprompt
    keytool -v -delete -alias root-ca -keystore client/src/test/resources/identity.jks -storepass secret
    keytool -v -delete -alias root-ca -keystore shared-server-resources/src/main/resources/identity.jks -storepass secret
}

configureApplicationProperties() {
    echo
    echo 'Configuring application properties of the server'
    rm server/src/main/resources/application.yml
    echo -e 'spring:\n  banner:\n    location: classpath:banner.txt\n\nserver:\n  port: 8443\n  ssl:\n    enabled: true\n    key-store: classpath:identity.jks\n    key-password: secret\n    key-store-password: secret\n    trust-store: classpath:truststore.jks\n    trust-store-password: secret\n    client-auth: need'  >> server/src/main/resources/application.yml

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
