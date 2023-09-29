package com.selenium.pricecollector.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GlobalVariables {
    @Autowired
    FileConfigurationProperties fileConfigurationProperties;
    public static List<String> errorCompanyList = new ArrayList<>();
    public static String screenshots;

    public String path() {
        String filePath;
        if (System.getProperty("os.name").contains("Windows")) {
            filePath = System.getProperty("user.home") + fileConfigurationProperties.getFilePathWindows();
        } else { //linux
            filePath = fileConfigurationProperties.getFilePathLinux();
        }
        return filePath;
    }

}
