package com.dahua.ferryman.common.utils;

import com.dahua.ferryman.common.constants.BasicConst;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.text.SimpleDateFormat;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 上午11:30
 */
public class JSONUtil {

    public static final String CODE = "code";

    public static final String STATUS = "status";

    public static final String DATA = "data";

    public static final String MESSAGE = "message";

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonFactory jasonFactory = mapper.getFactory();

    static {
        //	序列化时候，只序列化非空字段
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mapper.setDateFormat(new SimpleDateFormat(BasicConst.DATE_FORMAT));

        // 	当反序列化出现未定义字段时候，不出现错误
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        //  dubbo泛化调用去除class字段
        mapper.addMixIn(Object.class, ExcludeFilter.class);
        mapper.setFilterProvider(new SimpleFilterProvider()
                .addFilter("excludeFilter", SimpleBeanPropertyFilter.serializeAllExcept("class")));
    }

    public static <T> T parse(String str, JavaType javaType) {
        try {
            return mapper.readValue(str, javaType);
        } catch (Exception e) {
            throw new RuntimeException("json parse to object [" + str + "] error:" + str, e);
        }
    }

    public static <T> T parse(String str, Class<T> clz) {
        try {
            return mapper.readValue(str == null ? "{}" : str, clz);
        } catch (Exception e) {
            throw new RuntimeException("json parse to object [" + clz + "] error:" + str, e);
        }
    }

    public static String toJSONString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("object format to json error:" + obj, e);
        }
    }

    public static ObjectNode createObjectNode() {
        return mapper.createObjectNode();
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().
                constructParametricType(collectionClass, elementClasses);
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return mapper.convertValue(fromValue, toValueType);
    }

    @JsonFilter("excludeFilter")
    public static class ExcludeFilter {

    }

}
