package com.selenium.pricecollector.pages;


import com.selenium.pricecollector.helper.FileConfigurationProperties;
import com.selenium.pricecollector.helper.GlobalFunctions;
import com.selenium.pricecollector.helper.GlobalVariables;
import com.selenium.pricecollector.helper.driver.MyRemoteWebDriver;
import com.selenium.pricecollector.helper.ticker.XMLimport;
import com.selenium.pricecollector.sql.EntryDataRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Component
public class ProAurum {
    @Autowired
    private EntryDataRepository entryDataRepository;
    @Autowired
    private MyRemoteWebDriver proAurumDriver;
    @Autowired
    private XMLimport xmlimport;
    @Autowired
    private GlobalFunctions globalFunctions;

    private RemoteWebDriver driver;

    @Autowired
    FileConfigurationProperties fileConfigurationProperties;

    public void run() {

        List<String> articleNr = new LinkedList<>();
        List<String> category = new LinkedList<>();
        List<String> articleName = new LinkedList<>();
        List<String> articleWeight = new LinkedList<>();
        List<Double> articleBuyPrice = new LinkedList<>();
        List<Double> articleSellPrice = new LinkedList<>();

        try {
            driver = proAurumDriver.start();

            List<WebElement> sellBuy, feinGewicht, articleNameElement;

            driver.navigate().to("https://www.proaurum.de/shop/preisliste-onlineshop/");
            Thread.sleep(10000);

            driver.findElement(By.xpath("//button[contains(.,'Ablehnen')]")).click();
            Thread.sleep(10000);
            //Sessions based on Gewicht filter
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.className("Category-productImageWrapper-nhz"), 10));
            Thread.sleep(3000);
            driver.findElement(By.xpath("//label[contains(.,'Nur verfügbare Produkte')]")).click();
            Thread.sleep(2000);

            articleNameElement = driver.findElements(By.xpath("//span[contains(@class,'Category-productName-')]"));
            feinGewicht = driver.findElements(By.xpath("//span[contains(@class,'Category-fineWeightCell-')]"));
            sellBuy = driver.findElements(By.xpath("//div[contains(@class,'buySellSection-price-')]"));

            String[] lines;
            for (int i = 0; i < articleNameElement.size(); i++) {
                articleNr.add(null);
                lines = articleNameElement.get(i).getText().split(" ", 2);
                category.add(lines[0]
                        .replace("münze", "münzen"));

                articleName.add(lines[1]
                        .replace("Gold ", "")
                        .replace("Silber ", "")
                        .replace("Platin ", "")
                        .replace("Palladium ", "")
                        .trim());

                articleWeight.add(feinGewicht.get(i).getText()
                        .replace(",000.00 g", " kg")
                        .replace(".000,00 g", " kg")

                        .replace("31,102 g", "1 oz")
                        .replace("31,103 g", "1 oz")
                        .replace("31,104 g", "1 oz")
                        .replace("31,105 g", "1 oz")
                        .replace("31,106 g", "1 oz")
                        .replace("31,107 g", "1 oz")
                        .replace("31,10 g", "1 oz")
                        .replace("3,11 g", "1/10 oz")
                        .replace("15,552 g", "1/2 oz")
                        .replace("7,776 g", "1/4 oz")
                        .replace("62,207 g", "2 oz")

                        .replace("31.102 g", "1 oz")
                        .replace("31.103 g", "1 oz")
                        .replace("31.104 g", "1 oz")
                        .replace("31.105 g", "1 oz")
                        .replace("31.106 g", "1 oz")
                        .replace("31.107 g", "1 oz")
                        .replace("31.10 g", "1 oz")
                        .replace("3.11 g", "1/10 oz")
                        .replace("15.552 g", "1/2 oz")
                        .replace("7.776 g", "1/4 oz")
                        .replace("62,207 g", "2 oz")

                        .replace(".00 g", " g")
                        .replace(",00 g", " g"));
            }

            for (int i = 0; i < sellBuy.size(); i++) {

                articleSellPrice.add(Double.parseDouble(sellBuy.get(i).getText()
                        .replace("*", "")
                        .replace(".", "")
                        .replace(" €", "")
                        .replace(",", ".")));
                i++;
                articleBuyPrice.add(Double.parseDouble(sellBuy.get(i).getText()
                        .replace("*", "")
                        .replace(".", "")
                        .replace(" €", "")
                        .replace(",", ".")));
            }
            //endregion

            //////////////////////////////////////////////////////////////////////////////
            entryDataRepository.deleteByCompanyAndDataCollectionDatetimeAfter("ProAurum", Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            entryDataRepository.saveAll(globalFunctions.getEntryData("ProAurum", articleName, articleNr, category, articleBuyPrice, articleSellPrice, articleWeight));

        } catch (Throwable e) {
            GlobalVariables.errorCompanyList.add("ProAurum");
            try (FileWriter writer = new FileWriter(new File(fileConfigurationProperties.getReportFilesPath(), "ProAurum.txt"))) {
                for (StackTraceElement stackTrace : e.getStackTrace()) {
                    writer.write(stackTrace.toString() + "\n");
                }
            } catch (IOException ex) {
                System.out.println("An error occurred.");
                throw new RuntimeException(ex);
            }
            try {
                File SrcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Path destination = Paths.get(Path.of(fileConfigurationProperties.getReportFilesPath(), "ProAurum") + ".png");
                System.out.println(destination);
                System.out.println(SrcFile.toPath());
                Files.move(SrcFile.toPath(), destination, REPLACE_EXISTING);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        driver.quit();
    }
}
