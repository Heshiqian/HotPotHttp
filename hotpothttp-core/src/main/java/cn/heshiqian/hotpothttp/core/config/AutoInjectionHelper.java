package cn.heshiqian.hotpothttp.core.config;

public class AutoInjectionHelper {

    private Object attachClass;

    public AutoInjectionHelper(Object o) {
        attachClass=o;
    }

    public void multiKey(String keys,String val) throws NoSuchFieldException, IllegalAccessException {
//        String[] split = keys.split("\\.");
//        String rootKeyName = split[0];
//        //find root
//        Field declaredField = attachClass.getClass().getDeclaredField(rootKeyName);
//        //have to need
//        declaredField.setAccessible(true);
//        Object rootObj = declaredField.get(attachClass);
//        for (int i = 1; i < split.length; i++) {
//            String subKey = split[i];
//            Field subField = rootObj.getClass().getDeclaredField(subKey);
//            subField.setAccessible(true);
//            rootObj=subField.get(rootObj);
//        }
//        //here should be last key object
//        if (rootObj==null)
//            throw new NoSuchFieldException("not found keys : "+keys+", if this is important key, it's will replace by available default value");
//        Field lastField = rootObj.getClass().getDeclaredField(split[split.length - 1]);
//        Class<?> type = lastField.getType();
//        lastField.setAccessible(true);
//        lastField.set(rootObj,type.cast(val));
    }

    public void singleKey(String key,String val){

    }

}
