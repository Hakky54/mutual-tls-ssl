# Mastering two way TLS

This tutorial will walk you through the process of protecting your application with TLS authentication, only allowing access for certain users. This means that you can choose which users are allowed to call your application.

This sample project demonstrates a basic setup of a server and a client. The communication between the server and client happens through HTTP, so there is no encryption at all. The goal is to ensure that all communication happens in a secure way.

These are the following steps:
1. [Starting the server](#starting-the-server)
2. [Saying hello to the server (without encryption)](#saying-hello-to-the-server-without-encryption)
3. [Enabling HTTPS on the server (one-way TLS)](#enabling-https-on-the-server-one-way-tls)
4. [Require the client to identify itself (two way TLS)](#require-the-client-to-identify-itself-two-way-tls)
5. [Two way TLS based on trusting the Certificate Authority](#two-way-tls-based-on-trusting-the-certificate-authority)
6. [Automated script for two way TLS based on trusting the Certificate Authority](#automated-script-for-two-way-tls-based-on-trusting-the-certificate-authority)

Usefull links
- [Keytool Cheatsheet](https://gist.github.com/Hakky54/7a2f0fcbcf5fdf4674d48f1a0b31c862)
- [Openssl Cheatsheet](https://gist.github.com/webtobesocial/5313b0d7abc25e06c2d78f8b767d4bc3)
- [Spring application properties overview](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)

## Starting the server
**Minimum requirements:**
1. Java 8
2. Maven 3.5.0
3. Eclipse, Intellij IDEA (or any other text editor like VIM)
4. A terminal
5. Openssl (For Windows users use [Git Bash](https://git-scm.com/))

Start the server by running the main method of the [App Class](server/src/main/java/nl/altindag/server/App.java) in the server project or by running the following command from the terminal in the root directory:
```bash
(cd server/ && mvn spring-boot:run)
```

## Saying hello to the server (without encryption)

Currently, the server is running on the default port of 8080 without encryption. You can call the hello endpoint with the following curl command in the terminal:

```bash
curl -i -XGET http://localhost:8080/api/hello
```

It should give you the following response:

```bash
HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8
Content-Length: 5
Date: Sun, 11 Nov 2018 14:21:50 GMT

Hello
```

You can also call the server with the provided client in the client directory. The client is an integration test based on Cucumber, and you can start it by running the ClientRunnerIT class from your IDE. There is a Hello.feature file that describes the steps for the integration test. You can find it in the test resources of the client project. You can also start the client from the command line with the following command: `mvn clean verify -P\!start-server` or `mvn clean verify -P!start-server`.

There is another way to run the server and client and that is with the following command: `mvn clean verify`

## Enabling HTTPS on the server (one-way TLS)

Now, you will learn how to secure your server by enabling TLS. You can do that by adding the required properties to the application properties file named: `application.yml`

Add the following property:
```yaml
server:
  port: 8443
  ssl:
    enabled: true
```

You will probably ask yourself why the port is set to 8443. The port convention for a tomcat server with https is 8443, and for http, it is 8080. So, we could use port 8080 for https connections, but it is a bad practice. See [Wikipedia](https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers) for more information about port conventions.

Restart the server so that it can apply the changes you made. You will probably get the following exception: `IllegalArgumentException: Resource location must not be null`.

You are getting this message because the server requires a keystore with the certificate of the server to ensure that there is a secure connection with the outside world. The server can provide you more information if you provide the following VM argument: `-Djavax.net.debug=SSL,keymanager,trustmanager,ssl:handshake`

To solve this issue, you are going to create a keystore with a public and private key for the server. The public key will be shared with users so that they can encrypt the communication. The communication between the user and server can be decrypted with the private key of the server. Please never share the private key of the server, because others could intercept the communication and will be able to see the content of the encrypted communication.

To create a keystore with a public and private key, execute the following command in your terminal:
```bash
keytool -genkeypair -keyalg RSA -keysize 2048 -alias server -dname "CN=Hakan,OU=Amsterdam,O=Luminis,C=NL" -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -validity 3650 -keystore server/src/main/resources/identity.jks -storepass secret -keypass secret -deststoretype pkcs12
```

Now, you need to tell your server where the location of the keystore is and provide the passwords. Paste the following in your  `application.yml` file:
```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:identity.jks
    key-password: secret
    key-store-password: secret
```

Congratulations! You enabled a TLS-encrypted connection between the server and the client! Now, you can try to call the server with the following curl command: `curl -i --insecure -v -XGET https://localhost:8443/api/hello`

Let's also run the client in the ClientRunnerIT class.

You will see the following error message: `java.net.ConnectException: Connection refused (Connection refused)`. It looks like the client is trying to say hello to the server but the server is not there. The problem is that the client it trying to say hello to the server on port 8080 while it is active on the port 8443. Apply the following changes to the HelloStepDefs class:

**From**:
```java
private static final String SERVER_URL = "http://localhost:8080";
```
**To**:
```java
private static final String SERVER_URL = "https://localhost:8443";
```

## Require the client to identify itself (two way TLS)
The next step is to require the authentication of the client. This will force the client to identify itself, and in that way, the server can also validate the identity of the client and whether or not it is a trusted one. You can enable this by telling the server that you also want to validate the client with the property client-auth. Put the following properties in the `application.yml` of the server:
```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:identity.jks
    key-password: secret
    key-store-password: secret
    client-auth: need
```

If you run the client, it will fail with the following error message: `javax.net.ssl.SSLHandshakeException: Received fatal alert: bad_certificate`. This indicates that the certificate of the client is not valid because there is no certificate at all. So, let's create one with the following command
```bash
keytool -genkeypair -keyalg RSA -keysize 2048 -alias client -dname "CN=Suleyman,OU=Altindag,O=Altindag,C=NL" -validity 3650 -keystore client/src/test/resources/identity.jks -storepass secret -keypass secret -deststoretype pkcs12
```
You also need to create a truststore. A truststore is a suitcase containing trusted certificates. The client or server will compare the certificate, which it will receive during the SSL Handshake process with the content of its truststore. If there is a match, then the SSL Handshake process will continue. Before creating the truststores, you need to have the certificates of the client and server. You can get it with the following command:

**Export certificate of the client**
```bash
keytool -exportcert -keystore client/src/test/resources/identity.jks -storepass secret -alias client -rfc -file client/src/test/resources/client.cer
```

**Export certificate of the server**
```bash
keytool -exportcert -keystore server/src/main/resources/identity.jks -storepass secret -alias server -rfc -file server/src/main/resources/server.cer
```

Now, you can create the truststore for the client and import the certificate of the server with the following command:
```bash
keytool -keystore client/src/test/resources/truststore.jks -importcert -file server/src/main/resources/server.cer -alias server -storepass secret
```
The next step is to do the same for the truststore of the server:
```bash
keytool -keystore server/src/main/resources/truststore.jks -importcert -file client/src/test/resources/client.cer -alias client -storepass secret
```

You created the two keystores for the client. Unfortunately, the client is not aware of this. Now, you need to tell that it needs to use the keystores with the correct location and password. You also need to tell the client that ssl is enabled. Provide the following property in the `application.yml` file of the client:
```yaml
client:
  ssl:
    mutual-authentication-enabled: true
    key-store: identity.jks
    key-password: secret
    key-store-password: secret
    trust-store: truststore.jks
    trust-store-password: secret
```

The server is also not aware of the newly created truststore. Therefore replace the current properties with the following properties:
```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:identity.jks
    key-password: secret
    key-store-password: secret
    trust-store: classpath:truststore.jks
    trust-store-password: secret
    client-auth: need
```

If you run the client again, you will see that the test passed and that the client received the hello message from the server in a secured way. Congratulations! You finished installing two-way TLS!

## Two way TLS based on trusting the Certificate Authority
There is another way to have mutual authentication and that is based on trusting the Certificate Authority. It has pros and cons.

##### Pros
- Client's do not need to add the certificate of the server
- Server does not need to add all the certificates of the clients
- Maintenance will be less because only the Certificate Authority's certificate validity can expire
##### Cons
- You don't have control anymore for which applications are allowed to call your application. You give permission to any application who has a signed certificate by the Certificate Authority.

These are the following steps:
1. [Creating a Certificate Authority](#creating-a-certificate-authority)
2. [Creating a Certificate Signing Request](#creating-a-certificate-signing-request)
3. [Signing the certificate with the Certificate Signing Request](#signing-the-certificate-with-the-certificate-signing-request)
4. [Replace unsigned certificate with a signed one](#replace-unsigned-certificate-with-a-signed-one)
5. [Trusting the Certificate Authority only](#trusting-the-certificate-authority-only)

#### Creating a Certificate Authority
Normally there is already a Certificate Authority and you need to provide your certificate to have it signed. Here you will create your own Certificate Authority and sign the Client and Server certificate with it. To create one you can execute the following command:

```bash
keytool -genkeypair -keyalg RSA -keysize 2048 -alias root-ca -dname "CN=Root-CA,OU=Certificate Authority,O=Luminis,C=NL" -validity 3650 -keystore root-ca/identity.jks -storepass secret -keypass secret -deststoretype pkcs12
```

Or you can use the one which is already provided in the repository, see [identity.jks](root-ca/identity.jks)

#### Creating a Certificate Signing Request
To get your certificate signed you need to provide a Certificate Signing Request (.csr) file. This can be created with the following command:

##### Certificate Signing Request for the server
```bash
keytool -certreq -keystore server/src/main/resources/identity.jks -alias server -keypass secret -storepass secret -keyalg rsa -file server/src/main/resources/server.csr
```

##### Certificate Signing Request for the client
```bash
keytool -certreq -keystore client/src/test/resources/identity.jks -alias client -keypass secret -storepass secret -keyalg rsa -file client/src/test/resources/client.csr
```

The Certificate Authority need these csr files to be able to sign it. The next step will be signing the requests.

#### Signing the certificate with the Certificate Signing Request
Up till now you only used the keytool, but for the signing procedure you need openssl. A csr file can be signed with a pem file and the private key of the Certificate Authority. A pem file is a container format that may include just the public certificate and CA certificate files or may include an entire certificate chain including public key, private key, and root certificates. In this example it will only contain the public certificate. You will extract the pem and key file from the identity.jks with the following commands:

##### Convert java keystore to a p12 file
```bash
keytool -importkeystore -srckeystore root-ca/identity.jks -destkeystore root-ca/root-ca.p12 -srcstoretype jks -deststoretype pkcs12 -srcstorepass secret -deststorepass secret
```

##### Create pem file from a p12 file
```bash
openssl pkcs12 -in root-ca/root-ca.p12 -out root-ca/root-ca.pem -nokeys -passin pass:secret -passout pass:secret
```

##### Create a key file from a p12 file
```bash
openssl pkcs12 -in root-ca/root-ca.p12 -out root-ca/root-ca.key -nocerts -passin pass:secret -passout pass:secret
```

The next step will be signing the certificates. You can sign it with the following commands:
##### Signing the client certificate
```bash
openssl x509 -req -in client/src/test/resources/client.csr -CA root-ca/root-ca.pem -CAkey root-ca/root-ca.key -CAcreateserial -out client/src/test/resources/client-signed.cer -days 1825 -passin pass:secret
```

##### Signing the server certificate
```bash
openssl x509 -req -in server/src/main/resources/server.csr -CA root-ca/root-ca.pem -CAkey root-ca/root-ca.key -CAcreateserial -out server/src/main/resources/server-signed.cer -sha256 -extfile server/src/main/resources/extensions/v3.ext -days 1825 -passin pass:secret
```

#### Replace unsigned certificate with a signed one
The identity keystore of the server and client still have the unsigned certificate. Now you can replaced it with the signed one. It won't be that easy, because the signed certificate does not have the private key so it won't be a valid identity without it. What you need to do is extract the key file from the identity and then merge it back to the identity with the signed certificate. You can do that with the following commands:

##### Client
```bash
keytool -importkeystore -srckeystore client/src/test/resources/identity.jks -destkeystore client/src/test/resources/client.p12 -srcstoretype jks -deststoretype pkcs12 -srcstorepass secret -deststorepass secret
openssl pkcs12 -in client/src/test/resources/client.p12 -nodes -out client/src/test/resources/client-private.key -nocerts -passin pass:secret
openssl pkcs12 -export -in client/src/test/resources/client-signed.cer -inkey client/src/test/resources/client-private.key -out client/src/test/resources/client-signed.p12 -name client -passout pass:secret
keytool -delete -alias client -keystore client/src/test/resources/identity.jks -storepass secret
keytool -importkeystore -srckeystore client/src/test/resources/client-signed.p12 -srcstoretype PKCS12 -destkeystore client/src/test/resources/identity.jks -srcstorepass secret -deststorepass secret
```

##### Server
```bash
keytool -importkeystore -srckeystore server/src/main/resources/identity.jks -destkeystore server/src/main/resources/server.p12 -srcstoretype jks -deststoretype pkcs12 -srcstorepass secret -deststorepass secret
openssl pkcs12 -in server/src/main/resources/server.p12 -nodes -out server/src/main/resources/server-private.key -nocerts -passin pass:secret
openssl pkcs12 -export -in server/src/main/resources/server-signed.cer -inkey server/src/main/resources/server-private.key -out server/src/main/resources/server-signed.p12 -name server -passout pass:secret
keytool -delete -alias server -keystore server/src/main/resources/identity.jks -storepass secret
keytool -importkeystore -srckeystore server/src/main/resources/server-signed.p12 -srcstoretype PKCS12 -destkeystore server/src/main/resources/identity.jks -srcstorepass secret -deststorepass secret
```

#### Trusting the Certificate Authority only
Now you need to configure your client and server to only trust the Certificate Authority. You can do that by importing the certificate of the Certificate Authority into the truststores of the client and server. You can do that with the following two commands:

##### Client
```bash
keytool -keystore client/src/test/resources/truststore.jks -importcert -file root-ca/root-ca.pem -alias root-ca -storepass secret
```

##### Server
```bash
keytool -keystore server/src/main/resources/truststore.jks -importcert -file root-ca/root-ca.pem -alias root-ca -storepass secret
```

The truststores still contains the client and server specific certificates and that needs to be removed. You can do that with the following command:
##### Client
```bash
keytool -keystore client/src/test/resources/truststore.jks -delete -alias server -storepass secret
```

##### Server
```bash
keytool -keystore server/src/main/resources/truststore.jks -delete -alias client -storepass secret
```

If you run the client again, you will see that the test passed and that the client received the hello message from the server while based on a certificate which is signed by the Certificate Authority.

## Automated script for two way TLS based on trusting the Certificate Authority

You can also automate the creation of the client and server certificates including the signing process, updating the application properties and the client configuration with the provided script at the root directory. Run it with the following command: `./configure-mutual-tls my-company-name`
The provided argument will be used as CN (Common Name) when creating the client certificate.
