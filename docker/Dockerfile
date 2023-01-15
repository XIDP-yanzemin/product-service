FROM adoptopenjdk/openjdk11
VOLUME /tmp
ADD notification-service-0.0.1-SNAPSHOT-plain.jar product-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/product-service.jar"]
