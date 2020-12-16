package com.github.hopedc.luzern.boot;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hopedc on 2017/3/9 009.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(luzernConfiguration.class)
@EnableConfigurationProperties(luzernProperties.class)
public @interface EnableLuzern {
}
