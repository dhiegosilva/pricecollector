package com.selenium.pricecollector.pages;


import com.selenium.pricecollector.helper.FileConfigurationProperties;
import com.selenium.pricecollector.helper.GlobalFunctions;
import com.selenium.pricecollector.helper.GlobalVariables;
import com.selenium.pricecollector.helper.driver.MyRemoteWebDriver;
import com.selenium.pricecollector.sql.EntryData;
import com.selenium.pricecollector.sql.EntryDataRepository;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Component
public class ReiseBank {

    @Autowired
    private EntryDataRepository entryDataRepository;
    @Autowired
    private MyRemoteWebDriver reiseBankDriver;
    @Autowired
    private GlobalFunctions globalFunctions;

    //    private RemoteWebDriver driver;
    private WebDriver driver;

    @Autowired
    FileConfigurationProperties fileConfigurationProperties;

    public void run() {

        List<EntryData> entryData = new LinkedList<>();

        String company = "ReiseBank", articleNr = null, category = null, articleName = null, articleWeight = null;
        Double articleBuyPrice = 00.00, articleSellPrice = 00.00;

        try {
            driver = reiseBankDriver.start();
//            driver = reiseBankDriver.startWebdriver();

            List<WebElement> pageElements, cookieModal, parentGrid;
            int pageQty;
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
                        cookieModal.getFirst().click();
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
                    pageElements = parentGrid.get(0).findElements(By.className("product-item"));

                    for (WebElement pageElement : pageElements) {
                        row = pageElement.getText();
                        lines = row.split("\n");

                        if (lines.length == 6) {

                            switch (session) {
                                case "https://www.reisebank.de/gold/goldmuenzen-kaufen?product_list_limit=36" ->
                                        category = ("Goldmünzen");
                                case "https://www.reisebank.de/gold/goldbarren-kaufen?product_list_limit=36" ->
                                        category = ("Goldbarren");
                                case "https://www.reisebank.de/silber/silbermuenzen-kaufen?product_list_limit=36" ->
                                        category = ("Silbermünzen");
                                case "https://www.reisebank.de/silber/silberbarren-kaufen?product_list_limit=36" ->
                                        category = ("Silberbarren");
                            }

                            splitName = lines[0].split("[-]?[0-9]+[,.]?[0-9]*([\\/][0-9]+[,.]?[0-9]*)*\\s(?:gr?|oz|ounces?|-grm|grams?)\s");
                            if (splitName.length == 2) {
                                articleName = (splitName[1]
                                        .replace(" - ", " ")
                                        .replace("'", "''")
                                );
                            } else {
                                articleName = (lines[0]
                                        .replace(" - ", " ")
                                        .replace("'", "''")
                                );
                            }
                            articleWeight = (lines[2].replace("000 g", " kg"));
                            articleBuyPrice = (Double.parseDouble(lines[4].substring(0, lines[4].length() - 2)
                                    .replace(".", "")
                                    .replace(",", ".")
                            ));
//                            articleSellPrice=(00.00);
                            entryData.add(globalFunctions.getEntryData(company, articleName, articleNr, category, articleBuyPrice, articleSellPrice, articleWeight));

                        }
                    }

                    //go to next page
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
            entryDataRepository.deleteByCompanyAndDataCollectionDatetimeAfter("ReiseBank", Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            entryDataRepository.saveAll(entryData);

        } catch (Throwable e) {
            GlobalVariables.errorCompanyList.add("ReiseBank");
            try (FileWriter writer = new FileWriter(new File(fileConfigurationProperties.getReportFilesPath(), "ReiseBank.txt"))) {
                for (StackTraceElement stackTrace : e.getStackTrace()) {
                    writer.write(stackTrace.toString() + "\n");
                }
            } catch (IOException ex) {
                System.out.println("An error occurred.");
                throw new RuntimeException(ex);
            }
            try {
                File SrcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Path destination = Paths.get(Path.of(fileConfigurationProperties.getReportFilesPath(), "ReiseBank") + ".png");
                System.out.println(destination);
                System.out.println(SrcFile.toPath());
                Files.move(SrcFile.toPath(), destination, REPLACE_EXISTING);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            driver.quit();
        }
    }
}
