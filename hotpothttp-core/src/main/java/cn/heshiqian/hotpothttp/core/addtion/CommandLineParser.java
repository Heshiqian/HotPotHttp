package cn.heshiqian.hotpothttp.core.addtion;

import cn.heshiqian.hotpothttp.core.config.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * command line parser.
 */
public class CommandLineParser {


    /**
     * do parse
     * example: java -jar xxx.jar --? param
     * ? -> some HotPotHttp supported command.
     * param -> some parameter string.
     * @param args command line args
     */
    public Map<String,String> parse(String[] args) {
        Map<String,String> map = new HashMap<>();
        int argIndex = 0;
        if (args.length==0)
            return map;
        for (;;){
            if (isOutOfArrayRange(args,argIndex)) break;
            String someTag = args[argIndex];
            switch (someTag){

                case Configuration.COMMAND_LINE_GUI_MODE:{
                    map.put(Configuration.COMMAND_LINE_GUI_MODE,"GUI");
                    return map;
                }

                case Configuration.COMMAND_LINE_HTTP_PORT:{
                    if (!isOutOfArrayRange(args,argIndex+1)){
                        String portStr = args[argIndex + 1];
                        checkPortLegal(portStr);
                        map.put(Configuration.COMMAND_LINE_HTTP_PORT,portStr);
                    }else
                        throw new IllegalArgumentException("the parameter is missing, behind this command: ["+Configuration.COMMAND_LINE_HTTP_PORT+"]");
                    break;
                }

                case Configuration.COMMAND_LINE_HTTP_IP:{
                    if (!isOutOfArrayRange(args,argIndex+1)){
                        String ipStr = args[argIndex + 1];
                        if (!ipCheckMethod(ipStr))
                            throw new IllegalArgumentException("ip:["+ipStr+"] is not illegal, please check your input ip! should be like this:[127.0.0.1]");
                        else
                            map.put(Configuration.COMMAND_LINE_HTTP_IP,ipStr);
                    }else
                        throw new IllegalArgumentException("the parameter is missing, behind this command: ["+Configuration.COMMAND_LINE_HTTP_IP+"]");
                    break;
                }

                case Configuration.COMMAND_LINE_WORKER_PATH:{
                    if (!isOutOfArrayRange(args,argIndex+1)){
                        String workerPathStr = args[argIndex + 1];
                        File file = new File(workerPathStr);
                        if (file.exists()){
                            map.put(Configuration.COMMAND_LINE_WORKER_PATH,file.getAbsolutePath());
                        }else{
                            throw new IllegalArgumentException("'"+workerPathStr+"' is not a file or folder or does not exist, and probably access denied");
                        }
                    }else
                        throw new IllegalArgumentException("the parameter is missing, behind this command: ["+Configuration.COMMAND_LINE_WORKER_PATH+"]");
                }

                default:
                    throw new IllegalStateException("Unexpected value: " + someTag);
            }
            argIndex+=2;
        }
        return map;
    }

    private boolean isOutOfArrayRange(String[] arr,int exp){
        //if less than exp or equals exp return false, conversely return true.
        return exp > arr.length - 1;
    }

    private void checkPortLegal(String param) {
        int i1 = Integer.parseInt(param);
        if (i1<1||i1>65535)
            throw new IllegalArgumentException("port out of range! range:1-65535");
    }

    private boolean ipCheckMethod(String param) {
        String[] split = param.split("\\.");
        if (split.length!=4)
            return false;
        for(int i=0;i<split.length;i++){
            try{
                int i1 = Integer.parseInt(split[i]);
                if (i==0&&(i1<1||i1>255))
                    return false;
                else if (i1<0||i1>255)
                    return false;
            }catch (NumberFormatException e){
                return false;
            }
        }
        return true;
    }

}
