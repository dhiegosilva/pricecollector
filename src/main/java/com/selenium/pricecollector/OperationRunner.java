package com.selenium.pricecollector;

import com.selenium.pricecollector.email.EmailService;
import com.selenium.pricecollector.helper.GlobalVariables;
import com.selenium.pricecollector.helper.ticker.XMLimport;
import com.selenium.pricecollector.pages.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;


@Component
public class OperationRunner {

    @Autowired
    private Degussa degussa;
    @Autowired
    private Auragentum auragentum;
    @Autowired
    private GoldSilberShop goldSilberShop;
    @Autowired
    private Philoro philoro;
    @Autowired
    private ProAurum proAurum;
    @Autowired
    private ReiseBank reiseBank;
    @Autowired
    private EmailService emailService;
    @Autowired
    private XMLimport xmlimport;

//    @PostConstruct
    @Scheduled(cron = "0 0/30 1-18 * * *")
    public void run() throws InterruptedException, MessagingException, ExecutionException, IOException, ParserConfigurationException, SAXException {

        xmlimport.xmlSpotPriceReader();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(6);

        Future<?> degussaFuture = executor.submit(() -> degussa.run());
        Future<?> philoroFuture = executor.submit(() -> philoro.run());//need check
        Future<?> auragentumFuture = executor.submit(() -> auragentum.run());//need check
        Future<?> goldSilberShopFuture = executor.submit(() -> goldSilberShop.run());//need check
        Future<?> proAurumFuture = executor.submit(() -> proAurum.run());
        Future<?> reiseBankFuture = executor.submit(() -> reiseBank.run()); //need check


        degussaFuture.get();
        philoroFuture.get();
        auragentumFuture.get();
        goldSilberShopFuture.get();
        proAurumFuture.get();
        reiseBankFuture.get();

        executor.shutdown();

        if (!GlobalVariables.errorCompanyList.isEmpty()) {
            emailService.sendErrorMailHTML();
            GlobalVariables.errorCompanyList.clear();
            emailService.clearFilesReport();
        } else {
            emailService.sendSuccessMailHTML();
        }
    }
}
