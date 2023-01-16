FROM adoptopenjdk/maven-openjdk11:latest

RUN apt-get update
run apt-get install git -y