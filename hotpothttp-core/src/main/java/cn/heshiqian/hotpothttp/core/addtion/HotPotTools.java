package cn.heshiqian.hotpothttp.core.addtion;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class HotPotTools {

    private static final SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public enum ExitCode{
        ERROR,NORMAL,SOMETINGBREAK;
    }

    public static String getThreadName(){
        return Thread.currentThread().getName();
    }

    public static void threadSleepOnNoExecption(long time){
        try {
            Thread.sleep(time);
        }catch (Exception e){
            // do nothing
        }
    }

    public static void printThreadStartNotify(String tname){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[")
                .append(tname)
                .append("] thread start in ")
                .append(dateFormatter.format(new Date()))
                .append(" -->  id : ")
                .append(String.valueOf(Thread.currentThread().getId()));
        System.out.println(stringBuilder.toString());
    }

    public static String readHttpHeadInfo(BufferedReader bufferedReader){
        StringBuilder stringBuilder = new StringBuilder();
        try{
            while (true){
                String temp = bufferedReader.readLine();
                if (temp==null||temp.equals(""))
                    break;
                stringBuilder.append(temp);
                stringBuilder.append("\r\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static boolean scanFileExist(String beginPath,String targetName,boolean isDeep){
        if (!isDeep){
            File file = new File(beginPath);
            File[] files = file.listFiles();
            if (files==null) return false;
            for (int i = 0; i < files.length; i++) {
                File file1 = files[i];
                if (file1.getName().equals(targetName)){
                    return true;
                }
            }
        }else {
            //deep scan
            File file = new File(beginPath);
            File[] files = file.listFiles();
            if (files==null) return false;
            for (int i = 0; i < files.length; i++) {
                File file1 = files[i];
                if (file1.isDirectory()){
                    return scanFileExist(file1.getAbsolutePath(),targetName,true);
                }else {
                    if (file1.getName().equals(targetName)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static File findFile(String beginPath,String targetName,boolean isDeep){
        if (!isDeep){
            File file = new File(beginPath);
            File[] files = file.listFiles();
            if (files==null) return null;
            for (int i = 0; i < files.length; i++) {
                File file1 = files[i];
                if (file1.getName().equals(targetName)){
                    return file1;
                }
            }
        }else {
            //deep scan
            File file = new File(beginPath);
            File[] files = file.listFiles();
            if (files==null) return null;
            for (int i = 0; i < files.length; i++) {
                File file1 = files[i];
                if (file1.isDirectory()){
                    return findFile(file1.getAbsolutePath(),targetName,true);
                }else {
                    if (file1.getName().equals(targetName)){
                        return file1;
                    }
                }
            }
        }
        return null;
    }


    public static String printStackTrace(StackTraceElement[] elements){
        StringBuilder builder=new StringBuilder();
        for (StackTraceElement e:elements){
            builder.append(e.getClassName())
                    .append(":")
                    .append(e.getMethodName())
                    .append("() in line:")
                    .append(e.getLineNumber())
                    .append("\n");
        }
        return builder.toString();
    }

    public static void exitSystem(ExitCode code){
        switch (code){
            case ERROR:
                System.exit(-1);
            case NORMAL:
                System.exit(0);
            case SOMETINGBREAK:
                System.exit(1);
        }
    }

    public static String getMapLineByLine(Map map, String tag){
        StringBuilder sb = new StringBuilder();
        Iterator iterator = map.entrySet().iterator();
        sb.append("===========Print Map Line By Line============\n");
        sb.append("map data by : ").append(tag).append("\n");
        sb.append("time:").append(System.currentTimeMillis()).append("\n");
        sb.append("+begin"+"\n");
        while (iterator.hasNext()){
            Map.Entry next = (Map.Entry) iterator.next();
            Object key = next.getKey();
            Object value = next.getValue();
            sb.append("    ").append(key.toString()).append(" -> ").append(value.toString()).append("\n");
        }
        sb.append("+end\n");
        sb.append("=============================================\n");
        return sb.toString();
    }


    public static void printMapLineByLine(Map map, String tag){
        Iterator iterator = map.entrySet().iterator();
        Log.debug("===========Print Map Line By Line============");
        Log.debug("map data by : "+tag);
        Log.debug("time:"+System.currentTimeMillis());
        Log.debug("+begin");
        while (iterator.hasNext()){
            Map.Entry next = (Map.Entry) iterator.next();
            Object key = next.getKey();
            Object value = next.getValue();
            System.out.println("    "+key.toString()+" -> "+value.toString());
        }
        Log.debug("+end");
        Log.debug("=============================================");
    }

    public static String getListLineByLine(List list){
        Iterator iterator = list.iterator();
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()){
            Object next = iterator.next();
            sb.append(next.toString()).append("\r\n");
        }
        return sb.toString();
    }

    public static String getArrayLineByLine(Object[] objects){
        return getListLineByLine(Arrays.asList(objects));
    }

    public static class Timer{

        private static long startTime=0;

        public static void startTimer(){
            startTime=System.currentTimeMillis();
        }

        public static void resetTimer(){
            startTime=-1;
        }

        public static long stopTimerAndGetTime() {
            long l = System.currentTimeMillis() - startTime;
            startTime=0;
            return l;
        }

        public static void stopTimer(String tag){
            if (startTime==0)
                throw new IllegalStateException("not start timer, please call startTimer() first!");
            long now = System.currentTimeMillis();
            System.out.println("[Timer] "+tag+" <- this progress run time:"+(now-startTime)+"ms");
            startTime=0;
        }

    }

}
