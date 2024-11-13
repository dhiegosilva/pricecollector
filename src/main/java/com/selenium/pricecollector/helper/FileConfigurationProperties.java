package com.selenium.pricecollector.helper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URL;

@Data
@ConfigurationProperties(prefix = "file")
public class FileConfigurationProperties {
    private String reportFilesPath;
    private URL urlDriver;
}
