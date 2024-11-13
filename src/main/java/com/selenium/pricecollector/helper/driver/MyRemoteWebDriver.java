package com.selenium.pricecollector.helper.driver;

import com.selenium.pricecollector.helper.FileConfigurationProperties;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
public class MyRemoteWebDriver {
    @Autowired
    FileConfigurationProperties fileConfigurationProperties;

    private final List<String> list = Arrays.asList(
            "--blink-settings=imagesEnabled=false",
            "--window-size=1920,1080",
//            "--headless",
            "--disable-extensions",
            "--proxy-server='direct://'",
            "--proxy-bypass-list=*",
            "--start-maximized",
//            "--disable-gpu",
            "--no-sandbox",
            "--disable-browser-side-navigation",
            "--ignore-certificate-errors",
            "--remote-allow-origins=*",
//            "--user-agent=Chrome/119.0.0.0",
            "--lang=de"
    );

    public RemoteWebDriver start() {
        ChromeOptions param = new ChromeOptions();
        param.addArguments(list);
        RemoteWebDriver driver = new RemoteWebDriver(fileConfigurationProperties.getUrlDriver(), param);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return driver;
    }

    //Used to run locally (Need to change from Remote to Webdriver inside the testing Page)
    public WebDriver startWebdriver() {
        ChromeOptions param = new ChromeOptions();
        param.addArguments(list);
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return driver;
    }

}
