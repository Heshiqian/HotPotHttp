package cn.heshiqian.hotpothttp.core;

import cn.heshiqian.hotpothttp.core.addtion.CommandLineParser;
import cn.heshiqian.hotpothttp.core.addtion.Log;
import cn.heshiqian.hotpothttp.core.config.Configuration;
import cn.heshiqian.hotpothttp.core.exception.EngineFireErrorExcption;
import cn.heshiqian.hotpothttp.core.proxy.PrivateProxyManage;
import cn.heshiqian.hotpothttp.core.proxy.Proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HotPotHttpApplicationStarter {

    /**
     * @deprecated field no longer support
     * use Configuration.getArg("debug",Boolean.class)
     */
    private static final boolean DEBUG=false;
    //Starter Class(Clazz) -> SCLZ
    private static final String START_CLASS="SCLZ_KEY";

    private static final String WORKER_FOLDER="WORKER_FOLDER_KEY";
    private static final String CONF_FOLDER="CONF_FOLDER_KEY";
    private static final String LIB_FOLDER="LIB_FOLDER_KEY";

    private static final Configuration configuration = Configuration.getInstance();

    private static Map<String, String> argsmap=new HashMap<>();
    private static Proxy proxy;

    /**
     * default starter
     */
    public static void main(String[] args) { run(args,HotPotHttpApplicationStarter.class); }

    public static void run(String[] args,Class starterClass){
        argsmap.put(START_CLASS,starterClass.getTypeName());
        run(args);
    }

    private static void run(String[] args) {
        parseArgs(args);
        setupEnvironment();
//        System.out.println("args = [" + argsmap.toString() + "]");
//        HotPotHttpOrigin.init(argsmap);
        Proxy.proxy();
        Proxy.scanLib();
        if (!isGUIMode()) {
            $$FIRE_THE_ENGINE$$();
        }else {
            try {
                invokeGui();
            } catch (Exception e) {
                e.printStackTrace();
                Log.err("GUI mode start failure. don't add [--gui] args and try again.");
                shutdown();
            }
        }
    }

    private static void invokeGui() throws Exception{
        Class<?> guiStarter;
        guiStarter = PrivateProxyManage.getInstance().onlyGetClass("cn.heshiqian.hotpothttp.gui.GUI");
        Object starter = guiStarter.newInstance();
        Method gui = guiStarter.getMethod("gui", Map.class);
        gui.invoke(starter,Configuration.getInstance().getArgMaps());
    }

    private static boolean isGUIMode() {
        String gui = Configuration.getArg(Configuration.COMMAND_LINE_GUI_MODE, String.class);
        return gui!=null && gui.equals("GUI");
    }

    private static void parseArgs(String[] args) {
        CommandLineParser commandLineParser = new CommandLineParser();
        Map<String, String> parse = commandLineParser.parse(args);
        argsmap.putAll(parse);
    }

    private static void setupEnvironment() {
        Log.log("setup environment.");
        String userDir = System.getProperty("user.dir");
        File file = new File(userDir + File.separator + "server.properties");
        Properties properties=new Properties();//init field
        InputStream inputStream;
        try {
            File websiteFolder = new File(userDir + File.separator + "website");
            File confFolder = new File(userDir + File.separator + "conf");
            File libFolder = new File(userDir + File.separator + "lib");
            if (!websiteFolder.exists()) {
                Log.log("create website folder... path:"+websiteFolder.getAbsolutePath());
                boolean mkdir = websiteFolder.mkdir();
                if (!mkdir){
                    Log.err("create website folder failure. maybe no access permission!");
                    Log.err("will use internal html page, some function will not be used.");
                }
            }
            if (!confFolder.exists()){
                Log.log("create conf folder... path:"+confFolder.getAbsolutePath());
                boolean mkdir = confFolder.mkdir();
                if (!mkdir){
                    Log.err("create conf folder failure. maybe no access permission!");
                }
            }
            if (!libFolder.exists()){
                Log.log("create lib folder... path:"+libFolder.getAbsolutePath());
                boolean mkdir = libFolder.mkdir();
                if (!mkdir){
                    Log.err("create lib folder failure. maybe no access permission!");
                }
            }
            properties.setProperty(WORKER_FOLDER,websiteFolder.getAbsolutePath());
            properties.setProperty(CONF_FOLDER,confFolder.getAbsolutePath());
            properties.setProperty(LIB_FOLDER,libFolder.getAbsolutePath());
            if (file.exists()) {
                inputStream = new FileInputStream(file);
            }else {
                Log.log("not found outside configure file, will use internal properties");
                inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("server.properties");
            }
            if (inputStream == null) {
                Log.err("cannot found file 'server.properties', maybe name not equal 'server.properties' or file not readable");
                Log.err("it's will be use command line args");
            }else {
                properties.load(inputStream);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        properties.putAll(argsmap);
        configuration.analyze(properties);
//        if (DEBUG)
//            Configuration.debug();
        argsmap.clear();
        argsmap=null;
    }

    private static void $$FIRE_THE_ENGINE$$() {
        //添加默认的线程异常未处理消息
        Thread.setDefaultUncaughtExceptionHandler(new MainThreadUncaughtExceptionHandler());
        proxy = Proxy.getInstance();
        proxy.preLoadStaticFileList();
        proxy.preInitAllClass();
        HotPotHttp hotPotHttp = new HotPotHttp();
        InetAddress netAddress;
        String ip = Configuration.getArg(Configuration.IP, String.class);
        if (ip == null) ip = "localhost";
        try{
            netAddress = InetAddress.getByName(ip);
            if (netAddress==null)
                netAddress = InetAddress.getLocalHost();
        }catch (Exception e){
            throw new EngineFireErrorExcption("ip is invalid.",e);
        }
        Integer port = Configuration.getArg(Configuration.PORT, Integer.class);
        if (port==null) port=8222;
        hotPotHttp.setIp(netAddress);
        hotPotHttp.setPort(port);
        hotPotHttp.setServerName(Version.name);
        proxy.fireEngine(hotPotHttp);
    }

    private static class MainThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            Log.err("Uncaught Exception Waring!");
            Log.err("in ["+t.getName()+":"+t.getId()+"] thread");
            e.printStackTrace();
            Log.err("This can make system extreme unstable, so we need shutdown this system.");
            Log.err("Sorry about that.");
            Log.err(":(");
            shutdown();
        }
    }

    private static void shutdown(){
        Log.log("shutdown system...");
        Proxy.getInstance().getDaemon().shutdown();
        System.exit(0);
    }
}