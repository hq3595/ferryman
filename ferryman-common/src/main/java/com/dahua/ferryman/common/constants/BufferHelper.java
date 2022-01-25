package com.dahua.ferryman.common.constants;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/22 下午9:56
 * 网关缓冲区辅助类
 */
public interface BufferHelper {

    String FLUSHER = "FLUSHER";

    String MPMC = "MPMC";

    String NONE = "NONE";

    static boolean isMpmc(String bufferType) {
        return MPMC.equals(bufferType);
    }

    static boolean isFlusher(String bufferType) {
        return FLUSHER.equals(bufferType);
    }

}
