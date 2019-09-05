package cn.heshiqian.hotpothttp.core.pojo;

public class Package<T> {

    public static class PackageType {
        String typeName;
        String typeDesc;
        public PackageType(String typeName, String typeDesc) {
            this.typeName = typeName;
            this.typeDesc = typeDesc;
        }
    }

    private String packageName;
    private T ownObj;

    public String getPackageName() {
        return packageName;
    }

    public T getOwnObj() {
        return ownObj;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setOwnObj(T ownObj) {
        this.ownObj = ownObj;
    }
}
