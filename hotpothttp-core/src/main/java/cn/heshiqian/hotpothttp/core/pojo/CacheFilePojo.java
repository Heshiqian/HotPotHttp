package cn.heshiqian.hotpothttp.core.pojo;

import java.io.File;

public class CacheFilePojo {

    private File file;
    private String relativePath;
    private SimpleFilePojo simpleFilePojo;
    private byte[] bytes;

    @Override
    public int hashCode() {
        return relativePath.hashCode();
    }

    @Override
    public String toString() {
        return "CacheFilePojo{" +
                "file=" + file +
                ", relativePath='" + relativePath + '\'' +
                ", simpleFilePojo=" + simpleFilePojo +
                '}';
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public SimpleFilePojo getSimpleFilePojo() {
        return simpleFilePojo;
    }

    public void setSimpleFilePojo(SimpleFilePojo simpleFilePojo) {
        this.simpleFilePojo = simpleFilePojo;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setPayload(byte[] bytes){
        this.bytes=bytes;
    }

    public CacheFilePojo(File file, String relativePath, SimpleFilePojo simpleFilePojo) {
        this.file = file;
        this.relativePath = relativePath;
        this.simpleFilePojo = simpleFilePojo;
    }
}
