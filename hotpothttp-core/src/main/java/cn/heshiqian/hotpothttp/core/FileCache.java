package cn.heshiqian.hotpothttp.core;

import cn.heshiqian.hotpothttp.core.addtion.HotPotTools;
import cn.heshiqian.hotpothttp.core.addtion.Log;
import cn.heshiqian.hotpothttp.core.config.Configuration;
import cn.heshiqian.hotpothttp.core.pojo.CacheFilePojo;
import cn.heshiqian.hotpothttp.core.pojo.SimpleFilePojo;
import cn.heshiqian.hotpothttp.core.response.ResponseHelper;

import java.io.*;
import java.util.*;

public final class FileCache {

    private final boolean debug;
    private Boolean enableCache;
    private Integer maxCacheable;
    private CacheMode cacheMode;

    private static final class FileCacheHolder{
        private static FileCache fileCache=new FileCache();
    }
    public static FileCache getInstance() {
        return FileCacheHolder.fileCache;
    }

    private final Object lock=new Object();

    private Map<String, SimpleFilePojo> fileLinkedMap = new HashMap<>();

    private static final ArrayList<CacheFilePojo> cacheFileSet = new ArrayList<>();

    private FileCache(){
        debug = Configuration.getArg("debug",Boolean.class)!=null||Configuration.getArg("debug",Boolean.class)==true;
        //判断是否启用缓存机制,默认启用
        enableCache = (enableCache=Configuration.getArg(Configuration.CACHE_ENABLE, Boolean.class))==null?true:enableCache;
        //获取缓存最大数,默认50
        maxCacheable = (maxCacheable=Configuration.getArg(Configuration.CACHE_MAX, Integer.class))==null?50:maxCacheable;

        String modeStr = Configuration.getArg(Configuration.CACHE_MODE, String.class);
        if (modeStr==null){
            cacheMode = CacheMode.MEMORY;
        }else {
            if (!modeStr.equals("memory")&&!modeStr.equals("file")){
                Log.err("you input cache mode is invalid, is only accept 'memory' or 'file'.");
                Log.err("this time will use default mode: 'memory'.");
                cacheMode = CacheMode.MEMORY;
            }else {
                cacheMode = CacheMode.convert(modeStr);
            }
        }
        Log.debug("cache enable:" + enableCache);
        Log.debug("cache mode:" + cacheMode.toString());
    }


    @SuppressWarnings("All")
    private CacheFilePojo cacheFile(String path,SimpleFilePojo filePojo) throws IOException {
        if (isCache(filePojo))
            return getFileCache(filePojo);
        //最大缓存移除最后一个
        if (cacheFileSet.size() >= maxCacheable)
            cacheFileSet.remove(cacheFileSet.size()-1);

        String filePath = filePojo.getFilePath();
        String fileName = filePojo.getFileName();

        File file = new File(filePath);

        if (file.length()>Integer.MAX_VALUE){
            Log.err("file:"+fileName+" path:"+filePath+" is too large, isn't cacheable");
            return null;
        }

        CacheFilePojo cacheFilePojo = new CacheFilePojo(file, path, filePojo);
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        byte[] buff=new byte[1024];
        int len=0;
        while ((len=fis.read(buff))!=-1){
            bos.write(buff,0,len);
        }
        cacheFilePojo.setPayload(bos.toByteArray());
        cacheFileSet.add(cacheFilePojo);
        return cacheFilePojo;
    }

    private boolean isCache(SimpleFilePojo filePojo){
        Iterator<CacheFilePojo> iterator = cacheFileSet.iterator();
        while (iterator.hasNext()){
            CacheFilePojo next = iterator.next();
            if (next.getSimpleFilePojo().equals(filePojo))
                return true;
        }
        return false;
    }

    private CacheFilePojo getFileCache(SimpleFilePojo filePojo){
        Iterator<CacheFilePojo> iterator = cacheFileSet.iterator();
        while (iterator.hasNext()){
            CacheFilePojo next = iterator.next();
            if (next.getSimpleFilePojo().equals(filePojo))
                return next;
        }
        return null;
    }

    public CacheFilePojo loadFile(String path, boolean enableCache) throws IOException {
        SimpleFilePojo simpleFilePojo = fileLinkedMap.get(path);
        CacheFilePojo cacheFilePojo;
        if (enableCache&&this.enableCache) {
            cacheFilePojo = cacheFile(path, simpleFilePojo);
        } else {
            cacheFilePojo = readFile(path, simpleFilePojo);
        }
        return cacheFilePojo;
    }

    @SuppressWarnings("All")
    private CacheFilePojo readFile(String path, SimpleFilePojo simpleFilePojo) throws IOException{
        String filePath = simpleFilePojo.getFilePath();
        File file = new File(filePath);
        CacheFilePojo cacheFilePojo = new CacheFilePojo(file, path, simpleFilePojo);
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        byte[] buff=new byte[1024];
        int len=0;
        while ((len=fis.read(buff))!=-1){
            bos.write(buff,0,len);
        }
        fis.close();
        cacheFilePojo.setPayload(bos.toByteArray());
        return cacheFilePojo;
    }

    public void addNewFile(String fileRelativePath, SimpleFilePojo filePojo){
        synchronized (lock){
            fileLinkedMap.put(fileRelativePath, filePojo);
        }
    }

    public boolean hasFile(String path){
        return fileLinkedMap.containsKey(path);
    }

    public void removeFile(String fileName){
        Iterator<Map.Entry<String, SimpleFilePojo>> iterator = fileLinkedMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, SimpleFilePojo> next = iterator.next();
            SimpleFilePojo value = next.getValue();
            if (value.getFileName().equals(fileName)){
                iterator.remove();
                break;
            }
        }
    }

    public void reload(){
        synchronized (lock){
            fileLinkedMap.clear();
        }
    }

    public void a(){
//        HotPotTools.printMapLineByLine(fileLinkedMap,"file cache debug mode");
    }

    public enum CacheMode{
        MEMORY,FILE;
        public static CacheMode convert(String mode){
            return valueOf(mode.toUpperCase());
        }
    }

}
