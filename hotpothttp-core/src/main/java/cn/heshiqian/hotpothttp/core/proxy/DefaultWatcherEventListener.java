package cn.heshiqian.hotpothttp.core.proxy;

import cn.heshiqian.hotpothttp.core.FileCache;

import java.io.File;

public class DefaultWatcherEventListener implements WatchEventListener {

    private FileCache fileCache = FileCache.getInstance();

    @Override
    public void fileCreate(File file, String name) {

    }

    @Override
    public void fileDelete(File file, String name) {

    }

    @Override
    public void fileModify(File file, String name) {

    }

    @Override
    public void fileMoved(File file, String name) {

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
