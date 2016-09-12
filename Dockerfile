FROM anapsix/alpine-java:8_server-jre_unlimited
COPY target/jetty-runner.jar /usr/jetty-runner/
COPY target/lib              /usr/jetty-runner/lib
EXPOSE 8080
