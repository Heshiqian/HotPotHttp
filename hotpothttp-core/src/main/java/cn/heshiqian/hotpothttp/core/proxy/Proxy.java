package cn.heshiqian.hotpothttp.core.proxy;

import cn.heshiqian.hotpothttp.core.HotPotHttp;
import cn.heshiqian.hotpothttp.core.addtion.HotPotTools;
import cn.heshiqian.hotpothttp.core.addtion.HotpothttpUncaughtExceptionHandler;
import cn.heshiqian.hotpothttp.core.addtion.Log;
import cn.heshiqian.hotpothttp.core.addtion.ThreadUnCatchHandle;
import cn.heshiqian.hotpothttp.core.config.Configuration;
import cn.heshiqian.hotpothttp.core.factory.RequestFrontFactory;
import cn.heshiqian.hotpothttp.core.factory.ResponseBackFactory;
import cn.heshiqian.hotpothttp.core.request.Request;
import cn.heshiqian.hotpothttp.core.request.RequestProcessor;
import cn.heshiqian.hotpothttp.core.plugin.Plugins;
import cn.heshiqian.hotpothttp.core.plugin.ProxyTarget;
import cn.heshiqian.hotpothttp.core.pojo.SimplePluginHolder;
import cn.heshiqian.hotpothttp.core.response.Response;
import cn.heshiqian.hotpothttp.core.response.ResponseProcessor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class Proxy {

    public static final int VERSION = 0;

    private static final AtomicInteger serverCount = new AtomicInteger(1);

    @Deprecated
    private static final String DEAFULE_WATCHER_LISTENER_CLASS_NAME="cn.heshiqian.hotpothttp.core.proxy.DefaultWatcherEventListener";

    private static final String DEAFULE_FILE_WATCHER_CLASS_NAME=DefaultFileWatcher.class.getTypeName();
    private static final String DEAFULR_FRONT_FACTORY = RequestFrontFactory.class.getTypeName();
    private static final String DEAFULR_BACK_FACTORY = ResponseBackFactory.class.getTypeName();

    private PrivateProxyManage proxyManage = PrivateProxyManage.getInstance();

    private static Proxy proxy = new Proxy();
    private static final Map<ProxyTarget, LinkedList<SimplePluginHolder>> proxyPluginList = new HashMap<>();

    //请求前置控件类名
    private String RFFClassStr;
    private String RBFClassStr;

    public static Proxy getInstance() {
        return proxy;
    }

    private static ProxyDaemonThread daemon;

    public static void proxy() {
        daemon = new ProxyDaemonThread();
        daemon.setName("proxy-daemon-thread");
        daemon.setUncaughtExceptionHandler(new ThreadUnCatchHandle());
        daemon.start();
    }

    public ProxyDaemonThread getDaemon(){
        return daemon;
    }









    public void fireEngine(HotPotHttp hotPotHttp){
        HotPotHttpRunnerProxyThread thread = new HotPotHttpRunnerProxyThread();
        thread.setDaemon(true);
        thread.setName("hotpothttp-"+serverCount.getAndIncrement());
        thread.setUncaughtExceptionHandler(new HotpothttpUncaughtExceptionHandler());
        thread.setHotPotHttp(hotPotHttp);
        thread.start();
    }

    public void preInitAllClass(){

        String starterClassStr = Configuration.getArg(Configuration.START_CLASS, String.class);
        Class startClass = null;
        try {
            startClass = proxyManage.onlyGetClass(starterClassStr);
        } catch (ClassNotFoundException e) {
            Log.err("starter class not found.");
            HotPotTools.exitSystem(HotPotTools.ExitCode.ERROR);
        }
        if (startClass == null) {
            Log.debug("unknown error.");
            HotPotTools.exitSystem(HotPotTools.ExitCode.SOMETINGBREAK);
        }

        Method[] starterMethods = startClass.getDeclaredMethods();

        for (Method method:starterMethods){
            Plugins plugins = method.getAnnotation(Plugins.class);
            if (plugins==null)continue;
            Class plugin = plugins.plugin();
            String pluginName = plugins.pluginName();
            ProxyTarget target = plugins.target();
            HotPotTools.Timer.startTimer();
            Object o;
            try {
                o = proxyManage.loadClassButNoAddIntoList(plugin);
            } catch (Exception e) {
                Log.err("class:"+plugin.getTypeName()+" create error!");
                Log.err(HotPotTools.printStackTrace(e.getStackTrace()));
                HotPotTools.Timer.resetTimer();
                continue;
            }
            Log.debug("load class ["+plugin.getTypeName()+"] use time:"+HotPotTools.Timer.stopTimerAndGetTime());

            SimplePluginHolder simplePluginHolder = new SimplePluginHolder();
            simplePluginHolder.setInstance(o);
            simplePluginHolder.setPluginClass(plugin);
            simplePluginHolder.setPluginName(pluginName);

            //如果对应的list没有实例化，实例化后返回
            LinkedList<SimplePluginHolder> list = proxyPluginList.computeIfAbsent(target, k -> new LinkedList<>());

            list.add(simplePluginHolder);

        }
        Log.debug(HotPotTools.getMapLineByLine(proxyPluginList,"代理列表"));


        if ((RFFClassStr = Configuration.getArg(Configuration.REQUEST_FRONT_FACTORY, String.class))==null){
            Log.debug("front processor custom not exist! use default.");
            RFFClassStr = DEAFULR_FRONT_FACTORY;
        }
        try {
            Object rffInstance = proxyManage.loadClass(RFFClassStr);
            if (!proxyManage.checkInstanceOf(rffInstance, RequestProcessor.class)) {
                throw new IllegalArgumentException("your custom request factory class:["+RFFClassStr+"] is " +
                        "not implements class:["+RequestProcessor.class.getTypeName()+"]");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            HotPotTools.exitSystem(HotPotTools.ExitCode.ERROR);
        }
        if ((RBFClassStr = Configuration.getArg(Configuration.RESPONSE_BACK_FACTORY, String.class))==null){
            Log.debug("back processor custom not exist! use default.");
            RBFClassStr = DEAFULR_BACK_FACTORY;
        }
        try {
            Object rbfInstance = proxyManage.loadClass(RBFClassStr);
            if (!proxyManage.checkInstanceOf(rbfInstance, ResponseProcessor.class)) {
                throw new IllegalArgumentException("your custom request factory class:["+RBFClassStr+"] is " +
                        "not implements class:["+ ResponseProcessor.class.getTypeName()+"]");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            HotPotTools.exitSystem(HotPotTools.ExitCode.ERROR);
        }
    }

    public void preLoadStaticFileList(){
        String watcherClass;
        if ((watcherClass = Configuration.getArg(Configuration.CLASS_WATCHER_SERVICE, String.class)) == null)
            watcherClass = DEAFULE_FILE_WATCHER_CLASS_NAME;
        try {
            proxyManage.loadClass(watcherClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            HotPotTools.exitSystem(HotPotTools.ExitCode.ERROR);
        }

        FileWatcher fileWatcher = proxyManage.getInstance(watcherClass, FileWatcher.class);

        if (fileWatcher == null){
            Log.war("Your custom watcher, can't get form ProxyManage, maybe 2nd parameter wrong!(need FileWatcher.class or subclass)");
            Log.war("This time will keep used default watcher.");
            fileWatcher = useDefaultWatcher(watcherClass);
        }

        Log.log("FileWatcher instance type ["+fileWatcher.getClass().getTypeName()+"] ver:"+fileWatcher.getVersion()+" desc:"+fileWatcher.versionDescription());
        fileWatcher.getWatchService();
        Log.log("start file watcher, ready to scan file.");
        fileWatcher.startWatch();
    }

    /**
     * @deprecated 因为其他问题，事件监听器不再使用
     */
    @Deprecated
    private WatchEventListener useDefaultEventListener(String oldClassName) {
        Object o = null;
        proxyManage.removeClass(oldClassName);
        try {
            o = proxyManage.loadClass(DEAFULE_WATCHER_LISTENER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            HotPotTools.exitSystem(HotPotTools.ExitCode.ERROR);
        }
        return (WatchEventListener) o;
    }

    private FileWatcher useDefaultWatcher(String oldClassName) {
        Object o = null;
        proxyManage.removeClass(oldClassName);
        try {
            o = proxyManage.loadClass(DEAFULE_FILE_WATCHER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            HotPotTools.exitSystem(HotPotTools.ExitCode.ERROR);
        }
        return (FileWatcher) o;
    }

    public RequestFrontFactory getFrontProcessor(){
        return proxyManage.getInstance(RFFClassStr, RequestFrontFactory.class);
    }

    public ResponseBackFactory getBackProcessor(){
        return proxyManage.getInstance(RBFClassStr, ResponseBackFactory.class);
    }

    public void thoughtRequest(Request request) {
        LinkedList<SimplePluginHolder> list = proxyPluginList.get(ProxyTarget.REQUEST);
        if (list==null) return;
        Iterator<SimplePluginHolder> iterator = list.iterator();
        while (iterator.hasNext()){
            SimplePluginHolder holder = iterator.next();
            Object instance = holder.getInstance();
            try {
                Method process = instance.getClass().getMethod("process", Request.class);
                Boolean invoke = (Boolean) process.invoke(instance, request);
                if (invoke) break;
            } catch (NoSuchMethodException nsme){
                Log.err("cannot invoke plugin, because this plugin is not class:[cn.heshiqian.hotpothttp.core.plugin.abs.RequestPlugin] subclass");
                Log.err("plugin name:'"+holder.getPluginName()+"' plugin class:["+holder.getPluginClass().getTypeName()+"]");
            } catch (Exception e){
                Log.err("plugin run error.");
                Log.err("in plugin:'"+holder.getPluginName()+"' class:["+holder.getPluginClass().getTypeName()+"]");
                Log.err("======== stack trace =========");
                e.printStackTrace();
                Log.err("==============================");
            }
        }
    }

    public void thoughtResponse(Response response) {

    }

    public static class HotPotHttpRunnerProxyThread extends Thread{

        private HotPotHttp hotPotHttp;

        public void setHotPotHttp(HotPotHttp hotPotHttp) {
            this.hotPotHttp = hotPotHttp;
        }

        public HotPotHttp getHotPotHttp() {
            return hotPotHttp;
        }

        @Override
        public void run() {
            hotPotHttp.start();
        }
    }

    public static class ProxyDaemonThread extends Thread{

        boolean running=true;
        private Proxy proxy = Proxy.getInstance();

        public void shutdown(){
            running=false;
            interrupt();
        }

        @Override
        public void run() {
            while (running){
                HotPotTools.threadSleepOnNoExecption(5000);
            }
            Log.log("proxy daemon exit...");
        }
    }


}
