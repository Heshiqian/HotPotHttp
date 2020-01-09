package cn.heshiqian.hotpothttp.module.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapped {
    String url();
    String resource();
    boolean isRedirect();
}
