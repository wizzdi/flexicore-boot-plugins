/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.wizzdi.flexicore.dynamic.invoker.service.export.service;


import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.dynamic.invoker.service.export.request.ExportDynamicExecution;
import com.wizzdi.flexicore.dynamic.invoker.service.export.request.ExportDynamicInvoker;
import com.wizzdi.flexicore.dynamic.invoker.service.export.request.FieldProperties;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.*;
import com.wizzdi.flexicore.boot.dynamic.invokers.service.DynamicExecutionService;
import com.wizzdi.flexicore.boot.dynamic.invokers.service.DynamicInvokerService;
import com.wizzdi.flexicore.file.request.FileResourceCreate;
import com.wizzdi.flexicore.file.service.FileResourceService;
import com.wizzdi.flexicore.file.service.MD5Service;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.*;

@Primary
@Component
@Extension
public class DynamicInvokerExportService implements Plugin {


    @Autowired
    private DynamicInvokerService dynamicInvokersRepository;


    @Autowired
    private FileResourceService fileResourceService;
    @Autowired
    private DynamicExecutionService dynamicExecutionService;

    @Autowired
    private DynamicInvokerService dynamicInvokerService;
    @Autowired
    private MD5Service md5Service;



    private static final Logger logger = LoggerFactory.getLogger(DynamicInvokerExportService.class);



    public FileResource exportDynamicExecutionResultToCSV(ExportDynamicExecution exportDynamicExecution, SecurityContext SecurityContext) {
        ExecuteInvokersResponse executeInvokersResponse = dynamicExecutionService.executeDynamicExecution(exportDynamicExecution, SecurityContext);
        Map<String, FieldProperties> fieldToName = exportDynamicExecution.getFieldToName();
        CSVFormat csvFormat = exportDynamicExecution.getCsvFormat();

        return invokerResponseToCSV(SecurityContext, executeInvokersResponse, fieldToName, csvFormat);


    }

    public FileResource exportDynamicInvokerToCSV(ExportDynamicInvoker exportDynamicExecution, SecurityContext SecurityContext) {
        ExecuteInvokersResponse executeInvokersResponse = dynamicInvokerService.executeInvoker(exportDynamicExecution, SecurityContext);
        Map<String, FieldProperties> fieldToName = exportDynamicExecution.getFieldProperties();
        CSVFormat csvFormat = exportDynamicExecution.getCsvFormat();

        return invokerResponseToCSV(SecurityContext, executeInvokersResponse, fieldToName, csvFormat);


    }

    private FileResource invokerResponseToCSV(SecurityContext SecurityContext, ExecuteInvokersResponse executeInvokersResponse, Map<String, FieldProperties> fieldToName, CSVFormat csvFormat) {
        File file = new File(fileResourceService.generateNewPathForFileResource("dynamic-execution-csv", SecurityContext.getUser()) + ".csv");
        String[] headersArr = fieldToName.values().stream().sorted(Comparator.comparing(FieldProperties::getOrdinal)).map(FieldProperties::getName).toArray(String[]::new);

        boolean writeBom = CSVFormat.EXCEL.equals(csvFormat);
        CSVFormat format = csvFormat.builder().setHeader(headersArr).build();
        Map<String, Method> fieldNameToMethod = new HashMap<>();
        if (writeBom) {
            try (Writer out = new OutputStreamWriter(new FileOutputStream(file, true))) {
                out.write(ByteOrderMark.UTF_BOM);

            } catch (Exception e) {
                logger.error("failed writing UTF-8 BOM", e);
            }


        }
        try (Writer out = new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(out, format)) {

            for (ExecuteInvokerResponse<?> respons : executeInvokersResponse.getResponses()) {
                Object response = respons.getResponse();
                if (response != null) {
                    Collection<?> collection = getCollection(response);
                    if (collection != null) {
                        List<List<Object>> expendedRecordSet = toExpendedRecordSet(fieldToName, fieldNameToMethod, collection);
                        csvPrinter.printRecords(expendedRecordSet);

                    }
                }

            }
            csvPrinter.flush();
        } catch (Exception e) {
            logger.error("unable to create csv",e);
        }
        FileResourceCreate fileResourceCreate=new FileResourceCreate()
                .setOriginalFilename(file.getName())
                .setFullPath(file.getAbsolutePath())
                .setMd5(md5Service.generateMD5(file))
                .setName(file.getName());
        FileResource fileResource = fileResourceService.createFileResourceNoMerge(fileResourceCreate, SecurityContext);
        fileResource.setKeepUntil(OffsetDateTime.now().plusMinutes(30));
        fileResourceService.merge(fileResource);
        return fileResource;
    }

