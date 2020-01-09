package cn.heshiqian.hotpothttp.core.proxy;

import cn.heshiqian.hotpothttp.core.addtion.HotPotTools;
import cn.heshiqian.hotpothttp.core.addtion.Log;
import cn.heshiqian.hotpothttp.core.clazz.LibClassLoader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public final class PrivateProxyManage implements ProxyManageInterface{

    private static ClassLoader systemClassLoader;
    private static ClassLoader libClassLoader;
    private static ClassLoader contextClassLoader;

    private PrivateProxyManage(){}
    private static class PPMHolder{
        private static PrivateProxyManage holder=new PrivateProxyManage();
    }
    public static PrivateProxyManage getInstance() {
        return PPMHolder.holder;
    }
    static {
        systemClassLoader = ClassLoader.getSystemClassLoader();
        contextClassLoader = Thread.currentThread().getContextClassLoader();
    }

    private LinkedList<Object> instance = new LinkedList<>();

    @Override
    public Object loadClass(String className) throws ClassNotFoundException{
        Object obj = null;
        if (className==null||className.length()==0)
            throw new IllegalArgumentException("class name is null or empty");
        try {
            Class<?> loadClass;
            try{
                //先找默认loader
                loadClass = systemClassLoader.loadClass(className);
            }catch (ClassNotFoundException e){
                //找不到再从lib中找
                loadClass = libClassLoader.loadClass(className);
            }
            Constructor constructor = loadClass.getConstructor();
            obj = constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        //添加进实例化管理
        if (obj!=null)
            instance.add(obj);
        return obj;
    }

    @Override
    public Object loadClassButNoAddIntoList(Class c) throws Exception{
        if (c==null) throw new IllegalArgumentException("class is null");
        Object o=null;
        try {
            o = c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    @Override
    public Object loadClass(Class c) throws Exception {
        if (c==null) throw new IllegalArgumentException("class is null");
        Object o=null;
        try {
            Constructor constructor = c.getConstructor();
            o = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        if (o!=null) instance.add(o);
        return o;
    }


    public Class onlyGetClass(String className) throws ClassNotFoundException{
        Class<?> loadClass;
        try{
            loadClass = Class.forName(className);
        }catch (ClassNotFoundException e){
            try{
                //先找默认loader
                loadClass = systemClassLoader.loadClass(className);
            }catch (ClassNotFoundException ee){
                //找不到再从lib中找
                loadClass = libClassLoader.loadClass(className);
            }
        }
        return loadClass;
    }

    @Override
    public void removeClass(Object instance) {

    }

    @Override
    public void removeClass(String className) {
        if (className==null||className.length()==0)return;
        Iterator<Object> iterator = instance.iterator();
        while (iterator.hasNext()){
            Object next = iterator.next();
            if (next.getClass().getTypeName().equals(className)){
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public Object getInstance(String className) {
        int index = isHasInstance(className);
        if (index == -1) return null;
        return instance.get(index);
    }

    @Override
    public <T> T getInstance(String className, Class<T> cls) {
        int index = isHasInstance(className);
        if (index == -1) return null;
        Object o = instance.get(index);
        //可能无法转换
        try{
            return cls.cast(o);
        }catch (ClassCastException e){
            Log.err("ProxyManage can not cast this class["+o.getClass().getTypeName()+"] to target class["+cls.getTypeName()+"]");
            Log.err(HotPotTools.printStackTrace(e.getStackTrace()));
            return null;
        }
    }

    private int isHasInstance(String className){
        if (className==null)return -1;
        for (int i = 0; i < instance.size(); i++) {
            String typeName = instance.get(i).getClass().getTypeName();
            if (typeName.equals(className))
                return i;
        }
        return -1;
    }


    public boolean checkInstanceOf(Object obj,Class targetClass){
        Objects.requireNonNull(obj);
        Objects.requireNonNull(targetClass);
        return targetClass.isAssignableFrom(obj.getClass());
    }

    @Override
    public Object addClass(Object instance) {
        return null;
    }

    @Override
    public <T> T addClass(Object instance, Class<T> cls) {
        return null;
    }

    @Override
    public void setLibs(List<File> libs) {
        ArrayList<URL> urls=new ArrayList<>();
        for (File lib : libs) {
            //加入到LibClassLoader
            URL url = null;
            try {
                url = lib.toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.err(lib.getAbsolutePath()+" load error.");
            }
            if (url!=null){
                urls.add(url);
                //如果Jar中存在MANIFEST.MF则去读取MANIFEST指定的字段进行


            }
        }
        libClassLoader=new LibClassLoader(urls);
    }
}
