package com.wizzdi.flexicore.dynamic.invoker.service.email.service;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.request.ExportDynamicExecution;
import com.flexicore.request.ExportDynamicInvoker;
import com.flexicore.request.FieldProperties;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.service.impl.DynamicInvokersService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.ExecuteInvokersResponse;
import com.wizzdi.flexicore.boot.dynamic.invokers.service.DynamicExecutionService;
import com.wizzdi.flexicore.boot.dynamic.invokers.service.DynamicInvokerService;
import com.wizzdi.flexicore.dynamic.invoker.service.email.request.SendDynamicExecutionRequest;
import com.wizzdi.flexicore.dynamic.invoker.service.email.request.SendDynamicInvokerRequest;
import com.wizzdi.flexicore.dynamic.invoker.service.email.response.SendDynamicEmailResponse;
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
import java.util.*;
import java.util.stream.Collectors;

@Component
@Extension

public class DynamicInvokerEmailService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(DynamicInvokerEmailService.class);

    @Value("${wizzdi.invokers.email.sendgrid.template.id}")
    private String templateId;
    @Value("${wizzdi.invokers.email.sendgrid.from}")
    private String from;
    @Value("${wizzdi.invokers.email.sendgrid.replyTo}")
    private String replyTo;

    @Autowired
    private DynamicExecutionService dynamicExecutionService;
    @Autowired
    private DynamicInvokerService dynamicInvokerService;
    @Autowired
    @Qualifier("mappedPOISendGrid")
    @Lazy
    private SendGrid sendGrid;


    public SendDynamicEmailResponse sendEmail(SendDynamicExecutionRequest sendDynamicExecutionRequest, SecurityContextBase securityContext) {
        ZoneOffset zoneOffset= sendDynamicExecutionRequest.getZoneOffset()!=null? sendDynamicExecutionRequest.getZoneOffset():ZoneOffset.UTC;
        ExportDynamicExecution exportDynamicExecution = sendDynamicExecutionRequest.getExportDynamicExecution();
        ExecuteInvokersResponse executeInvokersResponse = dynamicExecutionService.executeDynamicExecution(exportDynamicExecution, securityContext);
        String[] headersArr = exportDynamicExecution.getFieldToName().values().stream().sorted(Comparator.comparing(FieldProperties::getOrdinal)).map(FieldProperties::getName).toArray(String[]::new);
        Map<String, java.lang.reflect.Method> fieldNameToMethod = new HashMap<>();
        List<List<Object>> entries= executeInvokersResponse.getResponses().stream().map(f->f.getResponse()).map(f->getCollection(f)).filter(f->f!=null).map(f->correctOffset(DynamicInvokersService.toExpendedRecordSet(exportDynamicExecution.getFieldToName(),fieldNameToMethod,f),zoneOffset)).flatMap(List::stream).toList();
        String title= sendDynamicExecutionRequest.getTitle()!=null? sendDynamicExecutionRequest.getTitle():"CSV Export "+OffsetDateTime.now().atZoneSameInstant(zoneOffset);

        return sendEmail(entries,headersArr, sendDynamicExecutionRequest.getEmails(),title);
    }

    private List<List<Object>> correctOffset(List<List<Object>> recordSet, ZoneOffset zoneOffset) {
        return recordSet.stream().map(f->correctOffsetRow(f,zoneOffset)).toList();
    }

    private List<Object> correctOffsetRow(List<Object> row, ZoneOffset zoneOffset) {
        return row.stream().map(f->correctOffsetItem(f,zoneOffset)).collect(Collectors.toList());
    }

    private Object correctOffsetItem(Object f, ZoneOffset zoneOffset) {
        if(f instanceof OffsetDateTime offsetDateTime){
            return offsetDateTime.atZoneSameInstant(zoneOffset);
        }
        return f;
    }

    public SendDynamicEmailResponse sendEmail(List<List<Object>> entries, String[] headersArr, Set<String> emails, String title) {

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
                personalization.addDynamicTemplateData("headers",headersArr);

                personalization.addDynamicTemplateData("entries",entries);
                personalization.addDynamicTemplateData("title",title);


                mail.setFrom(new Email(from, "Notification Manager"));

                mail.setReplyTo(new Email(replyTo, "No Reply"));

                mail.setTemplateId(templateId);
                mail.addPersonalization(personalization);

                request.setBody(mail.build());
                Response response = sendGrid.api(request);
                logger.info(String.format("send invoker template email , status was: %d , message was : %s",response.getStatusCode(), response.getBody()));
            } catch (IOException e) {
                logger.error("failed sending mail ",e);
                return new SendDynamicEmailResponse().setSent(false);
            }

        return new SendDynamicEmailResponse().setSent(true);
    }

    public Collection<?> getCollection(Object response) {
        if (response instanceof Collection) {
            return (Collection<?>) response;
        }
        if (response instanceof PaginationResponse) {
            return ((PaginationResponse<?>) response).getList();
        }
        if (response instanceof com.wizzdi.flexicore.security.response.PaginationResponse) {
            return ((com.wizzdi.flexicore.security.response.PaginationResponse<?>) response).getList();
        }
        return null;
    }

    public SendDynamicEmailResponse sendEmail(SendDynamicInvokerRequest sendDynamicInvokerRequest, SecurityContextBase securityContext) {
        ZoneOffset zoneOffset= sendDynamicInvokerRequest.getZoneOffset()!=null? sendDynamicInvokerRequest.getZoneOffset():ZoneOffset.UTC;
        ExportDynamicInvoker exportDynamicInvoker = sendDynamicInvokerRequest.getExportDynamicInvoker();
        ExecuteInvokersResponse executeInvokersResponse = dynamicInvokerService.executeInvoker(exportDynamicInvoker, securityContext);
        String[] headersArr = exportDynamicInvoker.getFieldProperties().values().stream().sorted(Comparator.comparing(FieldProperties::getOrdinal)).map(FieldProperties::getName).toArray(String[]::new);
        Map<String, java.lang.reflect.Method> fieldNameToMethod = new HashMap<>();
        List<List<Object>> entries= executeInvokersResponse.getResponses().stream().map(f->f.getResponse()).map(f->getCollection(f)).filter(f->f!=null).map(f->correctOffset(DynamicInvokersService.toExpendedRecordSet(exportDynamicInvoker.getFieldProperties(),fieldNameToMethod,f),zoneOffset)).flatMap(List::stream).toList();
        String title= sendDynamicInvokerRequest.getTitle()!=null? sendDynamicInvokerRequest.getTitle():"CSV Export "+OffsetDateTime.now().atZoneSameInstant(zoneOffset);

        return sendEmail(entries,headersArr, sendDynamicInvokerRequest.getEmails(),title);
    }
}
