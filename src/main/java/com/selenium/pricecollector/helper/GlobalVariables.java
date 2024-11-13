package com.selenium.pricecollector.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class GlobalVariables {
    @Autowired
    FileConfigurationProperties fileConfigurationProperties;
    public static List<String> errorCompanyList = new LinkedList<>();
    public static String filePath;

    public String path() {
        filePath = fileConfigurationProperties.getReportFilesPath();
        return filePath;
    }
}
