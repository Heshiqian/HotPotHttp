package cn.heshiqian.hotpothttp.core.plugin.abs;

import cn.heshiqian.hotpothttp.core.plugin.Plugin;
import cn.heshiqian.hotpothttp.core.proxy.PluginProxy;
import cn.heshiqian.hotpothttp.core.pojo.Bundle;
import cn.heshiqian.hotpothttp.core.request.Request;

import java.net.Socket;
import java.util.Map;

public abstract class WorkerPlugin extends Thread implements Plugin, PluginProxy {

    protected Socket socket;
    protected Request request;

    @Override
    public void proxy(Map<Bundle.Type,Bundle> bundleMap){
        Bundle socket = bundleMap.get(Bundle.Type.SOCKET);
        Bundle request = bundleMap.get(Bundle.Type.REQUEST);
        this.socket = socket.get();
        this.request = request.get();
    }

    public abstract void worker(Socket socket, Request request);

    @Override
    public void run() {
        worker(socket,request);
    }
}
