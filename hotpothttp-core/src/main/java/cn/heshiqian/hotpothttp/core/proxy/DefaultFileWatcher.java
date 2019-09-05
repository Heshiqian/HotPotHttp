package cn.heshiqian.hotpothttp.core.proxy;

import cn.heshiqian.hotpothttp.core.FileCache;
import cn.heshiqian.hotpothttp.core.addtion.HotPotTools;
import cn.heshiqian.hotpothttp.core.addtion.Log;
import cn.heshiqian.hotpothttp.core.config.Configuration;
import cn.heshiqian.hotpothttp.core.pojo.SimpleFilePojo;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Timer;
import java.util.TimerTask;

public class DefaultFileWatcher implements FileWatcher {

    private WatchService service;
    private FileCache fileCache = FileCache.getInstance();
    private boolean debug;

    @Override
    public WatchService getWatchService() {
        debug = Configuration.getArg("debug",Boolean.class)!=null||Configuration.getArg("debug",Boolean.class)==true;
        newService();
        return service;
    }

    private void newService() {
        try {
            service = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            Log.err("File watch service start error, please see stack trace");
            Log.err(HotPotTools.printStackTrace(e.getStackTrace()));
        }
    }

    @Override
    public void startWatch() {
        WatcherThread watcherThread = new WatcherThread(service);
        Thread hook = new Thread(watcherThread::shutdown);
        Runtime.getRuntime().addShutdownHook(hook);
        watcherThread.setName("file-watch-thread");
        watcherThread.start();
    }

    private class WatcherThread extends Thread{

        private WatchService service;
        private boolean run = true;
        private String workerDirPath;
        private File file;

        private long threeSecTimer = 0;
        private boolean startTimer = false;

        private Timer timer;
        private TimerTask timerTask;

        private TimerTask getTimerTask() {
            return new TimerTask() {
                @Override
                public void run() {
                    setName("rescan-file-thread");
                    HotPotTools.Timer.startTimer();
                    redoScan();
                    HotPotTools.Timer.stopTimer("rescan file done");
                }
            };
        }

        private WatcherThread(WatchService service) {
            this.service=service;
        }

        private void shutdown(){
            run=false;
            interrupt();
        }

        @Override
        public void run() {
            HotPotTools.Timer.startTimer();
            workerDirPath = Configuration.getArg(Configuration.WOKRER_DIR, String.class);
            if (workerDirPath ==null)
                throw new NullPointerException("worker dir is null? normally this can't be null, something wrong!");

            file = new File(workerDirPath);
            if (!file.isDirectory()) {
                Log.err("worker dir:'"+ file.getAbsolutePath()+"' is not directory.");
                Log.err("watcher service is will not run in this time");
                return;
            }

            doScan(file);

            HotPotTools.Timer.stopTimer("file init scan");
            if (debug){
                fileCache.a();
            }

            if (makeAllDirectoryRegisterEvent(workerDirPath)) return;

            while (run) {
                try {
                    WatchKey take = service.take();
                    take.pollEvents();
                    take.reset();
                } catch (InterruptedException e) {
                }
                //对于事件发生本身超过3秒未出现下次事件则进行刷新
                //timer没启动
                if (!startTimer) {
                    //3秒计时
                    if (debug)
                        System.out.println("WatcherThread.run 文件更新计时器第一次启动");
                    threeSecTimer = System.currentTimeMillis();
                    //启动计时任务
                    timer = new Timer();
                    timerTask = getTimerTask();
                    timer.schedule(timerTask, 3000);
                    //设置标志为true
                    startTimer = true;
                } else {
                    //timer已经启动，但是未到3秒，留个冗余400ms
                    //此时重启计时
                    if (threeSecTimer != 0 && System.currentTimeMillis() - threeSecTimer < 2600) {
                        if (debug)
                            System.out.println("WatcherThread.run 文件更新计时器重新启动");
                        timer.cancel();
                        timerTask.cancel();
                        timer = new Timer();
                        timerTask = getTimerTask();
                        timer.schedule(timerTask, 3000);
                        threeSecTimer = System.currentTimeMillis();
                    } else {
                        //设置标志
                        //时间超过3秒，直接设置标志，此时应该扫描开始了
                        startTimer = false;
                    }
                }
            }
        }

        private void redoScan() {
            Log.log("detected worker folder change, rescan now...");
            fileCache.reload();
            if (makeAllDirectoryRegisterEvent(workerDirPath))
                throw new IllegalStateException("something folder/file cannot registered watch service");
            doScan(file);
            startTimer = false;
        }

        private WatchEvent.Kind[] kinds = {StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY};
        private boolean makeAllDirectoryRegisterEvent(String root) {
            try {
                //root
                File file = new File(root);
                if (file.isDirectory()) {
                    Paths.get(root).register(service, kinds);
                    File[] files = file.listFiles();
                    if (files == null) return false;
                    for (File f : files) {
                        if (f.isDirectory())
                            makeAllDirectoryRegisterEvent(f.getAbsolutePath());
                    }
                }
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                Log.err("registered file watch service failure.");
                Log.err("file watch service will not be run");
                return true;
            }
        }

        private void doScan(File file) {
            File[] files = file.listFiles();
            if (files == null) return;
            for (File f:files){
                if (f.isDirectory()){
                    doScan(f);
                }else {
                    String absolutePath = f.getAbsolutePath();
                    if (absolutePath.contains(workerDirPath)){
                        absolutePath = absolutePath.replace(workerDirPath,"");
                    }
                    fileCache.addNewFile(absolutePath,new SimpleFilePojo(f.getName(),f.getAbsolutePath()));
                }
            }
        }
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String versionDescription() {
        return "[dev only]";
    }
}
