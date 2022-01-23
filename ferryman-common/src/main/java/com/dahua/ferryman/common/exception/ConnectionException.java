package com.dahua.ferryman.common.exception;

import com.dahua.ferryman.common.constants.ResponseCode;
import lombok.Getter;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午5:15
 */
public class ConnectionException extends BaseException{

    private static final long serialVersionUID = -8503239867913964958L;

    @Getter
    private final String uniqueId;

    @Getter
    private final String requestUrl;

    public ConnectionException(String uniqueId, String requestUrl) {
        this.uniqueId = uniqueId;
        this.requestUrl = requestUrl;
    }

    public ConnectionException(Throwable cause, String uniqueId, String requestUrl, ResponseCode code) {
        super(code.getMessage(), cause, code);
        this.uniqueId = uniqueId;
        this.requestUrl = requestUrl;
    }

}
