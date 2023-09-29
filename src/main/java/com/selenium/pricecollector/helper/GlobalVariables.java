package com.selenium.pricecollector.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GlobalVariables {
    @Autowired
    static FileConfigurationProperties fileConfigurationProperties;

    public static String path = fileConfigurationProperties.getFilePathLinux();

    public static List<String> errorCompanyList = new ArrayList<>();

}
