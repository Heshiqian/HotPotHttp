package cn.heshiqian.hotpothttp.module.cookie;

import cn.heshiqian.hotpothttp.core.factory.ResponseBackFactory;
import cn.heshiqian.hotpothttp.core.pojo.Package;
import cn.heshiqian.hotpothttp.core.response.HttpSend;
import cn.heshiqian.hotpothttp.core.response.Response;

public class CookieResponseBackFactory extends ResponseBackFactory {

    @Override
    public HttpSend beforeSend(Response response) {
        HttpSend httpSend = super.beforeSend(response);
        Package<CookieManager> cookieManagerPackage = response.getPackage(CookieRequestFrontFactory.cookiePT);
        if (cookieManagerPackage==null)
            return httpSend;
        CookieManager cookieManager = cookieManagerPackage.getOwnObj();
        Cookie hphSession = cookieManager.getCookie("HPHSession");
        if (hphSession==null)
            return httpSend;
        String header = httpSend.getHeader();
        //移除最后的\r\n
        header = header.substring(0,header.length()-2);
        header += "Set-Cookie:"+hphSession.getKey()+"="+hphSession.getValue()+"; HttpOnly\r\n";
        httpSend.setHeader(header);
        return httpSend;
    }

}
