package cn.heshiqian.hotpothttp.core.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Plugins {
    String pluginName() default "";
    Class plugin();
    ProxyTarget target();
}
