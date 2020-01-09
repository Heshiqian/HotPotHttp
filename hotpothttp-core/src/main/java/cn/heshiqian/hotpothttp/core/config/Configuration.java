package cn.heshiqian.hotpothttp.core.config;

import cn.heshiqian.hotpothttp.core.addtion.Log;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class Configuration {

    private AutoInjectionHelper injectionHelper=new AutoInjectionHelper(this);
    private final static HashMap<String,Object> argMaps=new HashMap<>();

    private static class ConfigurationHolder{
        private static final Configuration cholder=new Configuration();
    }
    public static Configuration getInstance(){
        return ConfigurationHolder.cholder;
    }


    public static final String USER_DIR = "user.dir";
    public static final String WOKRER_DIR = "worker.dir";
    public static final String CONF_DIR="conf.dir";
    public static final String LIB_DIR="lib.dir";
    public static final String NIO_MODE = "core.mode.nio";
    public static final String NORMAL_MODE = "core.mode.normal";
    public static final String IP = "core.ip";
    public static final String PORT = "core.port";
    public static final String BACKLOG = "core.backlog";
    public static final String NIO_BLOCKING = "core.mode.nio.blocking";
    public static final String CACHE_ENABLE = "core.cache";
    public static final String CACHE_MAX = "core.cache.max";
    public static final String CACHE_MODE = "core.cache.mode";

    public static final String REQUEST_FRONT_FACTORY = "core.request.factory";
    public static final String RESPONSE_BACK_FACTORY = "core.response.factory";

    public static final String CLASS_WATCHER_SERVICE="core.file.watcher";
    public static final String CLASS_EVENT_LISTENER="core.file.listener";

    public static final String START_CLASS="SCLZ_KEY";

    public static final String COMMAND_LINE_HTTP_PORT="--port";
    public static final String COMMAND_LINE_HTTP_IP="--ip";
    public static final String COMMAND_LINE_WORKER_PATH="--w";
    public static final String COMMAND_LINE_GUI_MODE="--gui";

    public static <T>T getArg(String paramKey,Class<T> type) {
        Object o = argMaps.get(paramKey);
        if (o == null) return null;
        T cast = null;
        try{
            cast = type.cast(o);
        }catch (Exception e) {
            if (o instanceof Boolean) {
                Boolean o1 = (Boolean) o;
                cast = type.cast(o1);
            }else if(o instanceof Integer){
                Integer o1 = (Integer) o;
                cast = type.cast(o1);
            }else if(o instanceof String){
                String o1 = (String) o;
                if (o1.equalsIgnoreCase("true")||o1.equalsIgnoreCase("false")){
                    boolean b = Boolean.parseBoolean(o1);
                    return type.cast(b);
                }else {
                    try{
                        int i = Integer.parseInt(o1);
                        return type.cast(i);
                    }catch (NumberFormatException nfe){
                        return type.cast(o1);
                    }
                }
            }
        }
        return cast;
    }

    public void analyze(Properties properties){
        Enumeration<Object> keys = properties.keys();
        while (keys.hasMoreElements()){
            Object o = keys.nextElement();
            String key = (String) o;
            //remove useless key
            if (checkEffectiveness(key)) {
                Log.err("key:"+key+" is useless! this cannot add into the system");
                continue;
            }
            String value = properties.getProperty(key, "");
            autoSet(key,value);
        }

        //setup system parameter
        String userDir = System.getProperty("user.dir");
        argMaps.put("debug",false);
        argMaps.put(USER_DIR,userDir);
        argMaps.put(WOKRER_DIR,argMaps.get("WORKER_FOLDER_KEY"));
        argMaps.put(CONF_DIR,argMaps.get("CONF_FOLDER_KEY"));
        argMaps.put(LIB_DIR,argMaps.get("LIB_FOLDER_KEY"));
        argMaps.put(NORMAL_MODE,true);

        convertCommandLineArgs();
        convertOtherParameterToStandard();
    }

    private void convertOtherParameterToStandard() {
        if (argMaps.containsKey("core.debug"))
            argMaps.put("debug",argMaps.get("core.debug"));
    }

    private void convertCommandLineArgs() {
        if (argMaps.containsKey(Configuration.COMMAND_LINE_HTTP_PORT))
            argMaps.put(PORT,argMaps.get(COMMAND_LINE_HTTP_PORT));

        if (argMaps.containsKey(Configuration.COMMAND_LINE_HTTP_IP))
            argMaps.put(IP,argMaps.get(COMMAND_LINE_HTTP_IP));

        if (argMaps.containsKey(Configuration.COMMAND_LINE_WORKER_PATH))
            argMaps.put(WOKRER_DIR,argMaps.get(COMMAND_LINE_WORKER_PATH));

    }

    private void autoSet(String key, String value) {
//        if (!key.startsWith("cn.heshiqian.hotpothttp")){
            //guess does not belong 'HotPotHttp' package

//        }
//        injectionHelper.multiKey(key, value);
        //injection success save to map
        argMaps.put(key, value);
    }

    private boolean checkEffectiveness(String key) {
        if ("cn/heshiqian/hotpothttp/core".equals(key)) return true;
        return false;
    }

    public HashMap<String, Object> getArgMaps() {
        if (Boolean.parseBoolean((String) argMaps.get("debug")))
            return argMaps;
        else
            return null;
    }

    public static void debug() {
        argMaps.put("debug",true);
    }
}
