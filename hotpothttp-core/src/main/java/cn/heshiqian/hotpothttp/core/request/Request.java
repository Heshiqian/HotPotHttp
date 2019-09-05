package cn.heshiqian.hotpothttp.core.request;

import cn.heshiqian.hotpothttp.core.pojo.Package;

import java.net.InetAddress;
import java.util.Map;

public interface Request {

    String getMethod();

    InetAddress getInetAddress();

    String getIp();

    RequestHead getHeader();

    String getPath();

    String getType();

    Status getStatus();

    Map<Package.PackageType, Package> getPackages();

    Package getPackage(Package.PackageType type);

    String getParameter(String key);

    void setStatus(Status status);
    void setType(String type);
    void setPath(String path);
    void setHeader(RequestHead requestHead);
    void addPackage(Package.PackageType type,Package pack);

}
