package cn.heshiqian.hotpothttp.core.exception;

public class HotPotHttpException extends RuntimeException {

    public HotPotHttpException(String message) {
        super(message);
    }

    public HotPotHttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public HotPotHttpException(Throwable cause) {
        super(cause);
    }
}
