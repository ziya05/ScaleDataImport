package com.ziya05.ScaleDataImport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
public @interface MySqlFor {
	String name();
	boolean iskey() default false;
	boolean isvalue() default false;
}
