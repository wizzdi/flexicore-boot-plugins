package com.wizzdi.basic.iot.service.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = VersionValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidVersion {
    String message() default "Invalid version format must be XXX.YYY.ZZZ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}