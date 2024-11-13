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
public class Degussa {

    @Autowired
    private EntryDataRepository entryDataRepository;
    @Autowired
    private MyRemoteWebDriver degussaDriver;
    @Autowired
    private GlobalFunctions globalFunctions;

    private RemoteWebDriver driver;

    @Autowired
    FileConfigurationProperties fileConfigurationProperties;

    public void run() {

        List<EntryData> entryData = new LinkedList<>();

        String company = "Degussa", articleNr = null, category = null, articleName = null, articleWeight = null;
        Double articleBuyPrice = 00.00, articleSellPrice = 00.00;

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

                    if (lines.length > 8 && lines[2].contains("oz") || lines.length > 8 && lines[2].equals("g") || lines.length > 8 && lines[2].equals("kg")) {

                        splitElement = row.split("\s[-]?[0-9]+[,.]?[0-9]*([\\/][0-9]+[,.]?[0-9]*)*\\s(?:gr?|kg|kilogramms?|oz|ounces?|-grm|gramms?)");
                        if (splitElement.length == 2) {

                            articleWeight = (row
                                    .replace(splitElement[0], "")
                                    .replace(splitElement[1], "")
                                    .replace("000 g", " kg")
                                    .trim());

                            articleNr = (lines[0]);

                            if (row.toLowerCase().contains("goldbar")) {
                                category = ("Goldbarren");

                            } else if (row.toLowerCase().contains("goldm")) {
                                category = ("Goldmünzen");

                            } else if (row.toLowerCase().contains("gold")) {
                                category = ("Gold");

                            } else if (row.toLowerCase().contains("silberbar")) {
                                category = ("Silberbarren");

                            } else if (row.toLowerCase().contains("silberm")) {
                                category = ("Silbermünzen");

                            } else if (row.toLowerCase().contains("silber")) {
                                category = ("Silber");

                            } else if (row.toLowerCase().contains("platinbar")) {
                                category = ("Platinbarren");

                            } else if (row.toLowerCase().contains("platinm")) {
                                category = ("Platinmünzen");

                            } else if (row.toLowerCase().contains("platinb")) {
                                category = ("Platinbarren");

                            } else if (row.toLowerCase().contains("palladiumbar")) {
                                category = ("Palladiumbarren");

                            } else if (row.toLowerCase().contains("palladiumm")) {
                                category = ("Palladiummünzen");

                            } else if (row.toLowerCase().contains("palladium")) {
                                category = ("Palladium");
                            }

                            splitElement = row.split("\s[-]?[0-9]+[,.]?[0-9]*([\\/][0-9]+[,.]?[0-9]*)*\\s(?:gr?|kg|kilogramms?|oz|ounces?|-grm|gramms?)");
                            articleName = (splitElement[1]
                                    .replace(category, "")
                                    .replace(" - ", " ")
                                    .replace("Degussa", "")
                                    .replace("€", "")
                                    .replace(lines[lines.length - 4], "")
                                    .replace(lines[lines.length - 2], "")
                                    .replace("'", "''")
                                    .trim());

                            if (lines[lines.length - 3].contains("€") && lines[lines.length - 2].contains(",")) {
                                articleSellPrice = (Double.parseDouble(lines[lines.length - 4]
                                        .replace(".", "")
                                        .replace(",", ".")
                                        .trim()
                                ));
                            } else {
                                articleSellPrice = (0.00);
                            }

                            if (lines[lines.length - 1].contains("€") && lines[lines.length - 2].contains(",")) {
                                articleBuyPrice = (Double.parseDouble(lines[lines.length - 2]
                                        .replace(".", "")
                                        .replace(",", ".")
                                        .trim()
                                ));
                            } else {
                                articleBuyPrice = (0.00);
                            }
                        }
                    }
                    entryData.add(globalFunctions.getEntryData(company, articleName, articleNr, category, articleBuyPrice, articleSellPrice, articleWeight));

                }
            }

            //Insert into org.gold.SQL
            ///////////////////////////////////////////
            entryDataRepository.deleteByCompanyAndDataCollectionDatetimeAfter("Degussa", Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            entryDataRepository.saveAll(entryData);
        } catch (Throwable e) {
            GlobalVariables.errorCompanyList.add("Degussa");
            try (FileWriter writer = new FileWriter(new File(fileConfigurationProperties.getReportFilesPath(), "Degussa.txt"))) {
                for (StackTraceElement stackTrace : e.getStackTrace()) {
                    writer.write(stackTrace.toString() + "\n");
                }
            } catch (IOException ex) {
                System.out.println("An error occurred.");
                ex.printStackTrace();
            }
            try {
                File SrcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Path destination = Paths.get(Path.of(fileConfigurationProperties.getReportFilesPath(), "Degussa") + ".png");
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