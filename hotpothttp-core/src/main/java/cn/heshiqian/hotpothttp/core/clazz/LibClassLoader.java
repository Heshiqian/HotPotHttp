package cn.heshiqian.hotpothttp.core.clazz;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

public class LibClassLoader extends URLClassLoader {

    public LibClassLoader(ArrayList<URL> urls) {
        super(urls.toArray(new URL[]{}));
    }

}
