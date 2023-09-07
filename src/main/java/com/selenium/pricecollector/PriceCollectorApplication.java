package com.selenium.pricecollector;

import com.selenium.pricecollector.helper.FileConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@AutoConfiguration
@EnableScheduling
@EnableConfigurationProperties(value = {FileConfigurationProperties.class})
@SpringBootApplication
public class PriceCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PriceCollectorApplication.class, args);
    }

}