    private Collection<?> getCollection(Object response) {
        if (response instanceof Collection) {
            return (Collection<?>) response;
        }
        if (response instanceof PaginationResponse<?>) {
            return ((PaginationResponse<?>) response).getList();
        }
        return null;
    }

    public static List<List<Object>> toExpendedRecordSet(Map<String, FieldProperties> fieldToName, Map<String, Method> methodCache, Collection<?> collection){
        Set<String> failedMethodsCache = new HashSet<>();
        List<String> fieldsSorted = fieldToName.entrySet().stream().sorted(Comparator.comparing(f -> f.getValue().getOrdinal())).map(f -> f.getKey()).toList();
        return collection.parallelStream().map(f -> toExpendedRecord(methodCache, failedMethodsCache, fieldsSorted, f)).toList();


    }

    private static List<Object> toExpendedRecord(Map<String, Method> methodCache, Set<String> failedMethodsCache, List<String> fieldsSorted, Object collectionEntry)  {
        List<Object> recordAsList = new ArrayList<>();
        for (String fieldPath : fieldsSorted) {
            Object current = getValueFromFieldPath(methodCache, failedMethodsCache, collectionEntry, fieldPath);
            if (current == null) {
                recordAsList.add("");
                continue;
            }
            recordAsList.add(current);
        }
        return recordAsList;
    }


    private static Object getValueFromFieldPath(Map<String, Method> methodCache, Set<String> failedCache, Object currentCollectionItem, String fieldPath) {
        Class<?> collectionItemType = currentCollectionItem.getClass();
        String[] split = fieldPath.split("\\.");
        String fullPathSoFar = "";
        Object current = currentCollectionItem;
        Class<?> currentClass = collectionItemType;
        //for object A containing nested object B ... with fieldPath Z
        //fieldPath = b.c.....z
        for (String currentPathComponent : split) {
            fullPathSoFar += currentPathComponent;
            if (failedCache.contains(fullPathSoFar)) {
                //failed in the past
                current = null;
            }
            Object data;
            if(current instanceof Map<?,?> map){
                data =map.get(currentPathComponent);
            }
            else{
                Method method = getGetter(methodCache, fullPathSoFar, currentClass, currentPathComponent);
                if (method == null) {
                    failedCache.add(fullPathSoFar);
                    current = null;
                    break;
                }
                data = invokeMethodProtected(current, method);
            }
            if(data==null){
                current=null;
                break;
            }

            current = data;
            currentClass = current.getClass();
        }
        return current;
    }

    private static Object invokeMethodProtected(Object current, Method method){
        try {
            return method.invoke(current);
        }
        catch (Throwable e){
            logger.debug("failed invoking method "+method.getName() +" on object",e);
            return null;
        }
    }

    private static Method getGetter(Map<String, Method> fieldNameToMethod, String fullPathSoFar, Class<?> currentClass, String currentName) {
        Method method = fieldNameToMethod.get(fullPathSoFar);
        if (method == null) {
            try {
                method = currentClass.getMethod("get" + StringUtils.capitalize(currentName));
            } catch (Exception ignored) {
            }
            if (method == null) {
                try {
                    method = currentClass.getMethod("is" + StringUtils.capitalize(currentName));
                } catch (Exception ignored) {

                    return null;
                }
            }

            fieldNameToMethod.put(fullPathSoFar, method);
        }
        return method;
    }


    public void validateExportDynamicExecution(ExportDynamicExecution exportDynamicExecution, SecurityContext SecurityContext) {
        dynamicExecutionService.validate(exportDynamicExecution, SecurityContext);
        if (exportDynamicExecution.getFieldToName() == null || exportDynamicExecution.getFieldToName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Field to name map must be non null and not empty");
        }
        if (exportDynamicExecution.getCsvFormat() == null) {
            exportDynamicExecution.setCsvFormat(CSVFormat.EXCEL);
        }
    }

    public void validateExportDynamicInvoker(ExportDynamicInvoker exportDynamicInvoker, SecurityContext SecurityContext) {
        if (exportDynamicInvoker.getFieldProperties() == null || exportDynamicInvoker.getFieldProperties().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"fieldProperties map must be non null and not empty");
        }
        if (exportDynamicInvoker.getCsvFormat() == null) {
            exportDynamicInvoker.setCsvFormat(CSVFormat.EXCEL);
        }
    }


    public void validate(DynamicExecutionExampleRequest dynamicExecutionExampleRequest, SecurityContext SecurityContext) {
        dynamicExecutionService.validate(dynamicExecutionExampleRequest, SecurityContext);

    }
}
