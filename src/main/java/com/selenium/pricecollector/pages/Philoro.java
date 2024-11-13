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
import org.openqa.selenium.interactions.Actions;
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
public class Philoro {

    @Autowired
    private EntryDataRepository entryDataRepository;
    @Autowired
    private MyRemoteWebDriver philoroDriver;
    @Autowired
    private GlobalFunctions globalFunctions;

    private RemoteWebDriver driver;
//    private WebDriver driver;

    @Autowired
    FileConfigurationProperties fileConfigurationProperties;

    public void run() {

        List<EntryData> entryData = new LinkedList<>();

        String company = "Philoro", articleNr = null, category = null, articleName = null, articleWeight = null;
        Double articleBuyPrice = 00.00, articleSellPrice = 00.00;

        String[] sessions;
        String lastTwoWords = "";

        try {
            driver = philoroDriver.start();
//            driver = philoroDriver.startWebdriver();

            WebElement parent;
            List<WebElement> pageElements;

            //region Gather Data
            driver.navigate().to("https://philoro.de/preisliste/alle");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("onetrust-reject-all-handler")));
            Thread.sleep(5000);
            driver.findElement(By.id("onetrust-reject-all-handler")).click();
            Thread.sleep(5000);
            List<WebElement> checkboxAvailableProducts = driver.findElements(By.className("checkbox__checkmark"));

            Actions actions = new Actions(driver);
            actions.moveToElement(checkboxAvailableProducts.get(0)).click().perform();

            Thread.sleep(10000);

            sessions = new String[]{"goldmünzen", "goldbarren", "silbermünzen", "silberbarren", "platin-& palladium", "numismatik"};

            for (String session : sessions) {

                //Parent
                parent = driver.findElement(By.id(session));
                //Child
                pageElements = parent.findElements(By.className("border-b"));

                String row;
                String[] lines, splitElement, splitCategory;

                for (WebElement pageElement : pageElements) {
                    row = pageElement.getText();
                    lines = row.split("\n");

                    if (lines.length == 4) {

                        splitCategory = lines[1].split("\s", 2);

                        if (splitCategory[0].equals("Gold")) {
                            category = ("Goldmünzen");
                        } else if (splitCategory[0].equals("Goldbarren")) {
                            category = ("Goldbarren");
                        } else if (splitCategory[0].equals("Silber")) {
                            category = ("Silbermünzen");
                        } else if (splitCategory[0].equals("Silberbarren")) {
                            category = ("Silberbarren");
                        } else if (splitCategory[0].equals("Platin")) {
                            category = ("Platinmünzen");
                        } else if (splitCategory[0].equals("Platinbarren")) {
                            category = ("Platinbarren");
                        } else if (splitCategory[0].equals("Palladium")) {
                            category = ("Palladiummünzen");
                        } else if (splitCategory[0].equals("Palladiumbarren")) {
                            category = ("Palladiumbarren");
                        } else if (splitCategory[0].equals("Kupfer")) {
                            category = ("Kupfermünzen");
                        } else {
                            break;
                        }
                        articleNr = (lines[0]);

                        splitElement = splitCategory[1].split("\\s+");
                        if (splitElement.length >= 2) {
                            // Extract the last two words
                            lastTwoWords = splitElement[splitElement.length - 2] + " " + splitElement[splitElement.length - 1];
                        }
                        articleName = (splitCategory[1]);

                        articleWeight = (lastTwoWords
                                .replace("Unze", "oz")
                                .replace("Gramm", "g")
                                .replace("0.5", "1/2")
                                .replace("0.25", "1/4")
                                .replace("0.1", "1/10")
                                .replace("0.05", "1/20")
                                .replace(".00 oz", " oz")
                                .replace(".00 g", " g")
                                .replace("000 g", " kg")
                                .replace("31,10 g", "1 oz")
                        );
                        articleSellPrice = (Double.parseDouble(lines[lines.length - 2]
                                .substring(0, (lines[lines.length - 2].length() - 2))
                                .replace(".", "")
                                .replace(",", ".")
                                .trim()
                        ));

                        articleBuyPrice = (Double.parseDouble(lines[lines.length - 1]
                                .substring(0, (lines[lines.length - 2].length() - 2))
                                .replace(".", "")
                                .replace(",", ".")
                                .trim()
                        ));

                        entryData.add(globalFunctions.getEntryData(company,articleName,articleNr,category,articleBuyPrice,articleSellPrice,articleWeight));

                    }
                }
            }

            //Insert into org.gold.SQL
            //////////////////////////////
            entryDataRepository.deleteByCompanyAndDataCollectionDatetimeAfter("Philoro", Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            entryDataRepository.saveAll(entryData);
        } catch (Throwable e) {
            GlobalVariables.errorCompanyList.add("Philoro");
            try (FileWriter writer = new FileWriter(new File(fileConfigurationProperties.getReportFilesPath(), "Philoro.txt"))) {
                for (StackTraceElement stackTrace : e.getStackTrace()) {
                    writer.write(stackTrace.toString() + "\n");
                }
            } catch (IOException ex) {
                System.out.println("An error occurred.");
                throw new RuntimeException(ex);
            }
            try {
                File SrcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Path destination = Paths.get(Path.of(fileConfigurationProperties.getReportFilesPath(), "Philoro") + ".png");
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