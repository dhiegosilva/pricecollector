FROM eclipse-temurin:21-jre-alpine
MAINTAINER Dhiego Silva
ENV TZ=Europe/Berlin
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/LocalDateTime && echo $TZ > /etc/timezone
RUN mkdir /opt/app
RUN mkdir /priceCollector
WORKDIR /opt/app
COPY target/PriceCollector-0.0.1-SNAPSHOT.jar /opt/app/PriceCollector-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "PriceCollector-0.0.1-SNAPSHOT.jar"]