FROM eclipse-temurin:latest
MAINTAINER Dhiego Silva
ENV TZ=Europe/Berlin
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/LocalDateTime && echo $TZ > /etc/timezone
RUN mkdir /opt/app
WORKDIR /opt/app
COPY target/PriceCollector-0.0.1-SNAPSHOT.jar PriceCollector-0.0.1-SNAPSHOT.jar
#COPY target/PriceCollector-0.0.1-SNAPSHOT.jar /opt/app/PriceCollector-0.0.1-SNAPSHOT.jar
#COPY src/main/resources/application.properties /opt/app/application.properties
ENTRYPOINT ["java", "-jar", "PriceCollector-0.0.1-SNAPSHOT.jar"]