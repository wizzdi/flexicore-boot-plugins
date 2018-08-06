package com.flexicore.scheduling.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SchedulingMethod {
    String displayName() default "";
    String description() default "";
    SchedulingParameter[] parameters() default {};

}
