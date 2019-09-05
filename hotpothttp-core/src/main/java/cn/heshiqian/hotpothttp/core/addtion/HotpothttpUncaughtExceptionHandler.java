package cn.heshiqian.hotpothttp.core.addtion;

import cn.heshiqian.hotpothttp.core.proxy.Proxy;

public class HotpothttpUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.err("this thread:"+t.getName()+" throw a uncaught exception");
        Log.err(HotPotTools.printStackTrace(e.getStackTrace()));
        Log.err("========= detail begin =========");
        e.printStackTrace();
        Log.err("========= detail end ===========");
        Log.err("this thread will be interrupt, maybe can turn better :)");
        t.interrupt();
    }
}
