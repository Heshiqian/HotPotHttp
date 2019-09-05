package cn.heshiqian.hotpothttp.core.response;

import cn.heshiqian.hotpothttp.core.addtion.Log;
import cn.heshiqian.hotpothttp.core.exception.HotPotHttpException;
import cn.heshiqian.hotpothttp.core.pojo.Package;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class ResponseImpl implements Response {

    private int status = 200;
    private boolean cacheControl = true;
    private String path;

    private Map<Package.PackageType,Package> packageTypePackageMap=new HashMap<>();
    private Map<String,String> header=new HashMap<>();

    private String contentEncoding;
    private String contentType;

    private String content;
    private InputStream stream;

    private boolean isRedirct=false;

    public ResponseImpl() {
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getHeader(String key) {
        return header.get(key);
    }

    @Override
    public Map<String, String> getHeaders() {
        return header;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public InputStream getStream() {
        return stream;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getCharacterEncoding() {
        return contentEncoding;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean getCacheControl() {
        return cacheControl;
    }

    @Override
    public Map<Package.PackageType, Package> getPackages() {
        return packageTypePackageMap;
    }

    @Override
    public <T> T getPackage(Package.PackageType type, Class<T> tClass) {
        return tClass.cast(packageTypePackageMap.get(type));
    }

    @Override
    public Package getPackage(Package.PackageType type) {
        return packageTypePackageMap.get(type);
    }

    @Override
    public void setCacheControl(boolean cache) {
        this.cacheControl=cache;
    }

    @Override
    public void setStatus(int status) {
        this.status=status;
    }

    @Override
    public void addHeader(String key, String value) {
        header.put(key,value);
    }

    @Override
    public void setCharacterEncoding(String charset) {
        if (!Charset.isSupported(charset))
            throw new HotPotHttpException("unsupported charset:" + charset);
        contentEncoding = charset;
    }

    @Override
    public void setContentType(String type) {
        this.contentType=type;
    }

    @Override
    public void setContent(String content) {
        this.content=content;
    }

    @Override
    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public void sendRedirect(String addr) {
        status = 302;
        isRedirct=true;
    }

    @Override
    public void setPath(String path) {
        this.path =path;
    }

    @Override
    public void addPackage(Package.PackageType type, Package pack) {
        packageTypePackageMap.put(type,pack);
    }

    @Override
    public void addPackages(Map<Package.PackageType, Package> pack) {
        packageTypePackageMap.putAll(pack);
    }
}
