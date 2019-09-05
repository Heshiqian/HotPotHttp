package cn.heshiqian.hotpothttp.core.proxy;

import cn.heshiqian.hotpothttp.core.addtion.VersionControl;

import java.io.File;

public interface WatchEventListener extends VersionControl {

    void fileCreate(File file, String name);

    void fileDelete(File file, String name);

    void fileModify(File file, String name);

    void fileMoved(File file, String name);

}
