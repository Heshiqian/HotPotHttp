package cn.heshiqian.hotpothttp.core.exception;

public class WhatTheFxxk extends RuntimeException{
    public WhatTheFxxk(String message) {
        super(message+" WTF?");
    }

    public WhatTheFxxk(String message, Throwable cause) {
        super(message+" WTF?", cause);
    }

}
