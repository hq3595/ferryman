package com.dahua.ferryman.common.exception;

import com.dahua.ferryman.common.constants.ResponseCode;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午5:13
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = -5658789202563433456L;

    public BaseException() {
    }

    protected ResponseCode code;

    public BaseException(ResponseCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public BaseException(String message, ResponseCode code) {
        super(message);
        this.code = code;
    }

    public BaseException(String message, Throwable cause, ResponseCode code) {
        super(message, cause);
        this.code = code;
    }

    public BaseException(ResponseCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public BaseException(String message, Throwable cause,
                         boolean enableSuppression, boolean writableStackTrace, ResponseCode code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public ResponseCode getCode() {
        return code;
    }

}
