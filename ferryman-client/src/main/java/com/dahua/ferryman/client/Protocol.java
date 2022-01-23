package com.dahua.ferryman.client;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午8:13
 */
public enum Protocol {

    HTTP("http", "http协议");

    private String code;

    private String desc;

    Protocol(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
