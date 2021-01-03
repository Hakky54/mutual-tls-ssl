# Mastering two way TLS

This hands-on session will walk you through the process of protecting your application with TLS authentication, only allowing access for certain users. This means that you can choose which users are allowed to call your application.

This sample project demonstrates a basic setup of a server and a client. The communication between the server and client happens through HTTP, so there is no encryption at all. The goal is to ensure that all communication happens in a secure way.

These are the following steps:
1. [Starting the server](#starting-the-server)
2. [Saying hello to the server (without encryption)](#saying-hello-to-the-server-without-encryption)
3. [Enabling HTTPS on the server (one-way TLS)](#enabling-https-on-the-server-one-way-tls)
4. [Two way TLS based on trusting the Certificate Authority](#two-way-tls-based-on-trusting-the-certificate-authority)

## Starting the server
**Minimum requirements:**
1. Java 11
2. Maven 3.5.0
3. Eclipse, Intellij IDEA (or any other text editor like VIM)
4. A terminal

Start the server by running the main method of the [App Class](server/src/main/java/nl/altindag/server/App.java) in the server project or by running the following command from the terminal in the root directory:
```bash
(cd server/ && mvn spring-boot:run)
```

## Saying hello to the server (without encryption)

Currently, the server is running on the default port of 8080 without encryption.

### Exercise 1
To test if the server is reachable call it with the command below and validate if you get the same response 
```bash
curl -i -XGET http://localhost:8080/api/hello

HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8
Content-Length: 5
Date: Sun, 11 Nov 2018 14:21:50 GMT

Hello
```

### Exercise 2
Run the Client to validate if it is also able to call the server. The client is an integration test based on Cucumber. There is a Hello.feature file that describes the steps for the integration test.

## Enabling HTTPS on the server (one-way TLS)

### Exercise 3
Now, you will learn how to secure your server by enabling TLS. You need to add some properties to the application properties of the server named `application.yml` and validate if the test still passes.

Here you can find which properties are available and which you can add: [Spring - General application properties](https://docs.spring.io/spring-boot/docs/current/reference/html/howto-properties-and-configuration.html) and [Spring - Security properties](https://docs.spring.io/spring-cloud-dataflow/docs/1.1.0.M1/reference/html/getting-started-security.html). Here you can also find the documentation regarding creating a certificate and how to use the keytool: [Keytool documentation](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/keytool.html) and [Keytool cheat sheet](https://gist.github.com/Hakky54/7a2f0fcbcf5fdf4674d48f1a0b31c862)

Restart the server so that it can apply the changes you made and rerun the client. Make sure that the client is executing the request with the https protocol.

If you get an exception try to add the following VM arguments to see more information: `-Djavax.net.debug=SSL,keymanager,trustmanager,ssl:handshake`

## Two way TLS based on trusting the Certificate Authority

There are two ways to have mutual authentication and that is based on trusting the Certificate Authority or based on trusting a specific server certificate. For this exercise you will be setting up to have it working based on trusting the Certificate Authority. It is good to know that it has pros and cons. 

**Pros**
  - Client's do not need to add the certificate of the server
  - Server does not need to add all the certificates of the clients
  - Maintenance will be less because only the Certificate Authority's certificate validity can expire

**Cons**
  - The server does not have control anymore for which applications are allowed to call him. The server gives permission to any application who has a signed certificate by the Certificate Authority.

### Exercise 4
Now you will try to connect to the server which is running on the Raspberry Pi. The Pi has two way TLS enabled so only clients with a valid certificate, which is signed by the Certificate Authority, can communicate with it. The url of the server is" `https://raspberrypi.local:8443` 

Create a certificate for the client, in the same way you created in the previous exercise, and ensure the client is using that by changing the application properties file of the client. Make sure that the client certificate has a private key and a public key (certificate) and that the output file is a jks (Java Keystore). After that you need the create a Certificate Signing Request based on the newly created client certificate.

Normally there is already a Certificate Authority and you need to provide your certificate signing request to have it signed. It could take couple of hours to get a signed certificate. To have it faster you will be signing your own certificate with a custom Certificate Authority provided in the root-ca directory of this project.

##### Signing the certificate with the Certificate Signing Request
The following commands are given because in a real-life situation you don't need to do the steps of the Certificate Authority.

##### Signing the client certificate 
```bash
keytool -v -gencert -infile client/src/test/resources/client.csr -outfile client/src/test/resources/client-signed.cer -keystore root-ca/identity.jks -storepass secret -alias root-ca -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth
```
Now you have a signed certificate. Your keystore file has an unsigned certificate with the private key. What you need to do is replace the unsigned certificate in the keystore with your signed one. It is required the preserve the private key. 

Run the client en make sure it is getting the Hello message from the Pi. If you are getting a Hello message from the pi you successfully finished this hands-on session and learned how to setup two-way tls authentication, Congratulations!
