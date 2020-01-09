package cn.heshiqian.hotpothttp.gui;

import cn.heshiqian.hotpothttp.gui.window.MainWindow;

import java.util.Map;

public final class GUI {
    private static final String START_CLASS="SCLZ_KEY";

    private static String mainClassName;


    private MainWindow mainWindow;

    public void gui(Map<String,Object> args){
        mainClassName =  (mainClassName = (String) args.get(START_CLASS))==null
                ?"cn.heshiqian.hotpothttp.core.HotPotHttpApplicationStarter"
                :mainClassName;

        mainWindow=new MainWindow("HotPotHttp GUI v."+Version.guiVersion);



    }

}
