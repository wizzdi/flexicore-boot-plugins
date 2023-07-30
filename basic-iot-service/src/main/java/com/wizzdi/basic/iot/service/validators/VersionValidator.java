package com.wizzdi.basic.iot.service.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ValidationException;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public class VersionValidator implements ConstraintValidator<ValidVersion, Object> {

    @Override
    public void initialize(ValidVersion contactNumber) {
    }

    @Override
    public boolean isValid(Object contactFieldObj, ConstraintValidatorContext cxt) {

        if (contactFieldObj != null) {
            if (contactFieldObj instanceof String) {
                String contactField = (String) contactFieldObj;
                return validVersion(contactField);
            } else {
                if (contactFieldObj instanceof Collection) {
                    Collection<String> contactFields = (Collection<String>) contactFieldObj;
                    return contactFields.stream().allMatch(f -> validVersion(f));

                }
                else{
                    throw new ValidationException("Expected field type to be String or Collection");
                }
            }

        }

        return true;
    }

    private boolean validVersion(String contactField) {
        String[] split = contactField.split("\\.");
        boolean validInts = Stream.of(split).map(VersionValidator::parseOrNull).noneMatch(Objects::isNull);
        return split.length < 4 && validInts;
    }

    private static Integer parseOrNull(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Throwable ignored) {

        }
        return null;
    }

}