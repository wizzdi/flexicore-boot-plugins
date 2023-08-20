package com.wizzdi.maps.service.email.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.maps.service.email.context.EmailEntry;
import com.wizzdi.maps.service.email.context.StatusEmailHeaders;
import com.wizzdi.maps.service.email.request.SendCSVMailRequest;
import com.wizzdi.maps.service.email.request.SendStatusEmailRequest;
import com.wizzdi.maps.service.email.response.SendStatusEmailResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.ByteOrderMark;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Extension
public class MappedPOICsvMailService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(MappedPOICsvMailService.class);

    @Value("${wizzdi.maps.status.sendgrid.from}")
    private String from;
    @Value("${wizzdi.maps.status.sendgrid.replyTo}")
    private String replyTo;
    @Autowired
    @Qualifier("mappedPOISendGrid")
    @Lazy
    private SendGrid sendGrid;

    public SendStatusEmailResponse sendEmailAsCSV(SendCSVMailRequest sendCSVMailRequest) {
        File csv=createCsv(sendCSVMailRequest.headerNames(),sendCSVMailRequest.entries(),sendCSVMailRequest.csvFormat());
        return sendEmail(csv,sendCSVMailRequest.emails(),sendCSVMailRequest.title());
    }

    public File createCsv(Map<StatusEmailHeaders, String> headerNames, List<EmailEntry> emailEntries, CSVFormat csvFormat) {
        try {
            File tempFile = File.createTempFile("status", ".csv");
            tempFile.deleteOnExit();
            if (CSVFormat.EXCEL.equals(csvFormat)) {
                try (Writer out = new OutputStreamWriter(new FileOutputStream(tempFile, true))) {
                    out.write(ByteOrderMark.UTF_BOM);

                } catch (Exception e) {
                    logger.error("failed writing UTF-8 BOM", e);
                }


            }
            try(CSVPrinter csvPrinter = new CSVPrinter(new java.io.FileWriter(tempFile, StandardCharsets.UTF_8, true), csvFormat)){
                csvPrinter.printRecord(Arrays.stream(StatusEmailHeaders.values()).map(f->headerNames.getOrDefault(f,f.name())).toList());
                for (EmailEntry emailEntry : emailEntries) {
                    csvPrinter.printRecord(emailEntry.getDate(),emailEntry.getTime(),emailEntry.getTenant(),emailEntry.getExternalId(),emailEntry.getName(),emailEntry.getStatusName());
                }
                csvPrinter.flush();
            }


            return tempFile;
        }
        catch (IOException e){
            logger.error("failed creating csv ",e);
        }
        return null;
    }

    public SendStatusEmailResponse sendEmail(File file, Set<String> emails, String title) {

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("/mail/send");

            // Create mail
            Mail mail = new Mail();
            Personalization personalization = new Personalization();
            for (String email : emails) {
                personalization.addTo(new Email(email, email));

            }
            try (final InputStream inputStream = Files.newInputStream(file.toPath())) {
                final Attachments attachments = new Attachments
                        .Builder("status.csv", inputStream)
                        .withType("text/csv")
                        .build();
                mail.addAttachments(attachments);
            }


            mail.setFrom(new Email(from, "Notification Manager"));
            mail.addContent(new Content("text/plain", "Status CSV is Attached"));
            mail.setReplyTo(new Email(replyTo, "No Reply"));

            mail.setSubject(title);
            mail.addPersonalization(personalization);

            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            logger.info(String.format("send status report emails , status was: %d , message was : %s",response.getStatusCode(), response.getBody()));
        } catch (IOException e) {
            logger.error("failed sending mail ",e);
            return new SendStatusEmailResponse().setSent(false);
        }

        return new SendStatusEmailResponse().setSent(true);
    }
}
