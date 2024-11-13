package com.selenium.pricecollector.email;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "email")
public class EmailConfigurationProperties {
    private String from, to;
}
