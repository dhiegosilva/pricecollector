FROM eclipse-temurin:21-jre-alpine
MAINTAINER Dhiego Silva
ENV TZ=Europe/Berlin
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/LocalDateTime && echo $TZ > /etc/timezone
RUN mkdir /opt/app
RUN mkdir /priceCollector
ENV PC_CSV_PATH_LINUX null
ENV PC_DB_URL null
ENV PC_DB_USER null
ENV PC_DB_PASSWORD null
ENV PC_SMTP_HOST null
ENV PC_SMTP_PORT null
ENV PC_SMTP_USERNAME null
ENV PC_SMTP_PASSWORD null
ENV PC_SMTP_AUTH null
ENV PC_SMTP_STARTTLS null
WORKDIR /opt/app
COPY target/PriceCollector-0.0.1-SNAPSHOT.jar /opt/app/PriceCollector-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "PriceCollector-0.0.1-SNAPSHOT.jar"]