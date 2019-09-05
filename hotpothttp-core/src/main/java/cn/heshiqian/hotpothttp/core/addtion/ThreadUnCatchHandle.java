package cn.heshiqian.hotpothttp.core.addtion;

public class ThreadUnCatchHandle implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.err.print("Thread:[" + t.getName() + "] Print Exception StackTrace: \n" + HotPotTools.printStackTrace(e.getStackTrace()));
    }
}