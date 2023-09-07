package com.selenium.pricecollector.pages;


import com.selenium.pricecollector.helper.GlobalFunctions;
import com.selenium.pricecollector.helper.GlobalVariables;
import com.selenium.pricecollector.helper.driver.MyRemoteWebDriver;
import com.selenium.pricecollector.helper.ticker.XMLimport;
import com.selenium.pricecollector.sql.EntryData;
import com.selenium.pricecollector.sql.EntryDataRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProAurum {
    @Autowired
    private EntryDataRepository entryDataRepository;
    @Autowired
    MyRemoteWebDriver proAurumDriver;
    @Autowired
    XMLimport xmlimport;
    private RemoteWebDriver driver;
    private List<EntryData> entryData = new ArrayList<>();
    private List<String> company = new ArrayList<>();
    private List<String> articleNr = new ArrayList<>();
    private List<String> category = new ArrayList<>();
    private List<String> articleName = new ArrayList<>();
    private List<String> articleWeight = new ArrayList<>();
    private List<Double> articleBuyPrice = new ArrayList<>();
    private List<Double> articleSellPrice = new ArrayList<>();
    private List<Double> ticker = new ArrayList<>();
    private List<Double> aufGeld = new ArrayList<>();
    private List<Double> abSchlag = new ArrayList<>();

    public void run() {

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

            ////////////////////////// Insert into SQL 
            xmlimport.xmlReader();
            for (int i = 0; i < articleNameElement.size(); i++) {

                ticker.add(null);
                aufGeld.add(null);
                abSchlag.add(null);
                company.add("ProAurum");

                if (category.get(i).toLowerCase().contains("gold")) {
                    if (articleBuyPrice.get(i) != 00.00 && articleBuyPrice.get(i) != null) {
                        switch (articleWeight.get(i)) {
                            case "1 oz" ->
                                    aufGeld.set(i, (100 / (XMLimport.goldTickerValue) * articleBuyPrice.get(i) - 100) / 100);
                            case "100 g" ->
                                    aufGeld.set(i, (100 / (XMLimport.goldTickerValue / 31.1035 * 100) * articleBuyPrice.get(i) - 100) / 100);
                            case "1 kg" ->
                                    aufGeld.set(i, (100 / (XMLimport.goldTickerValue / 31.1035 * 1000) * articleBuyPrice.get(i) - 100) / 100);
                            default -> aufGeld.set(i, null);
                        }
                    }

                    if (articleSellPrice.get(i) != 00.00 && articleSellPrice.get(i) != null) {
                        switch (articleWeight.get(i)) {
                            case "1 oz" ->
                                    abSchlag.set(i, (100 / (XMLimport.goldTickerValue) * articleSellPrice.get(i) - 100) / 100);
                            case "100 g" ->
                                    abSchlag.set(i, (100 / (XMLimport.goldTickerValue / 31.1035 * 100) * articleSellPrice.get(i) - 100) / 100);
                            case "1 kg" ->
                                    abSchlag.set(i, (100 / (XMLimport.goldTickerValue / 31.1035 * 1000) * articleSellPrice.get(i) - 100) / 100);
                            default -> abSchlag.set(i, null);
                        }
                    }

                    ticker.set(i, XMLimport.goldTickerValue);

                } else if (category.get(i).toLowerCase().contains("silber")) {
                    if (articleBuyPrice.get(i) != 00.00 && articleSellPrice.get(i) != null) {

                        switch (articleWeight.get(i)) {
                            case "1 oz" ->
                                    aufGeld.set(i, (100 / (XMLimport.silverTickerValue) * articleBuyPrice.get(i) - 100) / 100);
                            case "1 kg" ->
                                    aufGeld.set(i, (100 / (XMLimport.silverTickerValue / 31.1035 * 1000) * articleBuyPrice.get(i) - 100) / 100);
                            case "5 kg" ->
                                    aufGeld.set(i, (100 / (XMLimport.silverTickerValue / 31.1035 * 1000 * 5) * articleBuyPrice.get(i) - 100) / 100);
                            case "15 kg" ->
                                    aufGeld.set(i, (100 / (XMLimport.silverTickerValue / 31.1035 * 1000 * 15) * articleBuyPrice.get(i) - 100) / 100);
                            default -> aufGeld.set(i, null);
                        }
                    }

                    if (articleSellPrice.get(i) != 00.00 && articleSellPrice.get(i) != null) {

                        switch (articleWeight.get(i)) {
                            case "1 oz" ->
                                    abSchlag.set(i, (100 / (XMLimport.silverTickerValue) * articleSellPrice.get(i) - 100) / 100);
                            case "1 kg" ->
                                    abSchlag.set(i, (100 / (XMLimport.silverTickerValue / 31.1035 * 1000) * articleSellPrice.get(i) - 100) / 100);
                            case "5 kg" ->
                                    abSchlag.set(i, (100 / (XMLimport.silverTickerValue / 31.1035 * 1000 * 5) * articleSellPrice.get(i) - 100) / 100);
                            case "15 kg" ->
                                    abSchlag.set(i, (100 / (XMLimport.silverTickerValue / 31.1035 * 1000 * 15) * articleSellPrice.get(i) - 100) / 100);
                            default -> abSchlag.set(i, null);
                        }
                    }

                    ticker.set(i, XMLimport.silverTickerValue);

                } else if (category.get(i).toLowerCase().contains("pall")) {

                    ticker.set(i, XMLimport.palladiumTickerValue);

                } else if (category.get(i).toLowerCase().contains("plat")) {

                    ticker.set(i, XMLimport.platinumTickerValue);

                }
                if (articleSellPrice.get(i).equals(00.00) || articleSellPrice.get(i) == null) {
                    entryData.add(new EntryData(
                            company.get(i),
                            articleNr.get(i),
                            category.get(i),
                            articleName.get(i),
                            articleWeight.get(i),
                            articleBuyPrice.get(i),
                            ticker.get(i),
                            aufGeld.get(i)
                    ));
                } else {
                    entryData.add(new EntryData(
                            company.get(i),
                            articleNr.get(i),
                            category.get(i),
                            articleName.get(i),
                            articleWeight.get(i),
                            articleBuyPrice.get(i),
                            articleSellPrice.get(i),
                            ticker.get(i),
                            aufGeld.get(i),
                            abSchlag.get(i))
                    );
                }
            }

            //////////////////////////////////////////////////////////////////////////////
            entryDataRepository.deleteByCompanyAndDataCollectionDatetimeIsGreaterThan("ProAurum", Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            entryDataRepository.saveAll(entryData);
        } catch (Exception e) {
            GlobalVariables.errorCompanyList.add("ProAurum");
            try {
                GlobalFunctions.createScreenshot("ProAurum", driver);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        driver.quit();
    }
}
