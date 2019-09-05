package cn.heshiqian.hotpothttp.core.plugin.abs;

import cn.heshiqian.hotpothttp.core.plugin.Plugin;
import cn.heshiqian.hotpothttp.core.request.Request;

public abstract class RequestPlugin implements Plugin {

    public abstract boolean process(Request request);

}
