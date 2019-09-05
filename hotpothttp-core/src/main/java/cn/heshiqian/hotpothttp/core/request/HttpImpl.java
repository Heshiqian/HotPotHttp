package cn.heshiqian.hotpothttp.core.request;

import cn.heshiqian.hotpothttp.core.addtion.HotPotTools;
import cn.heshiqian.hotpothttp.core.addtion.Log;

import java.net.InetAddress;
import java.util.Arrays;

public class HttpImpl implements Http {

    private Method method;
    private String version;
    private String path;
    private String parameter;
    private String[] head;
    private InetAddress address;

    public HttpImpl(Method method, String version, String path, String parameter, String[] head, InetAddress address) {
        this.method = method;
        this.version = version;
        this.path = path;
        this.parameter = parameter;
        this.head = head;
        this.address = address;
    }

    @Override
    public Method method() {
        return method;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public String parameter() {
        return parameter;
    }

    @Override
    public String[] head() {
        return head;
    }

    @Override
    public InetAddress address() {
        return address;
    }

    @Override
    public String toString() {
        return "New Http:" +
                "method=" + method +
                ", version='" + version + '\'' +
                ", path='" + path + '\'' +
                ", parameter='" + parameter + '\'' +
                ", head=" + Arrays.toString(head);
    }

    public void debug() {
        Log.debug("New Http:" +
                "method=" + method +
                ", version='" + version + '\'' +
                ", path='" + path + '\'');
        Log.debug("Parameter => '" + parameter + '\'');
        Log.debug("Head => " + HotPotTools.getArrayLineByLine(head));
    }
}
