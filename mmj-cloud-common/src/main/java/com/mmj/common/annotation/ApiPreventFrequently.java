package com.mmj.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ApiPreventFrequently {
	
	/**
	 * 当preventFrequently值为true时，需设定时间间隔，单位：毫秒<br/>
	 * 表示在指定时间间隔内，同一用户只能对使用@MmjSecurity注解的方法进行一次请求，其它请求都会被视为重复请求
	 * @return
	 */
	int timeInterval() default 2000;
	
}
