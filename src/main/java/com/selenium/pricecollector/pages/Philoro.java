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
public class Philoro {
    @Autowired
    private EntryDataRepository entryDataRepository;
    @Autowired
    MyRemoteWebDriver philoroDriver;
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
            driver = philoroDriver.start();

            WebElement parent;
            List<WebElement> pageElements;

            //region Gather Data
            driver.navigate().to("https://philoro.de/preisliste");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("onetrust-reject-all-handler")));
            Thread.sleep(5000);
            driver.findElement(By.id("onetrust-reject-all-handler")).click();
            Thread.sleep(5000);
            driver.findElement(By.id("ExternalHideBuyOnly")).click();
            Thread.sleep(10000);

            //Parent
            parent = driver.findElement(By.id("productOverview"));
            //Child
            pageElements = parent.findElements(By.className("philoro_shoping-cart--element"));
            String row;
            String[] lines, splitElement, splitCategory;

            for (WebElement pageElement : pageElements) {
                row = pageElement.getText();
                lines = row.split("\n");

                if (row.contains("\nK")) {

                    //category
                    if (row.toLowerCase().contains("gold bar")) {
                        category.add("Goldbarren");

                    } else if (row.toLowerCase().contains("goldbar")) {
                        category.add("Goldbarren");

                    } else if (row.toLowerCase().contains("gold")) {
                        category.add("Goldmünzen");

                    } else if (row.toLowerCase().contains("silber bar")) {
                        category.add("Silberbarren");

                    } else if (row.toLowerCase().contains("silberbar")) {
                        category.add("Silberbarren");

                    } else if (row.toLowerCase().contains("silber")) {
                        category.add("Silbermünzen");

                    } else if (row.toLowerCase().contains("platinbar")) {
                        category.add("Platinbarren");

                    } else if (row.toLowerCase().contains("platin bar")) {
                        category.add("Platinbarren");

                    } else if (row.toLowerCase().contains("platin")) {
                        category.add("Platinmünzen");

                    } else if (row.toLowerCase().contains("palladium bar")) {
                        category.add("Palladiumbarren");

                    } else if (row.toLowerCase().contains("palladiumbar")) {
                        category.add("Palladiumbarren");

                    } else if (row.toLowerCase().contains("palladium")) {
                        category.add("Palladiummünze");

                    } else if (row.toLowerCase().contains("kupfer bar")) {
                        category.add("Kupferbarren");

                    } else if (row.toLowerCase().contains("kupferbar")) {
                        category.add("Kupferbarren");

                    } else if (row.toLowerCase().contains("kupfer")) {
                        category.add("Kupfermünzen");

                    } else {
                        category.add("");
                    }
                    articleNr.add(lines[0]);

                    int x;
                    if (lines[1].toLowerCase().contains("gold") || lines[1].toLowerCase().contains("sil") || lines[1].toLowerCase().contains("kupf") || lines[1].toLowerCase().contains("plat") || lines[1].toLowerCase().contains("pall")) {
                        x = 1;
                    } else {
                        x = 2;
                    }

                    splitCategory = lines[x].split("\s", 2);
                    splitElement = splitCategory[1].split("\s[-]?[0-9]+[,.]?[0-9]*([\\/][0-9]+[,.]?[0-9]*)*\\s(?:gr?|oz|ounces?|-grm|grams?)");

                    if (splitElement.length > 1) {
                        if (splitElement.length == 1) {
                            articleName.add(splitElement[0] + splitElement[1]
                                    .replace(" - ", " ")
                                    .replace("'", "''")
                                    .replace("000 g", " kg")
                                    .trim()
                            );

                        } else {
                            articleName.add((splitElement[0] + splitElement[1])
                                    .replace(" - ", " ")
                                    .replace("'", "''")
                                    .replace("000 g", " kg")
                                    .trim());
                        }
                    } else {
                        if (splitElement.length == 1) {
                            articleName.add(splitElement[0]
                                    .replace(" - ", " ")
                                    .replace("'", "''")
                                    .replace("000 g", " kg")
                                    .trim()
                            );

                        } else {
                            articleName.add(splitElement[0]
                                    .replace(" - ", " ")
                                    .replace("'", "''")
                                    .replace("000 g", " kg")
                                    .trim());
                        }
                    }

                    articleWeight.add(lines[x + 1]
                            .replace("0,50", "1/2")
                            .replace("0,25", "1/4")
                            .replace("0,10", "1/10")
                            .replace(",00 oz", " oz")
                            .replace(",00 g", " g")
                            .replace("000 g", " kg")
                            .replace("31,10 g", "1 oz")
                    );
                    if (row.contains("\nV\n")) {
                        articleSellPrice.add(Double.parseDouble(lines[lines.length - 4]
                                .substring(2)
                                .replace(".", "")
                                .replace(",", ".")
                                .trim()
                        ));

                    } else {
                        articleSellPrice.add(00.00);
                    }
                    articleBuyPrice.add(Double.parseDouble(lines[lines.length - 2]
                            .substring(2)
                            .replace(".", "")
                            .replace(",", ".")
                            .trim()
                    ));
                }
            }
            //Insert into org.gold.SQL
            xmlimport.xmlReader();
            for (int i = 0; i < articleName.size(); i++) {

                ticker.add(null);
                aufGeld.add(null);
                abSchlag.add(null);
                company.add("Philoro");

                if (category.get(i).toLowerCase().contains("gold")) {
                    if (articleBuyPrice.get(i) != 00.00 && articleBuyPrice.get(i) != null) {
                        switch (articleWeight.get(i)) {
                            case "1 oz":
                                aufGeld.set(i, (100 / (XMLimport.goldTickerValue) * articleBuyPrice.get(i) - 100) / 100);
                                break;
                            case "100 g":
                                aufGeld.set(i, (100 / (XMLimport.goldTickerValue / 31.1035 * 100) * articleBuyPrice.get(i) - 100) / 100);
                                break;
                            case "1 kg":
                                aufGeld.set(i, (100 / (XMLimport.goldTickerValue / 31.1035 * 1000) * articleBuyPrice.get(i) - 100) / 100);
                                break;
                            default:
                                aufGeld.set(i, null);
                                break;
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
            
            //////////////////////////////
            entryDataRepository.deleteByCompanyAndDataCollectionDatetimeAfter("Philoro", Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            entryDataRepository.saveAll(entryData);
        } catch (Exception e) {
            GlobalVariables.errorCompanyList.add("Philoro");
            try {
                GlobalFunctions.createScreenshot("Philoro", driver);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        driver.quit();
    }
}
