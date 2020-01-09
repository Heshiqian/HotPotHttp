package cn.heshiqian.hotpothttp.module.router;

public interface RouterConfig {

    boolean isOn();

    final class Builder {

        private boolean isOn;

        public Builder setOn(boolean on) {
            isOn = on;
            return this;
        }

        public RouterConfig build(){
            return new RouterConfig() {
                @Override
                public boolean isOn() {
                    return isOn;
                }
            };
        }

    }

}
