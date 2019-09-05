package cn.heshiqian.hotpothttp.core;


import cn.heshiqian.hotpothttp.core.addtion.HotPotRejectedExceptionHandlerCallback;
import cn.heshiqian.hotpothttp.core.addtion.HotPotThreadNameProcessFactory;
import cn.heshiqian.hotpothttp.core.addtion.HotPotTools;
import cn.heshiqian.hotpothttp.core.addtion.Log;
import cn.heshiqian.hotpothttp.core.config.Configuration;
import cn.heshiqian.hotpothttp.core.exception.EngineInitException;
import cn.heshiqian.hotpothttp.core.exception.HotPotHttpException;
import cn.heshiqian.hotpothttp.core.factory.RequestFrontFactory;
import cn.heshiqian.hotpothttp.core.factory.ResponseBackFactory;
import cn.heshiqian.hotpothttp.core.proxy.Proxy;
import cn.heshiqian.hotpothttp.core.request.HttpHelper;
import cn.heshiqian.hotpothttp.core.request.Request;
import cn.heshiqian.hotpothttp.core.response.HttpSend;
import cn.heshiqian.hotpothttp.core.response.Response;
import cn.heshiqian.hotpothttp.core.response.ResponseHelper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HotPotHttp {

    /**
     * 默认的线程池初始化大小
     */
    public static final int INIT_THREAD_POOL_SIZE = 5;
    /**
     * 默认的线程池最大大小
     */
    public static final int MAX_THREAD_POOL_SIZE = 50;


    private Integer backlog;
    private InetAddress ip;
    private int port;
    private String serverName;


    private ServerSocket serverSocket;

    private ThreadPoolExecutor threadPoolExecutor;
    private BlockingQueue<Runnable> threadQueue=new ArrayBlockingQueue<>(50);
    private boolean running = true;
    private final Proxy proxy;

    public HotPotHttp() {
        threadPoolExecutor = newThreadPool();
        backlog = (backlog = Configuration.getArg(Configuration.BACKLOG, Integer.class))==null?50:backlog;
        proxy = Proxy.getInstance();
    }

    private ThreadPoolExecutor newThreadPool() {
        HotPotRejectedExceptionHandlerCallback handler = new HotPotRejectedExceptionHandlerCallback();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(INIT_THREAD_POOL_SIZE,
                MAX_THREAD_POOL_SIZE ,5, TimeUnit.SECONDS,
                threadQueue,
                new HotPotThreadNameProcessFactory(),
                handler
                );
        handler.setRejectedEvent(new HotPotRejectedExceptionHandlerCallback.RejectedEvent() {
            @Override
            public void OnRejectEventExecute(Runnable r, ThreadPoolExecutor executor) {
                Log.debug("[WARING "+System.currentTimeMillis()+"] max thread pool size! this thread has be reject -> "+r.toString()+". try force add to queue.");
                threadQueue.add(r);
            }
        });
        return executor;
    }

    public void start(){
        try {
            Log.debug("init server socket");
            serverSocket = new ServerSocket(port,backlog,ip);
            Log.log("pre start thread pool size");
            threadPoolExecutor.prestartCoreThread();
            Log.log("Server is running on port:"+port);
            Log.log("Please access \"http://"+ip.getHostAddress()+":"+port+"/\", for more detail");
        } catch (IOException e) {
            shutdown();
            throw new EngineInitException("start error, maybe port was bind!",e);
        }
        //两个处理器获取一次就行了
        RequestFrontFactory frontProcessor = proxy.getFrontProcessor();
        ResponseBackFactory backProcessor = proxy.getBackProcessor();
        while (running){
            try {
                Socket accept = serverSocket.accept();
                Log.debug("收到请求");
                if (accept.isClosed())
                    throw new IllegalStateException("socket is close");
                Log.debug("开始接收请求");
                Request request = frontProcessor.receive(HttpHelper.analyze(accept));
                Log.debug("请求接收完");
                if (request == null)
                    throw new HotPotHttpException("front processor return request is null, please check your request factory.",
                            new NullPointerException("request is null"));
                Log.debug(request.toString());
                //循环request级别插件
                Log.debug("request插件");
                proxy.thoughtRequest(request);
                Log.debug("request插件结束");
                //开始对返回值做解析
                String method = request.getMethod();
                if (!method.equals("GET")){
                    //对于不是GET请求现在默认返回不支持
                    ResponseHelper.notSupport(accept);
                    continue;
                }
                Log.debug("解析response");
                Response response = ResponseHelper.parseResponse(request);
                Log.debug("解析response结束");
                //response级别插件
                Log.debug("response插件");
                proxy.thoughtResponse(response);
                Log.debug("response插件结束");
                try{
                    HttpSend httpSend;
                    httpSend = backProcessor.beforeSend(response);
                    if (httpSend==null)
                        throw new HotPotHttpException("HttpSend is null, need this to send stream");
                    ResponseHelper.send(httpSend, accept.getOutputStream());
                    accept.close();
                }catch (Exception e){
                    Log.err("response send error, path:"+response.getPath());
                    Log.debug("stack trace:\n"+HotPotTools.printStackTrace(e.getStackTrace()));
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Log.err("");
                e.printStackTrace();
            }
        }

    }


    public void shutdown(){
        running=false;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

}
