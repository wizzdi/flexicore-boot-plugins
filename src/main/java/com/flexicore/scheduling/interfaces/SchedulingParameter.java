package com.flexicore.scheduling.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
public @interface SchedulingParameter {
    String displayName() default "";
    String description() default "";
    
}
