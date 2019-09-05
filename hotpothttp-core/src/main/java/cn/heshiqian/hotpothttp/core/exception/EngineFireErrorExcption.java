package cn.heshiqian.hotpothttp.core.exception;

public class EngineFireErrorExcption extends IllegalStateException {

    public EngineFireErrorExcption(String s) {
        super(s);
    }

    public EngineFireErrorExcption(String message, Throwable cause) {
        super(message, cause);
    }
}
