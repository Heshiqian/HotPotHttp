package cn.heshiqian.hotpothttp.module.cookie;

import cn.heshiqian.hotpothttp.core.addtion.Log;
import cn.heshiqian.hotpothttp.core.factory.RequestFrontFactory;
import cn.heshiqian.hotpothttp.core.pojo.Package;
import cn.heshiqian.hotpothttp.core.request.Http;
import cn.heshiqian.hotpothttp.core.request.Request;

public class CookieRequestFrontFactory extends RequestFrontFactory {

    private CookieCenter cookieCenter = CookieCenter.getInstance();
    public static final Package.PackageType cookiePT=new Package.PackageType("hph-cookie","cookie");

    @Override
    public Request receive(Http http) {
        Request receive = super.receive(http);

        String cookie = receive.getHeader().getHeader("Cookie");

        if (cookie==null){
            createCookie(receive);
            return receive;
        }
        //对于非空cookie
        //先获取
        CookieManager cookieManager = new CookieManager(cookie);
        cookieManager.printCookies();
        Cookie hphSession = cookieManager.getCookie("HPHSession");
        if (hphSession == null){
            //session不存在
            createCookie(receive);
        }else {
            CookieManager cm = cookieCenter.read(hphSession.getValue());
            if (cm == null){
                //无效cookie
                createCookie(receive);
                return receive;
            }
            //存在cookie并且唯一，不做处理
        }
        return receive;
    }

    private void createCookie(Request receive) {
        //空cookie请求，直接创建一个cookie
        String id = cookieCenter.getID();
        Package<CookieManager> cookieManagerPackage = new Package<>();
        CookieManager cookieManager = new CookieManager();
        Cookie session = new Cookie("HPHSession", id);
        cookieManager.addCookie(session);
        cookieManagerPackage.setOwnObj(cookieManager);
        cookieCenter.write(id,cookieManager);
        receive.addPackage(cookiePT,cookieManagerPackage);
    }
}
