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
public class GoldSilberShop {
    @Autowired
    private EntryDataRepository entryDataRepository;
    @Autowired
    MyRemoteWebDriver goldSilberShopDriver;
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
            driver = goldSilberShopDriver.start();

            List<WebElement> sell, buy, articleElementName;
            String[] sessions, splitName;

            //Navigate Page
            driver.navigate().to("https://www.goldsilbershop.de/preisliste.html");
            Thread.sleep(5000);

//            List<WebElement> a = driver.findElements(By.id("focus-lock-id"));

            //Cookie
//            driver.get("https://www.goldsilbershop.de/preisliste.html");
//            Thread.sleep(5000);

//            try {
//                WebElement element = driver.findElement(By.cssSelector("#usercentrics-root"));
//                SearchContext context = element.getShadowRoot();
//                WebElement cookieAcceptAll = context.findElement(By.cssSelector("button[data-testid='uc-accept-all-button']"));
//                cookieAcceptAll.click();
//            } catch (Exception e) {
//
//            }
//
//            Thread.sleep(10000);


            sessions = new String[]{"pl_gold", "pl_silver", "pl_platin", "pl_palladium", "pl_copper"};

            //region Gather Data
            for (String session : sessions) {
                driver.findElement(By.id(session)).click();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
                wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.className("mto-pricelist-title"), 3));
//                Thread.sleep(25000);
                Thread.sleep(10000);

                articleElementName = driver.findElements(By.className("mto-pricelist-title"));
                sell = driver.findElements(By.className("mto-pricelist-price-ak"));
                buy = driver.findElements(By.className("mto-pricelist-price-online"));

                for (int i = 0; i < articleElementName.size(); i++) {
                    splitName = articleElementName.get(i).getText().split(" ");

                    if (splitName.length > 2) {
                        if (splitName[1].equals("g") || splitName[1].contains("kg") || splitName[1].toLowerCase().contains("unze") || splitName[1].toLowerCase().contains("gramm")) {
                            if (buy.get(i).getText().contains(" €") && sell.get(i).getText().contains(" €")) {
                                articleNr.add(null);
                                articleName.add(articleElementName.get(i).getText()
                                        .replace(splitName[0] + " " + splitName[1], "")
                                        .replace("Gold ", "")
                                        .replace("Silber ", "")
                                        .replace("Platin ", "")
                                        .replace("Palladium ", "")
                                        .trim());
                                articleWeight.add(splitName[0] + " " + splitName[1]
                                        .replace("Unzen", "oz")
                                        .replace("Unze", "oz")
                                        .replace("31,107 g", "1 oz")
                                        .replace("311,035 g", "10 oz")
                                        .replace("Unzen", "oz"));
                                articleBuyPrice.add(Double.parseDouble(buy.get(i).getText()
                                        .replace(" €", "")
                                        .replace(".", "")
                                        .replace(",", ".")));
                                articleSellPrice.add(Double.parseDouble(sell.get(i).getText()
                                        .replace(" €", "")
                                        .replace(".", "")
                                        .replace(",", ".")));
                                //Gold
                                if (articleElementName.get(i).getText().contains("Gold")) {

                                    if (articleElementName.get(i).getText().contains("münzba") || articleElementName.get(i).getText().contains("barr")) {
                                        category.add("Goldbarren");
                                    } else {
                                        category.add("Goldmünzen");
                                    }
                                }

                                //Silber
                                else if (articleElementName.get(i).getText().contains("Silber")) {

                                    if (articleElementName.get(i).getText().contains("münzb") || articleElementName.get(i).getText().contains("barr")) {
                                        category.add("Silberbarren");
                                    } else {
                                        category.add("Silbermünzen");
                                    }
                                }

                                //Platin
                                else if (articleElementName.get(i).getText().contains("Platin")) {

                                    if (articleElementName.get(i).getText().contains("münzb") || articleElementName.get(i).getText().contains("barr")) {
                                        category.add("Platinbarren");
                                    } else {
                                        category.add("Platinmünzen");
                                    }
                                }

                                //Palladium
                                else if (articleElementName.get(i).getText().contains("Palladium")) {

                                    if (articleElementName.get(i).getText().contains("münzb") || articleElementName.get(i).getText().contains("barr")) {
                                        category.add("Platinbarren");
                                    } else {
                                        category.add("Palladiummünzen");
                                    }
                                }

                                //Kufper
                                else if (articleElementName.get(i).getText().contains("Kupfer")) {

                                    if (articleElementName.get(i).getText().contains("münzb") || articleElementName.get(i).getText().contains("barr")) {
                                        category.add("Kupferbarren");
                                    } else {
                                        category.add("Kupfermünzen");
                                    }
                                } else {
                                    category.add("");
                                }
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
                company.add("GoldSilberShop");

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
            ////////////////////////////////////////////
            entryDataRepository.deleteByCompanyAndDataCollectionDatetimeAfter("GoldSilberShop", Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            entryDataRepository.saveAll(entryData);
        } catch (Exception e) {
            GlobalVariables.errorCompanyList.add("GoldSilberShop");
            try {
                GlobalFunctions.createScreenshot("GoldSilberShop", driver);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        driver.quit();
    }
}
