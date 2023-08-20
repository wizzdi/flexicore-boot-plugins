package com.wizzdi.maps.service.email.request;

import com.wizzdi.maps.service.email.context.EmailEntry;
import com.wizzdi.maps.service.email.context.StatusEmailHeaders;
import org.apache.commons.csv.CSVFormat;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record SendCSVMailRequest(List<EmailEntry> entries, Set<String> emails, CSVFormat csvFormat, String title, Map<StatusEmailHeaders, String> headerNames) {

}
