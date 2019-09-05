package cn.heshiqian.hotpothttp.core.request;

import cn.heshiqian.hotpothttp.core.pojo.Package;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class RequestImpl implements Request{


    private RequestHead requestHead;
    private String path;
    private String type;
    private Status status;

    private Map<Package.PackageType,Package> packageTypePackageMap=new HashMap<>();
    private Map<String,String> parameter=new HashMap<>();

    private String method;
    private InetAddress inetAddress;
    private String ip;

    public RequestImpl(String method, InetAddress inetAddress, String ip) {
        this.method = method;
        this.inetAddress = inetAddress;
        this.ip = ip;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public InetAddress getInetAddress() {
        return inetAddress;
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public RequestHead getHeader() {
        return requestHead;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Map<Package.PackageType, Package> getPackages() {
        return packageTypePackageMap;
    }

    @Override
    public Package getPackage(Package.PackageType type) {
        return packageTypePackageMap.get(type);
    }

    @Override
    public String getParameter(String key) {
        return null;
    }

    public void setParameter(Map<String, String> parameter) {
        this.parameter = parameter;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void setHeader(RequestHead requestHead) {
        this.requestHead = requestHead;
    }

    @Override
    public void addPackage(Package.PackageType type, Package pack) {
        packageTypePackageMap.put(type,pack);
    }

    @Override
    public String toString() {
        return "Request => ip:" + ip +
                " path:"+ path;
    }
}
