package com.selenium.pricecollector.email;

import com.selenium.pricecollector.helper.FileConfigurationProperties;
import com.selenium.pricecollector.helper.GlobalVariables;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
public class EmailService {
    @Autowired
    FileConfigurationProperties fileConfigurationProperties;
    @Autowired
    EmailConfigurationProperties emailConfigurationProperties;
    @Autowired
    JavaMailSender mailSender;

    private MimeMessageHelper getMimeMessageHelper(MimeMessage message) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(emailConfigurationProperties.getFrom());

        helper.setTo(emailConfigurationProperties.getTo().split("[,;|\\s]+"));

        return helper;
    }

    public void sendErrorMailHTML() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = getMimeMessageHelper(message);
        helper.setSubject("Selenium Price Collector - Error");

        helper.setText("""
                        <b>Dear Dev</b>,
                        <br><br><br>
                        <i>Please find the selenium screenshot errors attached.</i>
                        <br><br>""" +
                        GlobalVariables.errorCompanyList
                        + mailBody(),
                true);

        File f = new File(fileConfigurationProperties.getReportFilesPath());

        String[] fileNames = f.list((dir, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".txt"));

        if (fileNames != null) {
            for (String pathname : fileNames) {
                FileSystemResource file = new FileSystemResource(new File(fileConfigurationProperties.getReportFilesPath() + "\\" + pathname));
                helper.addAttachment(pathname, file);
            }
        }
        mailSender.send(message);
    }

    public void sendSuccessMailHTML() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = getMimeMessageHelper(message);
        helper.setSubject("Selenium Price Collector - Success");

        helper.setText("""
                        <b>Dear Dev</b>,
                        <br><br><br>
                        <i>Price Collector Imported with Success</i>
                        <br><br>
                        """
                        + mailBody(),
                true);

        mailSender.send(message);
    }

    private String mailBody() {
        return """
                                <br><br><br>
                
                                <p class=MsoNormal><span style='mso-bookmark:_MailAutoSig'><span
                        style='font-size:12.0pt;font-family:"Arial",sans-serif;mso-fareast-font-family:
                "Times New Roman";mso-fareast-theme-font:minor-fareast;color:#1F497D;
                mso-fareast-language:DE;mso-no-proof:yes'>Degussa </span></span><span
                style='mso-bookmark:_MailAutoSig'><span style='font-size:12.0pt;font-family:
                "Arial",sans-serif;mso-fareast-font-family:"Times New Roman";mso-fareast-theme-font:
                minor-fareast;color:#1F497D;mso-fareast-language:DE;mso-no-proof:yes'>
                        </span></span><span
                        style='mso-bookmark:_MailAutoSig'><span style='font-size:12.0pt;font-family:
                "Arial",sans-serif;mso-fareast-font-family:"Times New Roman";mso-fareast-theme-font:
                minor-fareast;color:#1F497D;mso-fareast-language:DE;mso-no-proof:yes'>&nbsp;
                Goldhandel GmbH </span></span><span style='mso-bookmark:_MailAutoSig'><span
                        style='font-size:13.5pt;mso-ascii-font-family:Calibri;mso-fareast-font-family:
                "Times New Roman";mso-fareast-theme-font:minor-fareast;mso-hansi-font-family:
                Calibri;mso-bidi-font-family:Calibri;mso-fareast-language:DE;mso-no-proof:yes'><o:p></o:p></span></span></p>
                        <p class=MsoNormal><span style='mso-bookmark:_MailAutoSig'><span
                        style='font-size:13.5pt;mso-ascii-font-family:Calibri;mso-fareast-font-family:
                "Times New Roman";mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri;
                mso-fareast-language:DE;mso-no-proof:yes'><br>
                        </span></span><span style='mso-bookmark:_MailAutoSig'><span style='font-family:
                "Arial",sans-serif;mso-fareast-font-family:"Times New Roman";color:#1F497D;
                mso-fareast-language:DE;mso-no-proof:yes'>IT-Abteilung&nbsp;<br>
                Niedenau 45 </span></span><span style='mso-bookmark:_MailAutoSig'><span
                        style='font-size:12.0pt;mso-ascii-font-family:Calibri;mso-fareast-font-family:
                "Times New Roman";mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri;
                mso-fareast-language:DE;mso-no-proof:yes'><br>
                        </span></span><span style='mso-bookmark:_MailAutoSig'><span style='font-family:
                "Arial",sans-serif;mso-fareast-font-family:"Times New Roman";color:#1F497D;
                mso-fareast-language:DE;mso-no-proof:yes'>60325 Frankfurt am Main </span></span><span
                style='mso-bookmark:_MailAutoSig'><span style='font-size:12.0pt;mso-ascii-font-family:
                Calibri;mso-fareast-font-family:"Times New Roman";mso-hansi-font-family:Calibri;
                mso-bidi-font-family:Calibri;mso-fareast-language:DE;mso-no-proof:yes'><br>
                        </span></span><span style='mso-bookmark:_MailAutoSig'><span style='font-size:
                13.5pt;mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
                mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri;mso-fareast-language:
                DE;mso-no-proof:yes'><br>
                        </span></span><span style='mso-bookmark:_MailAutoSig'><span style='font-family:
                "Arial",sans-serif;mso-fareast-font-family:"Times New Roman";color:#1F497D;
                mso-fareast-language:DE;mso-no-proof:yes'>Telefon: +49 69 860068 272<br>
                Telefax:&nbsp;+49 69 860068 333 <br>
                        Mobil:&nbsp;&nbsp;&nbsp;&nbsp; +49 151 550 521 99 <br>
                                </span></span><span style='mso-bookmark:_MailAutoSig'><span style='font-size:
                7.5pt;mso-ascii-font-family:Calibri;mso-fareast-font-family:"Times New Roman";
                mso-hansi-font-family:Calibri;mso-bidi-font-family:Calibri;mso-fareast-language:
                DE;mso-no-proof:yes'><br>
                        </span></span><span style='mso-bookmark:_MailAutoSig'><span style='font-family:
                "Arial",sans-serif;mso-fareast-font-family:"Times New Roman";color:#1F497D;
                mso-fareast-language:DE;mso-no-proof:yes'>Mail:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        </span></span><span style='mso-bookmark:_MailAutoSig'></span><a
                        href="mailto:bi@degussa-goldhandel.de"><span style='mso-bookmark:
                _MailAutoSig'><span style='font-family:"Arial",sans-serif;mso-fareast-font-family:
                "Times New Roman";color:#1F497D;mso-fareast-language:DE;mso-no-proof:yes'>bi@degussa-goldhandel.de
                        </span></span><span style='mso-bookmark:_MailAutoSig'></span></a><span
                        style='mso-bookmark:_MailAutoSig'><span style='font-size:7.5pt;mso-ascii-font-family:
                Calibri;mso-fareast-font-family:"Times New Roman";mso-hansi-font-family:Calibri;
                mso-bidi-font-family:Calibri;mso-fareast-language:DE;mso-no-proof:yes'><o:p></o:p></span></span></p>
                
                        <p class=MsoNormal style='margin-bottom:12.0pt'><span style='mso-bookmark:_MailAutoSig'><span
                        style='font-family:"Arial",sans-serif;mso-fareast-font-family:"Times New Roman";
                mso-fareast-theme-font:minor-fareast;color:#1F497D;mso-fareast-language:DE;
                mso-no-proof:yes'>Internet:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span></span><a
                href="https://www.degussa-goldhandel.de"><span style='mso-bookmark:_MailAutoSig'><span
                        style='font-family:"Arial",sans-serif;mso-fareast-font-family:"Times New Roman";
                mso-fareast-theme-font:minor-fareast;color:#1F497D;mso-fareast-language:DE;
                mso-no-proof:yes'>www.degussa-goldhandel.de </span></span><span
                style='mso-bookmark:_MailAutoSig'></span></a><span style='mso-bookmark:_MailAutoSig'><span
                        style='font-family:"Arial",sans-serif;mso-fareast-font-family:"Times New Roman";
                mso-fareast-theme-font:minor-fareast;color:#1F497D;mso-fareast-language:DE;
                mso-no-proof:yes'><br>
                Online-Shop: </span></span><span style='mso-bookmark:_MailAutoSig'></span><a
                        href="https://shop.degussa-goldhandel.de/"><span style='mso-bookmark:_MailAutoSig'><span
                        style='font-family:"Arial",sans-serif;mso-fareast-font-family:"Times New Roman";
                mso-fareast-theme-font:minor-fareast;color:#1F497D;mso-fareast-language:DE;
                mso-no-proof:yes'>shop.degussa-goldhandel.de </span></span><span
                style='mso-bookmark:_MailAutoSig'></span></a><span style='mso-bookmark:_MailAutoSig'><span
                        style='font-size:12.0pt;font-family:"Arial",sans-serif;mso-fareast-font-family:
                "Times New Roman";mso-fareast-theme-font:minor-fareast;color:#1F497D;
                mso-fareast-language:DE;mso-no-proof:yes'><br style='mso-special-character:
                line-break'>
                        <![if !supportLineBreakNewLine]><br style='mso-special-character:line-break'>
                                <![endif]><o:p></o:p></span></span></p>
                
                
                                </span></span><span style='mso-bookmark:_MailAutoSig'><span style='font-size:
                8.0pt;font-family:"Arial",sans-serif;mso-fareast-font-family:"Times New Roman";
                mso-fareast-theme-font:minor-fareast;color:#999999;mso-fareast-language:DE;
                mso-no-proof:yes'>Degussa Sonne/Mond Goldhandel GmbH,
                Geschäftsführer:&nbsp;Christian Rauch&nbsp;<br>
                        Handelsregister-Nr.: HRB 188979, Sitz der Gesellschaft: München, USt-ID-Nr.:
                DE275313528 </span></span><span style='mso-bookmark:_MailAutoSig'><span
                        style='font-size:10.0pt;font-family:"Arial",sans-serif;mso-fareast-font-family:
                "Times New Roman";mso-fareast-theme-font:minor-fareast;color:black;mso-fareast-language:
                DE;mso-no-proof:yes'><br style='mso-special-character:line-break'>""";
    }

    public void clearFilesReport() {
        File f = new File(fileConfigurationProperties.getReportFilesPath());

        String[] fileNames = f.list((dir, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".txt"));
        if (fileNames != null) {
            for (String filename : fileNames) {
                try {
                    Files.delete(Path.of(fileConfigurationProperties.getReportFilesPath(), filename));
                } catch (IOException e) {
                    log.error("Error removing files", e);
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
