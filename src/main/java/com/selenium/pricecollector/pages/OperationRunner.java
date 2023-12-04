package com.selenium.pricecollector.pages;

import com.selenium.pricecollector.email.EmailService;
import com.selenium.pricecollector.helper.GlobalVariables;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    private JavaMailSender javaMailSender;

    @Scheduled(cron = "0 0/30 1-18 * * *")
    public void run() throws InterruptedException, MessagingException, ExecutionException, IOException {

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(6);

        Future<?> degussaFuture = executor.submit(() -> {
            degussa.run();
        });

        Future<?> philoroFuture = executor.submit(() -> {
            philoro.run();
        });

        Future<?> auragentumFuture = executor.submit(() -> {
            auragentum.run();
        });

        Future<?> goldSilberShopFuture = executor.submit(() -> {
            goldSilberShop.run();
        });

        Future<?> proAurumFuture = executor.submit(() -> {
            proAurum.run();
        });

        Future<?> reiseBankFuture = executor.submit(() -> {
            reiseBank.run();
        });


        degussaFuture.get();
        philoroFuture.get();
        auragentumFuture.get();
        goldSilberShopFuture.get();
        proAurumFuture.get();
        reiseBankFuture.get();

        if (!GlobalVariables.errorCompanyList.isEmpty()) {
            javaMailSender.send(emailService.sendMailHTML());
            GlobalVariables.errorCompanyList.clear();
            emailService.clearPhotos();
        }
    }

}
