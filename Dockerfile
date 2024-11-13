# Use GraalVM JDK 21 base image (JRE mode)
FROM eclipse-temurin:21-jre-alpine

# Set timezone to Europe/Berlin
ENV TZ=Europe/Berlin
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Create necessary directories
RUN mkdir /opt/app
RUN mkdir /priceCollector

# Environment variables for PriceCollector application
ENV PC_REPORT_FILES_PATH_LINUX null
ENV PC_DB_URL null
ENV PC_DB_USER null
ENV PC_DB_PASSWORD null
ENV PC_EMAIL_TO null
ENV PC_EMAIL_FROM null
ENV PC_SMTP_HOST null
ENV PC_SMTP_PORT null
ENV PC_SMTP_USERNAME null
ENV PC_SMTP_PASSWORD null
ENV PC_SMTP_AUTH null
ENV PC_SMTP_STARTTLS null
ENV PC_URL_DRIVER null

# Set working directory
WORKDIR /opt/app

# Copy application JAR file
COPY target/PriceCollector-0.0.1-SNAPSHOT.jar /opt/app/PriceCollector-0.0.1-SNAPSHOT.jar

# Run the application with GraalVM optimizations
ENTRYPOINT exec java -jar PriceCollector-0.0.1-SNAPSHOT.jar