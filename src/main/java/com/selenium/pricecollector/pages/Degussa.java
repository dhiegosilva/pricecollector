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
public class Degussa {

    @Autowired
    private EntryDataRepository entryDataRepository;
    @Autowired
    private MyRemoteWebDriver degussaDriver;
    @Autowired
    private XMLimport xmlimport;
    private RemoteWebDriver driver;

    public void run() {

        List<EntryData> entryData = new ArrayList<>();
        List<String> company = new ArrayList<>();
        List<String> articleNr = new ArrayList<>();
        List<String> category = new ArrayList<>();
        List<String> articleName = new ArrayList<>();
        List<String> articleWeight = new ArrayList<>();
        List<Double> articleBuyPrice = new ArrayList<>();
        List<Double> articleSellPrice = new ArrayList<>();
        List<Double> ticker = new ArrayList<>();
        List<Double> aufGeld = new ArrayList<>();
        List<Double> abSchlag = new ArrayList<>();

        try {
            driver = degussaDriver.start();

            WebElement parent;
            List<WebElement> pageElements;

            //region Gather Data
            driver.navigate().to("https://www.degussa-goldhandel.de/preise/preisliste/");
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("CookieBoxSaveButton")));
                Thread.sleep(5000);
                driver.findElement(By.id("CookieBoxSaveButton")).click();
            } catch (Exception e) {
                System.out.println("Degussa Page missing Accept Cookies Modal");
            }

            String[] sessions = new String[]{"tab2", "tab3", "tab4", "tab5"};
            for (String session : sessions) {
                Thread.sleep(2000);
                driver.findElement(By.xpath("//a[contains(@href, '#" + session + "')]")).click();
                //Parent
                parent = driver.findElement(By.id(session));
                //Child
                pageElements = parent.findElements(By.xpath("//div[@id='" + session + "']/table/tbody/tr"));
                String row;
                String[] lines, splitElement;

                for (WebElement pageElement : pageElements) {
                    row = pageElement.getText();
                    lines = row.split("\s");

                    if (lines.length > 7 && lines[2].contains("oz") || lines[2].equals("g") || lines[2].equals("kg")) {

                        splitElement = row.split("\s[-]?[0-9]+[,.]?[0-9]*([\\/][0-9]+[,.]?[0-9]*)*\\s(?:gr?|kg|kilogramms?|oz|ounces?|-grm|gramms?)");
                        if (splitElement.length == 2) {

                            articleWeight.add(row
                                    .replace(splitElement[0], "")
                                    .replace(splitElement[1], "")
                                    .replace("000 g", " kg")
                                    .trim());

                            articleNr.add(lines[0]);

                            if (row.toLowerCase().contains("goldbar")) {
                                category.add("Goldbarren");

                            } else if (row.toLowerCase().contains("goldm")) {
                                category.add("Goldmünzen");

                            } else if (row.toLowerCase().contains("gold")) {
                                category.add("Gold");

                            } else if (row.toLowerCase().contains("silberbar")) {
                                category.add("Silberbarren");

                            } else if (row.toLowerCase().contains("silberm")) {
                                category.add("Silbermünzen");

                            } else if (row.toLowerCase().contains("silber")) {
                                category.add("Silber");

                            } else if (row.toLowerCase().contains("platinbar")) {
                                category.add("Platinbarren");

                            } else if (row.toLowerCase().contains("platinm")) {
                                category.add("Platinmünzen");

                            } else if (row.toLowerCase().contains("platinb")) {
                                category.add("Platinbarren");

                            } else if (row.toLowerCase().contains("palladiumbar")) {
                                category.add("Palladiumbarren");

                            } else if (row.toLowerCase().contains("palladiumm")) {
                                category.add("Palladiummünzen");

                            } else if (row.toLowerCase().contains("palladium")) {
                                category.add("Palladium");
                            }

                            splitElement = row.split("\s[-]?[0-9]+[,.]?[0-9]*([\\/][0-9]+[,.]?[0-9]*)*\\s(?:gr?|kg|kilogramms?|oz|ounces?|-grm|gramms?)");
                            articleName.add(splitElement[1]
                                    .replace(category.get(category.size() - 1), "")
                                    .replace(" - ", " ")
                                    .replace("Degussa", "")
                                    .replace("€", "")
                                    .replace(lines[lines.length - 4], "")
                                    .replace(lines[lines.length - 2], "")
                                    .replace("'", "''")
                                    .trim());

                            if (lines[lines.length - 3].contains("€") && lines[lines.length - 2].contains(",")) {
                                articleSellPrice.add(Double.parseDouble(lines[lines.length - 4]
                                        .replace(".", "")
                                        .replace(",", ".")
                                        .trim()
                                ));
                            } else {
                                articleSellPrice.add(0.00);
                            }

                            if (lines[lines.length - 1].contains("€") && lines[lines.length - 2].contains(",")) {
                                articleBuyPrice.add(Double.parseDouble(lines[lines.length - 2]
                                        .replace(".", "")
                                        .replace(",", ".")
                                        .trim()
                                ));
                            } else {
                                articleBuyPrice.add(0.00);
                            }
                        }
                    }
                }
            }

            //Insert into org.gold.SQL

            ///////////////////////////////////////////
            xmlimport.xmlReader();
            for (int i = 0; i < articleName.size(); i++) {

                ticker.add(null);
                aufGeld.add(null);
                abSchlag.add(null);
                company.add("Degussa");

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
            /////////////////////////////////////////////
            entryDataRepository.deleteByCompanyAndDataCollectionDatetimeAfter("Degussa", Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            entryDataRepository.saveAll(entryData);
        } catch (Exception e) {
            GlobalVariables.errorCompanyList.add("Degussa");
            try {
                GlobalFunctions.createScreenshot("Degussa", driver);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        driver.quit();
    }
}
