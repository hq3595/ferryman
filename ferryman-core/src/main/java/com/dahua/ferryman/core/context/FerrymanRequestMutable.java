package com.dahua.ferryman.core.context;

import org.asynchttpclient.Request;
import org.asynchttpclient.cookie.Cookie;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 上午11:34
 */
public interface FerrymanRequestMutable {

    void setModifyHost(String host);

    /**
     * 获取修改的host
     */
    String getModifyHost();

    /**
     * 设置请求路径
     */
    void setModifyPath(String path);

    /**
     * 获取修改的地址
     */
    String getModifyPath();

    /**
     * 添加请求头信息
     */
    void addHeader(CharSequence name, String value);

    /**
     * 设置请求头信息
     */
    void setHeader(CharSequence name, String value);

    /**
     * 添加请求的查询参数
     */
    void addQueryParam(String name, String value);


    /**
     * 添加或替换cookie
     */
    void addOrReplaceCookie(Cookie cookie);

    /**
     * 添加form表单参数
     */
    void addFormParam(String name, String value);

    /**
     * 设置请求超时时间
     */
    void setRequestTimeout(int requestTimeout);

    /**
     * 构建转发请求的请求对象
     */
    Request build();

    /**
     * 获取最终的路由路径
     */
    String getFinalUrl();

}
