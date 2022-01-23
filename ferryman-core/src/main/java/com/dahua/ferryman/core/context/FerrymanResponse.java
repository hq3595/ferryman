package com.dahua.ferryman.core.context;

import com.dahua.ferryman.common.constants.ResponseCode;
import com.dahua.ferryman.common.utils.JSONUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.handler.codec.http.*;
import lombok.Data;
import org.asynchttpclient.Response;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 上午11:31
 */
@Data
public class FerrymanResponse {

    //	响应头
    private HttpHeaders responseHeaders = new DefaultHttpHeaders();

    //	额外的响应结果
    private final HttpHeaders extraResponseHeaders = new DefaultHttpHeaders();

    //	返回的响应内容
    private String content;

    //	返回响应状态码
    private HttpResponseStatus httpResponseStatus;

    //	响应对象
    private Response futureResponse;

    private FerrymanResponse() {
    }

    /**
     * 设置响应头信息
     */
    public void putHeader(CharSequence key, CharSequence val) {
        responseHeaders.add(key, val);
    }

    /**
     * 构建网关响应对象
     */
    public static FerrymanResponse buildResponse(Response futureResponse) {
        FerrymanResponse ferrymanResponse = new FerrymanResponse();
        ferrymanResponse.setFutureResponse(futureResponse);
        ferrymanResponse.setHttpResponseStatus(HttpResponseStatus.valueOf(futureResponse.getStatusCode()));
        return ferrymanResponse;
    }

    /**
     * 返回一个json类型的响应信息，失败时候使用
     */
    public static FerrymanResponse buildResponse(ResponseCode code, Object... args) {
        ObjectNode objectNode = JSONUtil.createObjectNode();
        objectNode.put(JSONUtil.STATUS, code.getStatus().code());
        objectNode.put(JSONUtil.CODE, code.getCode());
        objectNode.put(JSONUtil.MESSAGE, code.getMessage());
        FerrymanResponse ferrymanResponse = new FerrymanResponse();
        ferrymanResponse.setHttpResponseStatus(code.getStatus());
        ferrymanResponse.putHeader(HttpHeaderNames.CONTENT_TYPE,
                HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        ferrymanResponse.setContent(JSONUtil.toJSONString(objectNode));
        return ferrymanResponse;
    }

    /**
     * 返回一个json类型的响应信息, 成功时候使用
     */
    public static FerrymanResponse buildResponseObj(Object data) {
        ObjectNode objectNode = JSONUtil.createObjectNode();
        objectNode.put(JSONUtil.STATUS, ResponseCode.SUCCESS.getStatus().code());
        objectNode.put(JSONUtil.CODE, ResponseCode.SUCCESS.getCode());
        objectNode.putPOJO(JSONUtil.DATA, data);
        FerrymanResponse ferrymanResponse = new FerrymanResponse();
        ferrymanResponse.setHttpResponseStatus(ResponseCode.SUCCESS.getStatus());
        ferrymanResponse.putHeader(HttpHeaderNames.CONTENT_TYPE,
                HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        ferrymanResponse.setContent(JSONUtil.toJSONString(objectNode));
        return ferrymanResponse;
    }

}
