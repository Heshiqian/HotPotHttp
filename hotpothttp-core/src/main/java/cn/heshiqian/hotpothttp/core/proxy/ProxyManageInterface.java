package cn.heshiqian.hotpothttp.core.proxy;

import java.io.File;
import java.util.List;

public interface ProxyManageInterface {

    /**
     * interface version
     */
    String version="0.0.1";

    Object loadClass(String className) throws ClassNotFoundException;
    Object loadClass(Class c)throws Exception;
    Object loadClassButNoAddIntoList(Class c) throws Exception;


    void removeClass(Object instance);
    void removeClass(String className);

    Object getInstance(String className);
    <T>T getInstance(String className,Class<T> cls);

    Object addClass(Object instance);
    <T>T addClass(Object instance,Class<T> cls);


    void setLibs(List<File> libs);
}
