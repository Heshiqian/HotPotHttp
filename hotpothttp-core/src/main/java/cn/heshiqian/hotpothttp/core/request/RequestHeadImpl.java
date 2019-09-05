package cn.heshiqian.hotpothttp.core.request;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHeadImpl implements RequestHead{

    public static final int HEAD_DEFAULT_SIZE = 15;

    private ConcurrentHashMap<String,String> map;

    public RequestHeadImpl() {
        map=new ConcurrentHashMap<>(HEAD_DEFAULT_SIZE);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map == null || map.size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public String get(Object key) {
        return map.get(key);
    }

    @Override
    public String put(String key, String value) {
        return map.put(key,value);
    }

    @Override
    public String remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<String> values() {
        return map.values();
    }

    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        return map.entrySet();
    }

    @Override
    public void printHead() {
        System.out.println(this.toString());
    }

    @Override
    public String getHeader(String key) {
        return map.get(key);
    }

    @Override
    public Map<String,String> getHeaders() {
        return map;
    }

    @Override
    public String toString() {
        return "Head ==> " + map;
    }
}
