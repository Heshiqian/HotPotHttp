package cn.heshiqian.hotpothttp.core.exception;

public class EngineInitException extends RuntimeException {

    public EngineInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public EngineInitException(String message) {
        super(message);
    }

    public EngineInitException(Throwable cause) {
        super(cause);
    }
}
