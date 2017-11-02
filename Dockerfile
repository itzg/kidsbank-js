FROM openjdk:8u141-jre

EXPOSE 8080

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/kidsbank/kidsbank.jar"]

ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/kidsbank/kidsbank.jar