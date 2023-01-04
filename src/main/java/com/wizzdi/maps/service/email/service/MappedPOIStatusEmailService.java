package com.wizzdi.maps.service.email.service;

import com.flexicore.security.SecurityContextBase;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.maps.model.StatusHistory;
import com.wizzdi.maps.service.email.context.EmailEntry;
import com.wizzdi.maps.service.email.request.SendStatusEmailRequest;
import com.wizzdi.maps.service.email.response.SendStatusEmailResponse;
import com.wizzdi.maps.service.request.StatusHistoryForDateRequest;
import com.wizzdi.maps.service.service.StatusHistoryGroupedService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Extension

public class MappedPOIStatusEmailService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(MappedPOIStatusEmailService.class);

    @Value("${wizzdi.maps.status.sendgrid.template.id}")
    private String templateId;
    @Value("${wizzdi.maps.status.sendgrid.from}")
    private String from;
    @Value("${wizzdi.maps.status.sendgrid.replyTo}")
    private String replyTo;

    @Autowired
    private StatusHistoryGroupedService statusHistoryGroupedService;
    @Autowired
    @Qualifier("mappedPOISendGrid")
    @Lazy
    private SendGrid sendGrid;


    public SendStatusEmailResponse sendEmail(SendStatusEmailRequest sendStatusEmailRequest, SecurityContextBase securityContext) {
        ZoneOffset zoneOffset=sendStatusEmailRequest.getZoneOffset()!=null?sendStatusEmailRequest.getZoneOffset():ZoneOffset.UTC;
        List<StatusHistory> statusHistories = statusHistoryGroupedService.getAllStatusHistoriesForDate(new StatusHistoryForDateRequest().setMappedPOIFilter(sendStatusEmailRequest.getMappedPOIFilter()).setStatusAtDate(OffsetDateTime.now()), securityContext).getList();
        List<EmailEntry> emailEntries=new ArrayList<>();
        for (int i = 0; i < statusHistories.size(); i++) {
            emailEntries.add(new EmailEntry(statusHistories.get(i),i,zoneOffset));
        }
        return sendEmail(emailEntries,sendStatusEmailRequest.getEmails(),zoneOffset);
    }

    public SendStatusEmailResponse sendEmail(List<EmailEntry> entries, Set<String> emails, ZoneOffset zoneOffset) {

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
                personalization.addDynamicTemplateData("entries",entries);
                personalization.addDynamicTemplateData("date",OffsetDateTime.now().withOffsetSameInstant(zoneOffset).format(DateTimeFormatter.ISO_DATE_TIME));


                mail.setFrom(new Email(from, "Notification Manager"));

                mail.setReplyTo(new Email(replyTo, "No Reply"));

                mail.setSubject("Status Report");
                mail.setTemplateId(templateId);
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
