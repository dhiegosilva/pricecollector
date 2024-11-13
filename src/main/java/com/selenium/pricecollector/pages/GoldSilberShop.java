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
public class GoldSilberShop {

    @Autowired
    private EntryDataRepository entryDataRepository;
    @Autowired
    private MyRemoteWebDriver goldSilberShopDriver;
    @Autowired
    private GlobalFunctions globalFunctions;

    private RemoteWebDriver driver;

    @Autowired
    FileConfigurationProperties fileConfigurationProperties;

    public void run() {

        List<EntryData> entryData = new LinkedList<>();

        String company = "GoldSilberShop", articleNr = null, category = null, articleName = null, articleWeight = null;
        Double articleBuyPrice = 00.00, articleSellPrice = 00.00;

        try {
            driver = goldSilberShopDriver.start();

            List<WebElement> sell, buy, articleElementName;
            String[] sessions, splitName;

            //Navigate Page
            driver.navigate().to("https://www.goldsilbershop.de/preisliste.html");
            Thread.sleep(7000);

            try {
                driver.findElement(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll")).click();

            } catch (Exception e) {

            }

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
                Thread.sleep(7000);

                articleElementName = driver.findElements(By.className("mto-pricelist-title"));
                sell = driver.findElements(By.className("mto-pricelist-price-ak"));
                buy = driver.findElements(By.className("mto-pricelist-price-online"));

                for (int i = 0; i < articleElementName.size(); i++) {
                    splitName = articleElementName.get(i).getText().split(" ");

                    if (splitName.length > 2) {
                        if (splitName[1].equals("g") || splitName[1].contains("kg") || splitName[1].toLowerCase().contains("unze") || splitName[1].toLowerCase().contains("gramm")) {
                            if (buy.get(i).getText().contains(" €") && sell.get(i).getText().contains(" €")) {
                                articleNr = (null);
                                articleName = (articleElementName.get(i).getText()
                                        .replace(splitName[0] + " " + splitName[1], "")
                                        .replace("Gold ", "")
                                        .replace("Silber ", "")
                                        .replace("Platin ", "")
                                        .replace("Palladium ", "")
                                        .trim());
                                articleWeight = (splitName[0] + " " + splitName[1]
                                        .replace("Unzen", "oz")
                                        .replace("Unze", "oz")
                                        .replace("31,107 g", "1 oz")
                                        .replace("311,035 g", "10 oz")
                                        .replace("Unzen", "oz"));
                                articleBuyPrice = (Double.parseDouble(buy.get(i).getText()
                                        .replace(" €", "")
                                        .replace(".", "")
                                        .replace(",", ".")));
                                articleSellPrice = (Double.parseDouble(sell.get(i).getText()
                                        .replace(" €", "")
                                        .replace(".", "")
                                        .replace(",", ".")));
                                //Gold
                                if (articleElementName.get(i).getText().contains("Gold")) {

                                    if (articleElementName.get(i).getText().contains("münzba") || articleElementName.get(i).getText().contains("barr")) {
                                        category = ("Goldbarren");
                                    } else {
                                        category = ("Goldmünzen");
                                    }
                                }

                                //Silber
                                else if (articleElementName.get(i).getText().contains("Silber")) {

                                    if (articleElementName.get(i).getText().contains("münzb") || articleElementName.get(i).getText().contains("barr")) {
                                        category = ("Silberbarren");
                                    } else {
                                        category = ("Silbermünzen");
                                    }
                                }

                                //Platin
                                else if (articleElementName.get(i).getText().contains("Platin")) {

                                    if (articleElementName.get(i).getText().contains("münzb") || articleElementName.get(i).getText().contains("barr")) {
                                        category = ("Platinbarren");
                                    } else {
                                        category = ("Platinmünzen");
                                    }
                                }

                                //Palladium
                                else if (articleElementName.get(i).getText().contains("Palladium")) {

                                    if (articleElementName.get(i).getText().contains("münzb") || articleElementName.get(i).getText().contains("barr")) {
                                        category = ("Platinbarren");
                                    } else {
                                        category = ("Palladiummünzen");
                                    }
                                }

                                //Kufper
                                else if (articleElementName.get(i).getText().contains("Kupfer")) {

                                    if (articleElementName.get(i).getText().contains("münzb") || articleElementName.get(i).getText().contains("barr")) {
                                        category = ("Kupferbarren");
                                    } else {
                                        category = ("Kupfermünzen");
                                    }
                                } else {
                                    category = ("");
                                }

                                entryData.add(globalFunctions.getEntryData(company,articleName,articleNr,category,articleBuyPrice,articleSellPrice,articleWeight));

                            }
                        }
                    }
                }
            }

            //Insert into org.gold.SQL
            entryDataRepository.deleteByCompanyAndDataCollectionDatetimeAfter("GoldSilberShop", Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            entryDataRepository.saveAll(entryData);

        } catch (Throwable e) {
            GlobalVariables.errorCompanyList.add("GoldSilberShop");
            try (FileWriter writer = new FileWriter(new File(fileConfigurationProperties.getReportFilesPath(), "GoldSilberShop.txt"))) {
                for (StackTraceElement stackTrace : e.getStackTrace()) {
                    writer.write(stackTrace.toString() + "\n");
                }
            } catch (IOException ex) {
                System.out.println("An error occurred.");
                throw new RuntimeException(ex);
            }
            try {
                File SrcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Path destination = Paths.get(Path.of(fileConfigurationProperties.getReportFilesPath(), "GoldSilberShop") + ".png");
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
