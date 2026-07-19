package com.wizzdi.basic.iot.tester.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.basic.iot.tester.request.IotTestStartRequest;
import com.wizzdi.basic.iot.tester.response.IotTestReport;
import com.wizzdi.basic.iot.tester.service.IotTesterService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@OperationsInside
@RequestMapping("/plugins/IotTester")
@Tag(name = "IoT Tester")
@Extension
@RestController
public class IotTesterController implements Plugin {
    @Autowired
    private IotTesterService service;

    @PostMapping("/start")
    @Operation(summary = "Starts the staged Basic IoT end-to-end test")
    public IotTestReport start(@RequestBody IotTestStartRequest request,
                               @RequestAttribute SecurityContext securityContext) {
        return service.start(request, securityContext);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException exception) {
        int statusCode = exception.getStatusCode().value();
        HttpStatus status = HttpStatus.resolve(statusCode);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", statusCode);
        body.put("error", status != null ? status.getReasonPhrase() : exception.getStatusCode().toString());
        body.put("message", exception.getReason());

        return ResponseEntity.status(statusCode).body(body);
    }

    @GetMapping("/getReport/{runId}")
    @Operation(summary = "Returns a Basic IoT end-to-end test report")
    public IotTestReport getReport(@PathVariable String runId) {
        IotTestReport report = service.getReport(runId);
        if (report == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No IoT test run " + runId);
        }
        return report;
    }

    @GetMapping("/getLatestReport")
    @Operation(summary = "Returns the latest Basic IoT end-to-end test report")
    public IotTestReport getLatestReport() {
        IotTestReport report = service.getLatestReport();
        if (report == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No IoT test has been started");
        }
        return report;
    }

    @PostMapping("/abort/{runId}")
    @Operation(summary = "Aborts an active Basic IoT end-to-end test")
    public IotTestReport abort(@PathVariable String runId) {
        IotTestReport report = service.abort(runId);
        if (report == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No IoT test run " + runId);
        }
        return report;
    }
}
