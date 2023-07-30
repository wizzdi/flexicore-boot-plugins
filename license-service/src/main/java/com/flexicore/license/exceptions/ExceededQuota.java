package com.flexicore.license.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ExceededQuota extends ResponseStatusException {

    public ExceededQuota() {
        super(HttpStatus.FORBIDDEN);
    }

    public ExceededQuota(String reason) {
        super(HttpStatus.FORBIDDEN, reason);
    }

    public ExceededQuota( String reason, Throwable cause) {
        super(HttpStatus.FORBIDDEN, reason, cause);
    }
}
