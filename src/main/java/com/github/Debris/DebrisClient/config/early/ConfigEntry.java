package com.github.Debris.DebrisClient.config.early;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigEntry {
    String key();

    double doubleDefault() default 0;

    boolean booleanDefault() default false;

    String stringDefault() default "";

    long intDefault() default 0;
}
