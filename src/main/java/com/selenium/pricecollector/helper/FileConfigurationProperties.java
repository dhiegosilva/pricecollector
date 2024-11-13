package com.selenium.pricecollector.helper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "file")
public class FileConfigurationProperties {
    private String filePathWindows;
    private String filePathLinux;
    private String urlDriver;

}
