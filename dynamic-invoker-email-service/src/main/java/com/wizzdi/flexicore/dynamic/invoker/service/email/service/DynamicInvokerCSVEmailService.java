package com.wizzdi.flexicore.dynamic.invoker.service.email.service;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
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
import com.wizzdi.flexicore.dynamic.invoker.service.email.request.SendDynamicExecutionRequest;
import com.wizzdi.flexicore.dynamic.invoker.service.email.request.SendDynamicInvokerRequest;
import com.wizzdi.flexicore.dynamic.invoker.service.email.response.SendDynamicEmailResponse;
import com.wizzdi.flexicore.dynamic.invoker.service.export.request.ExportDynamicExecution;
import com.wizzdi.flexicore.dynamic.invoker.service.export.request.ExportDynamicInvoker;
import com.wizzdi.flexicore.dynamic.invoker.service.export.service.DynamicInvokerExportService;
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
    @Value("${wizzdi.invokers.email.sendgrid.from.name:Notification Manager}")
    private String fromName;
    @Value("${wizzdi.invokers.email.sendgrid.replyTo}")
    private String replyTo;
    @Value("${wizzdi.invokers.email.sendgrid.replyTo.name:No Reply}")
    private String replyToName;

    @Autowired
    private DynamicInvokerExportService dynamicInvokersService;
    @Autowired
    @Qualifier("mappedPOISendGrid")
    @Lazy
    private SendGrid sendGrid;


    public SendDynamicEmailResponse sendEmail(SendDynamicExecutionRequest sendDynamicExecutionRequest, SecurityContext SecurityContext) {
        ZoneOffset zoneOffset= sendDynamicExecutionRequest.getZoneOffset()!=null? sendDynamicExecutionRequest.getZoneOffset():ZoneOffset.UTC;
        ExportDynamicExecution exportDynamicExecution = sendDynamicExecutionRequest.getExportDynamicExecution();
        FileResource fileResource = dynamicInvokersService.exportDynamicExecutionResultToCSV(exportDynamicExecution, SecurityContext);
        String title= sendDynamicExecutionRequest.getTitle()!=null? sendDynamicExecutionRequest.getTitle():"CSV Export "+OffsetDateTime.now().atZoneSameInstant(zoneOffset);
        File file=new File(fileResource.getFullPath());
        return sendEmail(file, sendDynamicExecutionRequest.getEmails(),title);
    }

    public SendDynamicEmailResponse sendEmail(File file, Set<String> emails, String title) {

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


                mail.setFrom(new Email(from,fromName ));
                mail.addContent(new Content("text/plain", "Exported CSV is Attached"));
                mail.setReplyTo(new Email(replyTo, replyToName));

                mail.setSubject(title);
                mail.addPersonalization(personalization);

                request.setBody(mail.build());
                Response response = sendGrid.api(request);
                logger.info(String.format("send invoker csv emails , status was: %d , message was : %s",response.getStatusCode(), response.getBody()));
            } catch (IOException e) {
                logger.error("failed sending mail ",e);
                return new SendDynamicEmailResponse().setSent(false);
            }

        return new SendDynamicEmailResponse().setSent(true);
    }

    public SendDynamicEmailResponse sendEmail(SendDynamicInvokerRequest sendDynamicExecutionRequest, SecurityContext SecurityContext) {
        ZoneOffset zoneOffset= sendDynamicExecutionRequest.getZoneOffset()!=null? sendDynamicExecutionRequest.getZoneOffset():ZoneOffset.UTC;
        ExportDynamicInvoker exportDynamicExecution = sendDynamicExecutionRequest.getExportDynamicInvoker();
        FileResource fileResource = dynamicInvokersService.exportDynamicInvokerToCSV(exportDynamicExecution, SecurityContext);
        String title= sendDynamicExecutionRequest.getTitle()!=null? sendDynamicExecutionRequest.getTitle():"CSV Export "+OffsetDateTime.now().atZoneSameInstant(zoneOffset);
        File file=new File(fileResource.getFullPath());
        return sendEmail(file, sendDynamicExecutionRequest.getEmails(),title);
    }
}
