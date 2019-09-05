package cn.heshiqian.hotpothttp.core.proxy;

import cn.heshiqian.hotpothttp.core.addtion.VersionControl;

import java.nio.file.WatchService;

public interface FileWatcher extends VersionControl {

    WatchService getWatchService();

    void startWatch();

}
