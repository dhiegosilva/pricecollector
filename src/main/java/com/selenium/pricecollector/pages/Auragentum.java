package com.selenium.pricecollector.pages;

import com.selenium.pricecollector.helper.FileConfigurationProperties;
import com.selenium.pricecollector.helper.GlobalFunctions;
import com.selenium.pricecollector.helper.GlobalVariables;
import com.selenium.pricecollector.helper.driver.MyRemoteWebDriver;
import com.selenium.pricecollector.sql.EntryData;
import com.selenium.pricecollector.sql.EntryDataRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
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

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Component
public class Auragentum {

    @Autowired
    private EntryDataRepository entryDataRepository;
    @Autowired
    private MyRemoteWebDriver auragentumDriver;
    @Autowired
    private GlobalFunctions globalFunctions;

    private RemoteWebDriver driver;

    @Autowired
    FileConfigurationProperties fileConfigurationProperties;

    public void run() {

        List<EntryData> entryData = new LinkedList<>();

        String company = "Auragentum", articleNr = null, category = null, articleName = null, articleWeight = null;
        Double articleBuyPrice = 00.00, articleSellPrice = 00.00;

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
                Thread.sleep(6000);

                if (session.equals(sessions[0])) {
                    driver.findElement(By.className("cookie-permission--decline-button")).click();
                    Thread.sleep(6000);
                    //Change compact template
                    templateButtons = driver.findElements(By.className("template--switcher"));
                    templateChild = templateButtons.getFirst().findElements(By.xpath("child::*"));
                    templateChild.get(2).click();
                    Thread.sleep(6000);
                    driver.findElement(By.xpath("//label[contains(.,'Sofort lieferbar')]")).click();
                    Thread.sleep(6000);
                }

                try {
                    pageQty = Integer.parseInt(driver.findElement(By.className("paging--display")).getText().split("\s")[1]);

                } catch (Exception e) {
                    pageQty = 1;
                }

                for (int a = 0; a < pageQty; a++) {
                    if (a != 0) {
                        driver.findElement(By.className("paging--next")).click();
                        Thread.sleep(5000);
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

                                articleNr = (lines[0]);
                                if (lines[1].contains("Goldmünz")) {
                                    category = ("Goldmünzen");
                                } else if (lines[1].contains("Goldbar")) {
                                    category = ("Goldbarren");
                                } else if (lines[1].contains("Silbermünz")) {
                                    category = ("Silbermünzen");
                                } else if (lines[1].contains("Silberbar")) {
                                    category = ("Silberbarren");
                                } else if (lines[1].contains("Platinmün")) {
                                    category = ("Platinmünzen");
                                } else if (lines[1].contains("Platinbar")) {
                                    category = ("Platinbarren");
                                } else if (lines[1].contains("Palladiumbar")) {
                                    category = ("Palladiumbarren");
                                } else if (lines[1].contains("Palladiummün")) {
                                    category = ("Palladiummünzen");
                                } else if (lines[1].contains("Kupfermün")) {
                                    category = ("Kupfermünzen");
                                } else if (lines[1].contains("Kupferbar")) {
                                    category = ("Kupferbarren");
                                } else {
                                    category = ("");
                                }

                                elementsZero = lines[1].replace("Unzen", "oz")
                                        .replace("Unze", "oz")
                                        .replace("Gramm", "g")
                                        .replace("Kilogramm", "kg")
                                        .split("\sGoldmünze|\sGoldbarren|\sSilbermünze|\sSilberbarren|\sPlatinmünze|\sPlatinbarren|\sPalladiummünze|\sPalladiumbarren|\sKupfermünze|\sKupferbarren", 2);

                                if (elementsZero.length == 2) {
                                    if (elementsZero[1].trim().isEmpty()) {
                                        articleName = (category
                                                .replace(" - ", " ")
                                                .replace("'", "''")
                                                .trim()
                                        );
                                    } else {
                                        articleName = (elementsZero[1]
                                                .replace(" - ", " ")
                                                .replace("'", "''")
                                                .trim()
                                        );
                                    }
                                    articleWeight = (elementsZero[0]
                                            .replace(".", "")
                                            .replace("000 g", " kg")
                                            .replace("31,1 g", "1 oz")
                                    );
                                } else {
                                    elementsZero = elementsZero[0].trim().split("\s", 3);
                                    articleName = (elementsZero[2]
                                            .replace("'", "''")
                                            .trim()
                                    );
                                    articleWeight = (elementsZero[0].trim() + " " + elementsZero[1]);
                                }

                                doubleSell = lines[2].split("\s");
                                doubleBuy = lines[4].replace("ab ", "").split("\s");

                                articleSellPrice = (Double.parseDouble(doubleSell[0]
                                        .replace(".", "")
                                        .replace(",", ".")
                                ));
                                articleBuyPrice = (Double.parseDouble(doubleBuy[0]
                                        .replace(".", "")
                                        .replace(",", ".")
                                ));
                            }

                            entryData.add(globalFunctions.getEntryData(company, articleName, articleNr, category, articleBuyPrice, articleSellPrice, articleWeight));

                        }
                    }
                }
            }

            //Insert into org.gold.SQL
            entryDataRepository.deleteByCompanyAndDataCollectionDatetimeAfter("Auragentum", Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            entryDataRepository.saveAll(entryData);

        } catch (Throwable e) {
            GlobalVariables.errorCompanyList.add("Auragentum");
            try (FileWriter writer = new FileWriter(new File(fileConfigurationProperties.getReportFilesPath(), "Auragentum.txt"))) {
                for (StackTraceElement stackTrace : e.getStackTrace()) {
                    writer.write(stackTrace.toString() + "\n");
                }
            } catch (IOException ex) {
                System.out.println("An error occurred.");
                throw new RuntimeException(ex);
            }
            try {
                File SrcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Path destination = Paths.get(Path.of(fileConfigurationProperties.getReportFilesPath(), "Auragentum") + ".png");
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
