package cn.heshiqian.hotpothttp.core.plugin.abs;

import cn.heshiqian.hotpothttp.core.response.Response;

public abstract class ResponsePlugin {

    public abstract boolean process(Response response);

}
