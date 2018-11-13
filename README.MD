# Mastering two way TLS

This tutorial will walk you through the process of protecting your application with TLS authentication and only allowing it for certain users. This means that you can choose which users are allowed to call your application.

This sample project is a basic setup of a server and a client. The communication between the server and client happens through HTTP, so there is no encryption at all. The goal is to ensure all communication happens in a secure way.

These are the following steps:
1. Starting the server
2. Saying hello to the server (without encryption)
3. Enabling HTTPS on the server (one-way TLS)
4. Require the client to identify itself (two way TLS)

## Starting the server
**Required to have:**
1. Java 11
2. Maven
3. Eclipse or Intellij IDEA

Start the server by running the main method of the App class in the server project.

## Saying hello to the server (without encryption)

Currently the server is running on the default port of 8080 without encryption. You can call the hello endpoint with the following curl command in the terminal: 

```bash
curl -i -XGET http://localhost:8080/api/hello
```

It should give you the following response:

```bash
HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8
Content-Length: 7
Date: Sun, 11 Nov 2018 14:21:50 GMT
Hello
```

You can also call the server with the provided client in the client directory. The client is an integration test based on Cucumber and you can start it by running the ClientRunnerIT class. There is a Hello.feature file which describes the steps for the integration test. You can find it in the test resources of the client project.

There is another way to run the server and client and that is with the following command: `mvn clean install`

## Enabling HTTPS on the server (one-way TLS)

Now you will learn how to secure your server by enabling TLS. You can do that by adding the required properties to the application properties file named: `application.yml`

Add the following property:
```yaml
server:
  port: 8443
  ssl:
    enabled: true
```

You will probably ask your self why the port is set to 8443. The port convention for https is 8443 and for http it is 8080. So we could use port 8080 for https connections but it is a bad practise. See [Wikipedia](https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers) for more information about port conventions.

Restart the server so it can apply the changes you made. You will probably get the following exception: `IllegalArgumentException: Resource location must not be null`. You are getting this message because the server requires a keystore with the certificate of the server to ensure a secure connection with the outside world. The server can provide you more information if you provide the following VM argument `-Djavax.net.debug=SSL,keymanager,trustmanager,ssl:handshake`

To solve this issue you are going to create a keystore with a public and private key for the server. The public key will be shared with users so they can encrypt the communication. The communication between the user and server can be decrypted with the private key of the server. Please never share the private key of the server, because others could intercept the communication and will be able to see the content of the encrypted communication.

To create a keystore with a public and private key execute the following command in your terminal:
```bash
keytool -genkeypair -keyalg RSA -keysize 3072 -alias hakan -dname "CN=Altindag,OU=Development,O=Hakan,C=NL" -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -validity 3650 -keystore server/src/main/resources/identity.jks -storepass secret -keypass secret -deststoretype pkcs12
```

Now you need to tell your server where the location is of the keystore and you also need to provide the passwords. Paste the following in your `application.yml` file:
```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: server/src/main/resources/identity.jks
    key-password: secret
    key-store-password: secret
```

Congratulations you enabled TLS encrypted connection between the server and the client! Now you can try to call the server with the following curl command: `curl -i --insecure -v -XGET https://localhost:8443/api/hello` 

Let's also run the client in the ClientRunnerIT class. 

You will see the following error message: `java.net.ConnectException: Connection refused (Connection refused)`.It looks like that the client is trying to say hello to the server but the server is not there. The problem is that the client it trying to say hello to the server on port 8080 while it is active on the port 8443. Apply the following changes to the HelloStepDefs class:

**From**:
```java
private static final String SERVER_URL = "http://localhost:8080";
```
**To**:
```java
private static final String SERVER_URL = "https://localhost:8443";
``` 

## Require the client to identify itself (two way TLS)
The next step is to require the authentication of the client. This will force the client to identify itself and in that way the server can also validate the identity of the client and if it is a trusted one. You can enabled this by telling the server that you also want to validate the client with the property client-auth. Put the following properties in the `application.yml` of the server:
```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: server/src/main/resources/identity.jks
    key-password: secret
    key-store-password: secret
    client-auth: need
```

If you run the client it will fail with the following error message: `javax.net.ssl.SSLHandshakeException: Received fatal alert: bad_certificate`. This indicates that the certificate of the client is not valid because there is no certificate at all. So let's create one with the following command:
```bash
keytool -genkeypair -keyalg RSA -keysize 3072 -alias suleyman -dname "CN=Altindag,OU=Development,O=Suleyman,C=NL" -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -validity 3650 -keystore client/src/test/resources/identity.jks -storepass secret -keypass secret -deststoretype pkcs12
```
You also need to create a truststore. A truststore is a suitcase containing trusted certificates. The client or server will compare the certificate which it will receive during the SSL Handshake process with the content of its truststore. If there is a match then the SSL Handshake process will continue. Before creating the truststores you need to have the certificates of the client and server. You can get it with the following command:

**Export certificate of the client**
```bash
keytool -exportcert -keystore client/src/test/resources/identity.jks -storepass secret -alias suleyman -rfc -file client/src/test/resources/client.cer
```

**Export certificate of the server**
```bash
keytool -exportcert -keystore server/src/main/resources/identity.jks -storepass secret -alias hakan -rfc -file server/src/main/resources/server.cer
```

Now you can create the truststore for the client and import the certificate of the server with the following command:
```bash
keytool -keystore client/src/test/resources/truststore.jks -importcert -file server/src/main/resources/server.cer -alias hakan -storepass secret
```
The next step is to do the same for the truststore of the server:
```bash
keytool -keystore server/src/main/resources/truststore.jks -importcert -file client/src/test/resources/client.cer -alias suleyman -storepass secret
```

You created the two keystores for the client, unfortunately the client is not aware of this. Now you need to tell that it needs to use the keystores with the correct location and password. You also need to tell the client that ssl is enabled. Provide the following property in the `application.yml` file of the client:
```yaml
client:
  ssl:
    enabled: true
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
    key-store: server/src/main/resources/identity.jks
    key-password: secret
    key-store-password: secret
    trust-store: server/src/main/resources/truststore.jks
    trust-store-password: secret
    client-auth: need
```

If you run the client again you will see that the test passed and that the client received the hello message from the server in a secured way!
Congratulations! You finished this tutorial!