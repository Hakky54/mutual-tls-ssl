FROM adoptopenjdk/maven-openjdk17:latest

RUN apt-get update
run apt-get install git -y