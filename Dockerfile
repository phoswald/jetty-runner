FROM openjdk:8-jre-alpine
COPY target/jetty-runner.jar /usr/local/jetty-runner/
COPY target/lib              /usr/local/jetty-runner/lib
EXPOSE 8080
WORKDIR /usr/local/jetty-runner
CMD exec java -jar jetty-runner.jar /usr/local/webapp/*.war
