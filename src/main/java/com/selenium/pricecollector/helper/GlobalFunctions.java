package com.selenium.pricecollector.helper;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Component
public class GlobalFunctions {
    @Autowired
    static FileConfigurationProperties fileConfigurationProperties;
    public static void createScreenshot(String company, RemoteWebDriver driver) throws IOException {
    {
        File SrcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path destination = Paths.get(fileConfigurationProperties.getFilePathLinux() + company + ".png");
        Files.move(SrcFile.toPath(), destination, REPLACE_EXISTING);
    }
}}
