package cn.heshiqian.hotpothttp.core.addtion;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class HotPotRejectedExceptionHandlerCallback implements RejectedExecutionHandler {


    private static final Object lock=new Object();
    private RejectedEvent rejectedEvent;

    public HotPotRejectedExceptionHandlerCallback() {
    }

    public HotPotRejectedExceptionHandlerCallback setRejectedEvent(RejectedEvent event){
        rejectedEvent = event;
        return this;
    }

    /**
     * receive error
     * @param r a thread not success into thread pool
     * @param executor thread pool
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        synchronized (lock){
            rejectedEvent.OnRejectEventExecute(r,executor);
        }
    }

    public interface RejectedEvent{
        void OnRejectEventExecute(Runnable r, ThreadPoolExecutor executor);
    }

}
