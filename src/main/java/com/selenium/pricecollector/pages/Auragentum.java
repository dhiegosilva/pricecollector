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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class Auragentum {

    @Autowired
    private EntryDataRepository entryDataRepository;
    @Autowired
    MyRemoteWebDriver auragentumDriver;
    @Autowired
    XMLimport xmlimport;

    RemoteWebDriver driver;
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
            driver = auragentumDriver.start();


            WebElement filterParent;
            int pageQty;
            List<WebElement> templateButtons, templateChild, pageElements;

            String[] sessions = new String[]{
                    "https://auragentum.de/preisliste/listing_template2",
                    "https://auragentum.de/preisliste/listing_template2?p=1&o=8&n=24&min=46999.68&f=349%7C458"
            };

            for (String session : sessions) {
                driver.navigate().to(session);

                //region Gather Data
                Thread.sleep(4000);

                if (session.equals(sessions[0])) {
                    driver.findElement(By.className("cookie-permission--decline-button")).click();
                    Thread.sleep(4000);
                    //Change compact template
                    templateButtons = driver.findElements(By.className("template--switcher"));
                    templateChild = templateButtons.get(0).findElements(By.xpath("child::*"));
                    templateChild.get(2).click();
                    Thread.sleep(4000);
                    driver.findElement(By.xpath("//label[contains(.,'Sofort lieferbar')]")).click();
                    Thread.sleep(4000);
                }

                try {
                    pageQty = Integer.parseInt(driver.findElement(By.className("paging--display")).getText().split("\s")[1]);

                } catch (Exception e) {
                    pageQty = 1;
                }

                for (int a = 0; a < pageQty; a++) {
                    if (a != 0) {
                        driver.findElement(By.className("paging--next")).click();
                        Thread.sleep(3000);
                    }
                    //Parent
                    //Sessions based on Page QTY
                    filterParent = driver.findElement(By.className("listing--container"));
                    pageElements = filterParent.findElements(By.className("box--tablelist"));

                    String row;
                    String[] lines, elementsZero, doubleSell, doubleBuy;
                    for (int i = 0; i < pageElements.size(); i++) {
                        row = pageElements.get(i).getText();
                        lines = row.split("\n");
                        if (lines.length == 6) {
                            //do if is not "Benachrichtigen wenn verfügbar"
                            if (!lines[4].contains("wenn") && !lines[5].contains("wenn") && !lines[1].contains(" x ")) {

                                articleNr.add(lines[0]);
                                if (lines[1].contains("Goldmünz")) {
                                    category.add("Goldmünzen");
                                } else if (lines[1].contains("Goldbar")) {
                                    category.add("Goldbarren");
                                } else if (lines[1].contains("Silbermünz")) {
                                    category.add("Silbermünzen");
                                } else if (lines[1].contains("Silberbar")) {
                                    category.add("Silberbarren");
                                } else if (lines[1].contains("Platinmün")) {
                                    category.add("Platinmünzen");
                                } else if (lines[1].contains("Platinbar")) {
                                    category.add("Platinbarren");
                                } else if (lines[1].contains("Palladiumbar")) {
                                    category.add("Palladiumbarren");
                                } else if (lines[1].contains("Palladiummün")) {
                                    category.add("Palladiummünzen");
                                } else if (lines[1].contains("Kupfermün")) {
                                    category.add("Kupfermünzen");
                                } else if (lines[1].contains("Kupferbar")) {
                                    category.add("Kupferbarren");
                                } else {
                                    category.add("");
                                }

                                elementsZero = lines[1].replace("Unzen", "oz")
                                        .replace("Unze", "oz")
                                        .replace("Gramm", "g")
                                        .replace("Kilogramm", "kg")
                                        .split("\sGoldmünze|\sGoldbarren|\sSilbermünze|\sSilberbarren|\sPlatinmünze|\sPlatinbarren|\sPalladiummünze|\sPalladiumbarren|\sKupfermünze|\sKupferbarren", 2);

                                if (elementsZero.length == 2) {
                                    if (elementsZero[1].trim().isEmpty()) {
                                        articleName.add(category.get(i)
                                                .replace(" - ", " ")
                                                .replace("'", "''")
                                                .trim()
                                        );
                                    } else {
                                        articleName.add(elementsZero[1]
                                                .replace(" - ", " ")
                                                .replace("'", "''")
                                                .trim()
                                        );
                                    }
                                    articleWeight.add(elementsZero[0]
                                            .replace(".", "")
                                            .replace("000 g", " kg")
                                            .replace("31,1 g", "1 oz")
                                    );
                                } else {
                                    elementsZero = elementsZero[0].trim().split("\s", 3);
                                    articleName.add(elementsZero[2]
                                            .replace("'", "''")
                                            .trim()
                                    );
                                    articleWeight.add(elementsZero[0].trim() + " " + elementsZero[1]);
                                }

                                doubleSell = lines[2].split("\s");
                                doubleBuy = lines[4].split("\s");

                                articleSellPrice.add(Double.parseDouble(doubleSell[0]
                                        .replace(".", "")
                                        .replace(",", ".")
                                ));
                                articleBuyPrice.add(Double.parseDouble(doubleBuy[0]
                                        .replace(".", "")
                                        .replace(",", ".")
                                ));
                            }
                        }
                    }
                }
            }

            //Insert into org.gold.SQL
            xmlimport.xmlReader();
            for (int i = 0; i < articleName.size(); i++) {

                ticker.add(null);
                aufGeld.add(null);
                abSchlag.add(null);
                company.add("Auragentum");

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
            
            ///////////////////////////////////////////////////////////////////////
            entryDataRepository.deleteByCompanyAndDataCollectionDatetimeAfter("Auragentum", Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            entryDataRepository.saveAll(entryData);
        } catch (
                Exception e) {
            GlobalVariables.errorCompanyList.add("Auragentum");
            try {
                GlobalFunctions.createScreenshot("Auragentum", driver);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        driver.quit();
    }
}
