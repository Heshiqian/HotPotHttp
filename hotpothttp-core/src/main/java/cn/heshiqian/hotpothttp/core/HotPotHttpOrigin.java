//package cn.heshiqian.hotpothttp.core;
//
//import cn.heshiqian.hotpothttp.core.addtion.HotPotRejectedExceptionHandlerCallback;
//import cn.heshiqian.hotpothttp.core.addtion.HotPotThreadNameProcessFactory;
//import cn.heshiqian.hotpothttp.core.addtion.HotPotTools;
//import cn.heshiqian.hotpothttp.core.addtion.Log;
//import cn.heshiqian.hotpothttp.core.config.Configuration;
//import cn.heshiqian.hotpothttp.core.request.Request;
//import cn.heshiqian.hotpothttp.core.request.RequestHeadAnalyzer;
//import cn.heshiqian.hotpothttp.core.request.Status;
//import cn.heshiqian.hotpothttp.core.response.*;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.net.InetAddress;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.Map;
//import java.util.concurrent.*;
//
//@SuppressWarnings("WeakerAccess")
//public class HotPotHttpOrigin {
//
//    /**
//     * thread pool init size
//     * normally is 5, maybe change to can configured
//     * @see ThreadPoolExecutor constructor method get more info
//     */
//    public static final int INIT_THREAD_POOL_SIZE = 5;
//    /**
//     * thread pool max size
//     * normally is 50, maybe change to can configure
//     * @see ThreadPoolExecutor constructor method get more info
//     */
//    public static final int MAX_THREAD_POOL_SIZE = 50;
//
//    /**
//     * starter class in map key;
//     */
//    private static final String START_CLASS="SCLZ_KEY";
//
//    /**
//     * the {@link HotPotHttpOrigin} instantiated object
//     */
//    private static HotPotHttpOrigin client;
//
//    /**
//     * the daemon thread, used for watched hotPotHttpRunnerThread {@link HotPotHttpRunnerThread} thead.
//     * runner thread maybe throw {@link NullPointerException,IOException}, so need watch service is normal running.
//     * (in addition, the thread may be in a wait state, that is, the socket is waiting for the receiving state.[google translate])
//     */
//    protected static final Thread daemon;
//    /**
//     * the worker thread pool, on receive one request the thread will be start one times, then put into thread pool.
//     * {@link ThreadPoolExecutor} see more usage info
//     */
//    protected static final ThreadPoolExecutor threadPool;
//    /**
//     * the thread pool runtime environment need.
//     * a blocking queue, init capacity is 20, also it's maybe change to can configured.
//     * @see ThreadPoolExecutor constructor method get more info
//     */
//    protected static final BlockingQueue<Runnable> threadQueue=new ArrayBlockingQueue<Runnable>(50);
//    /**
//     * the thread factory, used to named for thread.
//     * {@link HotPotThreadNameProcessFactory}
//     */
//    private static final HotPotThreadNameProcessFactory hotPotThreadNameProcessFactory;
//    /**
//     * the thread pool execution error message handel. if have exception thrown this class will callback main thread.
//     * this field is instantiated by sub class {@link HotPotRejectedExceptionHandlerCallback}
//     */
//    private static final RejectedExecutionHandler exceptionHandler;
//    /**
//     * exit flag. just a flag.
//     */
//    private static boolean exitFlag=true;
//
//    static {
//        daemon=new Thread(){
//            @Override
//            public void run() {
//                HotPotTools.printThreadStartNotify(HotPotTools.getThreadName());
//                while (exitFlag){
//                    HotPotTools.threadSleepOnNoExecption(6*10*1000);
//                    //daemon wake check
//                    if (!client.hotPotHttpRunnerThread.isAlive()){
//                        //If runner thread dead, the daemon will exit this application.
//                        System.err.println("[ERROR] The daemon detected that the main thread is dead, please restart the server! Maybe an error was thrown.");
//                        System.err.println("[ERROR] Server will safety exit by now.");
//                        threadQueue.clear();
//                        threadPool.shutdownNow();
//                        break;
//                    }
//                }
//                interrupt();//tag thread dead.
//            }
//        };
//        daemon.setName("HotPot Daemon Thread");
//        daemon.setDaemon(true);
//        hotPotThreadNameProcessFactory = new HotPotThreadNameProcessFactory();
//        exceptionHandler =new HotPotRejectedExceptionHandlerCallback().setRejectedEvent(new HotPotRejectedExceptionHandlerCallback.RejectedEvent() {
//            @Override
//            public void OnRejectEventExecute(Runnable r, ThreadPoolExecutor executor) {
//                Log.log("reject "+r);
//            }
//        });
//        threadPool=new ThreadPoolExecutor(INIT_THREAD_POOL_SIZE,MAX_THREAD_POOL_SIZE,5, TimeUnit.SECONDS
//                ,threadQueue,hotPotThreadNameProcessFactory, exceptionHandler);
//        //not prestart here
////        threadPool.prestartCoreThread();
//    }
//
//    /**
//     * the client name. default is 'MyHttpServer'
//     */
//    private String name;
//    /**
//     * the http server running port
//     */
//    private int port;
//    /**
//     * the http server running ip
//     */
//    private String ip;
//    /**
//     * the server socket runner thread
//     * {@link HotPotHttpRunnerThread}
//     */
//    private HotPotHttpRunnerThread hotPotHttpRunnerThread;
//    /**
//     * the http request analyzer.
//     * @see RequestHeadAnalyzer see more detail.
//     */
//    private static RequestAnalyzer analyzer;
//    private static Configuration configuration;
//
//    /**
//     * HotPotHttp constructor
//     * @param port socket listen port. ip default is 'localhost','127.0.0.1'
//     */
//    public HotPotHttpOrigin(int port) {
//        this(port,null);
//    }
//
//    /**
//     * HotPotHttp constructor
//     * @param port socket listen port
//     * @param ip socket listen ip
//     */
//    public HotPotHttpOrigin(int port, String ip){
//        Log.log("port = [" + port + "], ip = [" + ip + "]");
//        this.port = port;
//        this.ip = ip;
//    }
//
//    /**
//     * set name for HotPotHttp server
//     * @param name a name
//     */
//    public void setName(String name){
//        this.name = name;
//    }
//
//    /**
//     * start runner thread. main thread is not necessary and no suggested to run socket.
//     * the thread only run by one times.
//     * if port or address already in use, can throw {@link java.net.BindException}
//     * this thread will into WAIT state, on socket wait to accept.
//     * so, this thread may let main thread into fake death state.
//     */
//    public void start(){
//        if ((hotPotHttpRunnerThread==null)||!hotPotHttpRunnerThread.isRunning()){
//            hotPotHttpRunnerThread = new HotPotHttpRunnerThread();
//            hotPotHttpRunnerThread.setHotPotHttp(this);
//            hotPotHttpRunnerThread.start();
//        }
//        else
//            hotPotHttpRunnerThread.notify();
//    }
//
//    /**
//     * this method invoke by {@link HotPotHttpApplicationStarter},and is used to initialize the http configuration.
//     * @param argsmap parameter map {@link java.util.HashMap}
//     */
//    public static void init(Map<String,String> argsmap){
//        analyzer = RequestHeadAnalyzer.getInstance();
//        configuration = Configuration.getInstance();
//        //daemon thread start
//        daemon.start();
//        //release all parameter
//
//        String portString = Configuration.getArg(Configuration.PORT,String.class);
//        String ipString = Configuration.getArg(Configuration.IP,String.class);
//
//        int port = Integer.parseInt(portString == null ? "8080" : portString);
//        String ips = ipString == null ? "127.0.0.1" : ipString;
//
//        if (client==null){
//            client=new HotPotHttpOrigin(port,ips);
//            client.setName("MyHttpServer");
//        }else {
//            throw new RuntimeException("Http Server Was Created !");
//        }
//
//        //plugin AOP
//        Log.log("start scanning plugin...");
//
//        //start server service
//        client.start();
//
//    }
//
//    public ThreadPoolExecutor getThreadPool(){
//        return threadPool;
//    }
//
//    protected class HotPotHttpRunnerThread extends Thread{
//
//        private boolean isRunning;
//        private HotPotHttpOrigin hotPotHttp;
//        private ServerSocket serverSocket;
//
//        public HotPotHttpRunnerThread() {
//            isRunning=true;
//        }
//
//        public void setHotPotHttp(HotPotHttpOrigin hotPotHttp) {
//            this.hotPotHttp = hotPotHttp;
//        }
//
//        public boolean isRunning() {
//            return isRunning;
//        }
//
//        @Override
//        public void run() {
//            try{
//                Integer backlogSize;
//                backlogSize = (backlogSize = Configuration.getArg(Configuration.BACKLOG, Integer.class))==null?1000:backlogSize;
//
//                serverSocket = new ServerSocket(hotPotHttp.port, backlogSize, InetAddress.getByName(hotPotHttp.ip));
//                Log.log("pre start core thread.");
//                threadPool.prestartCoreThread();
//                Log.log("Server is running on port:"+hotPotHttp.port);
//                Log.log("Please access \"http://"+ip+":"+hotPotHttp.port+"/\", for more detail");
//                while (isRunning){
//                    Socket accept = serverSocket.accept();
//                    Request request = analyzer.analyzeHttp(accept);
//                    //do default worker
//                    threadPool.execute(new HotPotWorkerThread(accept, request));
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//                Log.err("http start error, please see stack trace get more info!");
//            }
//        }
//    }
//
//    protected class HotPotWorkerThread extends Thread{
//
//        private Socket socket;
//        private Request request;
//
//        public HotPotWorkerThread(Socket socket,Request request) {
//            this.socket=socket;
//            this.request=request;
//        }
//
//        @Override
//        public void run() {
//            Status status = request.getStatus();
//            request.getCookieManager().printCookies();
//            OutputStream outputStream;
//            try {
//                outputStream = socket.getOutputStream();
//            }catch (IOException e){
//                e.printStackTrace();
//                Log.err("get socket output stream error.");
//                return;//stop running.
//            }
//            try{
//                switch (status) {
//                    case OK:
//                        Response analyze = ResponseAnalyzerImpl.getInstance().analyze(request);
//                        //async socket return
//                        ResponseHelper.send(analyze,outputStream);
//                        break;
//                    case NONE_HEAD:
//                    case BODY_ERROR:
//                    case HEAD_ERROR:
//                    case HTTP_HEAD_ERROR:
//                    default:
//                        Response response = new ResponseBuilder()
//                                .setResponseCode(200)
//                                .setContentType("text/plain")
//                                .setCharacterEncoding("utf-8")
//                                .build();
//                        ResponseHelper.send(response,outputStream);
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//                ResponseHelper.send500(outputStream);
//            }finally {
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//}
