package cn.heshiqian.hotpothttp.core.addtion;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class HotPotThreadNameProcessFactory implements ThreadFactory {
    private final AtomicInteger tagNum=new AtomicInteger(1);
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r,"hotpot-worker-thread-"+tagNum.getAndIncrement());
        Log.debug("worker thread:"+t.getName()+" is running.");
        return t;
    }
}
