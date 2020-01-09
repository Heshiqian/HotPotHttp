package cn.heshiqian.hotpothttp.core.pojo;

public class Bundle {

    public static final String SOCKET ="socket";
    public static final String REQUEST ="request";
    public static final String RESPONSE="response";

    public static class Type{
        String spName;
        public Type(String name){
            spName=name;
        }
    }

    Object o;
    Class type;

    @Override
    public String toString() {
        return "Bundle{" +
                "o=" + o +
                ", type=" + type +
                '}';
    }

    public <T>T get(){
        Class<T> tClass = type;
        return tClass.cast(o);
    }

    public Object getO() {
        return o;
    }

    public void setO(Object o) {
        this.o = o;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public Bundle(Object o, Class type) {
        this.o = o;
        this.type = type;
    }
}
