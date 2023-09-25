package com.selenium.pricecollector.helper.driver;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Service
public class MyRemoteWebDriver {

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
            "--user-agent=Chrome/113.0.0.0",
            "--lang=de"
    );

    public RemoteWebDriver start() throws MalformedURLException {
        String url ="http://dxvmli005.degussa.local:4444";
        ChromeOptions param;
        RemoteWebDriver driver;
        param = new ChromeOptions();
        param.addArguments(list);
        driver = new RemoteWebDriver(new URL(url), param);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return driver;
    }


}
