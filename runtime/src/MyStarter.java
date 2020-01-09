import cn.heshiqian.hotpothttp.core.HotPotHttpApplicationStarter;
import cn.heshiqian.hotpothttp.core.plugin.Creater;
import cn.heshiqian.hotpothttp.module.router.Router;
import cn.heshiqian.hotpothttp.module.router.RouterConfig;

public class MyStarter extends HotPotHttpApplicationStarter {
    public static void main(String[] args) {
        run(args,MyStarter.class);
    }

    @Creater(target = Router.class)
    public RouterConfig initRouter(){
        RouterConfig.Builder builder = new RouterConfig.Builder();
        return builder.setOn(true)
                .build();
    }
}
