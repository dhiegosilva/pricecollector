package com.selenium.pricecollector.pages;


import com.selenium.pricecollector.helper.GlobalFunctions;
import com.selenium.pricecollector.helper.GlobalVariables;
import com.selenium.pricecollector.helper.driver.MyRemoteWebDriver;
import com.selenium.pricecollector.helper.ticker.XMLimport;
import com.selenium.pricecollector.sql.EntryData;
import com.selenium.pricecollector.sql.EntryDataRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ReiseBank {

    @Autowired
    private EntryDataRepository entryDataRepository;
    @Autowired
    MyRemoteWebDriver reiseBankDriver;
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

            driver = reiseBankDriver.start();

            List<WebElement> pageElements, cookieModal, parentGrid;
            int pageQty = 1;
            String row;
            String[] lines, splitName, sessions;

            sessions = new String[]{"https://www.reisebank.de/gold/goldmuenzen-kaufen?product_list_limit=36",
                    "https://www.reisebank.de/gold/goldbarren-kaufen?product_list_limit=36",
                    "https://www.reisebank.de/silber/silbermuenzen-kaufen?product_list_limit=36",
                    "https://www.reisebank.de/silber/silberbarren-kaufen?product_list_limit=36"};
            //region Gather Data
            for (String session : sessions) {
                driver.navigate().to(session);
                Thread.sleep(5000);
                //close cookie
                if (Objects.equals(session, sessions[0])) {
                    cookieModal = driver.findElements(By.className("amgdprcookie-button"));
                    if (!cookieModal.isEmpty()) {
                        cookieModal.get(0).click();
                        Thread.sleep(5000);
                    }
                }
                if (!driver.findElements(By.className("toolbar-products")).get(1).findElements(By.className("item")).isEmpty()) {
                    pageQty = driver.findElements(By.className("toolbar-products")).get(1).findElements(By.className("item")).size() - 1;
                } else {
                    pageQty = 1;
                }

                for (int i = 0; i < pageQty; i++) {
                    parentGrid = driver.findElements(By.className("product-items"));
                    //Child
                    pageElements = parentGrid.get(1).findElements(By.className("product-item"));

                    for (WebElement pageElement : pageElements) {
                        row = pageElement.getText();
                        lines = row.split("\n");

                        if (lines.length == 6) {

                            switch (session) {
                                case "https://www.reisebank.de/gold/goldmuenzen-kaufen?product_list_limit=36" ->
                                        category.add("Goldmünzen");
                                case "https://www.reisebank.de/gold/goldbarren-kaufen?product_list_limit=36" ->
                                        category.add("Goldbarren");
                                case "https://www.reisebank.de/silber/silbermuenzen-kaufen?product_list_limit=36" ->
                                        category.add("Silbermünzen");
                                case "https://www.reisebank.de/silber/silberbarren-kaufen?product_list_limit=36" ->
                                        category.add("Silberbarren");
                            }

                            splitName = lines[0].split("[-]?[0-9]+[,.]?[0-9]*([\\/][0-9]+[,.]?[0-9]*)*\\s(?:gr?|oz|ounces?|-grm|grams?)\s");
                            if (splitName.length == 2) {
                                articleName.add(splitName[1]
                                        .replace(" - ", " ")
                                        .replace("'", "''")
                                );
                            } else {
                                articleName.add(lines[0]
                                        .replace(" - ", " ")
                                        .replace("'", "''")
                                );
                            }
                            articleNr.add(null);
                            articleWeight.add(lines[2].replace("000 g", " kg"));
                            articleBuyPrice.add(Double.parseDouble(lines[4].substring(0, lines[4].length() - 2)
                                    .replace(".", "")
                                    .replace(",", ".")
                            ));
                            articleSellPrice.add(00.00);
                        }
                    }
                    if (i < pageQty - 1) {

                        try {
                            new Actions(driver).moveToElement(driver.findElement(By.className("pages-item-next"))).build().perform();
                            Thread.sleep(3000);
                            driver.findElement(By.className("pages-item-next")).click();
                        } catch (Exception e) {
                            Thread.sleep(5000);
                            new Actions(driver).moveToElement(driver.findElement(By.className("pages-item-next"))).click().build().perform();
                        }
                    }
                    Thread.sleep(5000);
                }
            }
            //Insert into org.gold.SQL
            xmlimport.xmlReader();
            for (int i = 0; i < articleName.size(); i++) {

                ticker.add(null);
                aufGeld.add(null);
                abSchlag.add(null);
                company.add("ReiseBank");

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
            
            ///////////////////////////////////
            entryDataRepository.deleteByCompanyAndDataCollectionDatetimeIsGreaterThan("ReiseBank", Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            entryDataRepository.saveAll(entryData);

        } catch (Exception e) {
            GlobalVariables.errorCompanyList.add("ReiseBank");
            try {
                GlobalFunctions.createScreenshot("ReiseBank", driver);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
