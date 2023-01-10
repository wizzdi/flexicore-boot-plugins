package com.wizzdi.flexicore.dynamic.invoker.service.email.service;

import com.flexicore.request.ExportDynamicExecution;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.impl.DynamicInvokersService;
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
import com.wizzdi.flexicore.dynamic.invoker.service.email.request.SendDynamicInvokerRequest;
import com.wizzdi.flexicore.dynamic.invoker.service.email.response.SendStatusEmailResponse;
import com.wizzdi.flexicore.file.model.FileResource;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Set;

@Component
@Extension

public class DynamicInvokerCSVEmailService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(DynamicInvokerCSVEmailService.class);


    @Value("${wizzdi.invokers.email.sendgrid.from}")
    private String from;
    @Value("${wizzdi.invokers.email.sendgrid.replyTo}")
    private String replyTo;

    @Autowired
    private DynamicInvokersService dynamicInvokersService;
    @Autowired
    @Qualifier("mappedPOISendGrid")
    @Lazy
    private SendGrid sendGrid;


    public SendStatusEmailResponse sendEmail(SendDynamicInvokerRequest sendDynamicInvokerRequest, SecurityContext securityContext) {
        ZoneOffset zoneOffset= sendDynamicInvokerRequest.getZoneOffset()!=null? sendDynamicInvokerRequest.getZoneOffset():ZoneOffset.UTC;
        ExportDynamicExecution exportDynamicExecution = sendDynamicInvokerRequest.getExportDynamicExecution();
        FileResource fileResource = dynamicInvokersService.exportDynamicExecutionResultToCSV(exportDynamicExecution, securityContext);
        String title= sendDynamicInvokerRequest.getTitle()!=null? sendDynamicInvokerRequest.getTitle():"CSV Export "+OffsetDateTime.now().atZoneSameInstant(zoneOffset);
        File file=new File(fileResource.getFullPath());
        return sendEmail(file, sendDynamicInvokerRequest.getEmails(),title);
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
                            .Builder("export.csv", inputStream)
                            .withType("text/csv")
                            .build();
                    mail.addAttachments(attachments);
                }


                mail.setFrom(new Email(from, "Notification Manager"));
                Content content = new Content("text/plain", "Exported CSV is Attached");
                mail.setReplyTo(new Email(replyTo, "No Reply"));

                mail.setSubject(title);
                mail.addPersonalization(personalization);

                request.setBody(mail.build());
                Response response = sendGrid.api(request);
                logger.info(String.format("send invoker csv emails , status was: %d , message was : %s",response.getStatusCode(), response.getBody()));
            } catch (IOException e) {
                logger.error("failed sending mail ",e);
                return new SendStatusEmailResponse().setSent(false);
            }

        return new SendStatusEmailResponse().setSent(true);
    }

}
