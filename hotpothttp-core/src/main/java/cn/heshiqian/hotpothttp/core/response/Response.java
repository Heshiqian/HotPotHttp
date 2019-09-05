package cn.heshiqian.hotpothttp.core.response;

import cn.heshiqian.hotpothttp.core.pojo.Package;

import java.io.InputStream;
import java.util.Map;

public interface Response {

    int getStatus();
    String getHeader(String key);
    Map<String,String> getHeaders();
    String getContent();
    InputStream getStream();
    String getContentType();
    String getCharacterEncoding();
    String getPath();
    boolean getCacheControl();
    Map<Package.PackageType, Package> getPackages();
    <T>T getPackage(Package.PackageType type, Class<T> tClass);
    <T>T getPackage(Package.PackageType type);

    void setCacheControl(boolean cache);
    void setStatus(int status);
    void addHeader(String key, String value);
    void setCharacterEncoding(String charset);
    void setContentType(String type);
    void setContent(String content);
    void setStream(InputStream stream);
    void sendRedirect(String addr);
    void setPath(String path);
    void addPackage(Package.PackageType type, Package pack);
    void addPackages(Map<Package.PackageType, Package> pack);

}
