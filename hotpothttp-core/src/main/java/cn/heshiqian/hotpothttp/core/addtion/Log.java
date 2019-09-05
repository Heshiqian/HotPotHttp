package cn.heshiqian.hotpothttp.core.addtion;

import cn.heshiqian.hotpothttp.core.config.Configuration;

public final class Log {


    public static void war(String msg){
        System.out.println("[Waring] "+msg);
    }

    public static void log(String msg){
        String name = Thread.currentThread().getName();
        System.out.println("["+name+"] "+msg);
    }

    public static void err(String msg){
        String name = Thread.currentThread().getName();
        System.err.println("["+name+"] "+msg);
    }

    public static void debug(String msg){
        if (Configuration.getArg("debug",Boolean.class)!=null&&Configuration.getArg("debug",Boolean.class)==true){
            String name = Thread.currentThread().getName();
            System.out.println("["+name+" 'Debug'] "+msg);
        }
    }
}
