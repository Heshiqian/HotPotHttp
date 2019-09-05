package cn.heshiqian.hotpothttp.core.config;

import java.lang.reflect.Field;

public class ConfigInjector implements IConfigureInject {

    private Object attachClass;

    public ConfigInjector(Object o) {
        attachClass=o;
    }

    @Override
    public void injectConfig(String rootTag, String val) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = attachClass.getClass().getDeclaredField(rootTag);
        //not necessary, but 'private' field mast set accessible equals true
        declaredField.setAccessible(true);
        declaredField.set(attachClass,val);
    }

}
