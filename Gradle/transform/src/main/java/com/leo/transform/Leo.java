package com.leo.transform;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * <p>Date:2020-03-30.19:19</p>
 * <p>Author:niu bao</p>
 * <p>Desc:</p>
 */
@Target({TYPE, METHOD, CONSTRUCTOR})
@Retention(CLASS)
public @interface Leo {
        String value() default "leo";
}
