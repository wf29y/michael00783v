

package com.leon.datalink.core.utils;

import cn.hutool.core.util.ClassUtil;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.leon.datalink.core.exception.runtime.DatalinkDeserializationException;
import com.leon.datalink.core.exception.runtime.DatalinkSerializationException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Json utils implement by Jackson.
 *
 * @author Leon
 */
public final class JacksonUtils {

    static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setSerializationInclusion(Include.NON_NULL);
    }

    /**
     * Object to json string.
     *
     * @param obj obj
     * @return json string
     * @throws DatalinkSerializationException if transfer failed
     */
    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new DatalinkSerializationException(obj.getClass(), e);
        }
    }

    /**
     * Object to json string byte array.
     *
     * @param obj obj
     * @return json string byte array
     * @throws DatalinkSerializationException if transfer failed
     */
    public static byte[] toJsonBytes(Object obj) {
        try {
            return ByteUtils.toBytes(mapper.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            throw new DatalinkSerializationException(obj.getClass(), e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param cls  class of object
     * @param <T>  General type
     * @return object
     * @throws DatalinkDeserializationException if deserialize failed
     */
    public static <T> T toObj(byte[] json, Class<T> cls) {
        try {
            return toObj(StringUtils.newStringForUtf8(json), cls);
        } catch (Exception e) {
            throw new DatalinkDeserializationException(cls, e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param cls  {@link Type} of object
     * @param <T>  General type
     * @return object
     * @throws DatalinkDeserializationException if deserialize failed
     */
    public static <T> T toObj(byte[] json, Type cls) {
        try {
            return toObj(StringUtils.newStringForUtf8(json), cls);
        } catch (Exception e) {
            throw new DatalinkDeserializationException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param inputStream json string input stream
     * @param cls         class of object
     * @param <T>         General type
     * @return object
     * @throws DatalinkDeserializationException if deserialize failed
     */
    public static <T> T toObj(InputStream inputStream, Class<T> cls) {
        try {
            return mapper.readValue(inputStream, cls);
        } catch (IOException e) {
            throw new DatalinkDeserializationException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json          json string byte array
     * @param typeReference {@link TypeReference} of object
     * @param <T>           General type
     * @return object
     * @throws DatalinkDeserializationException if deserialize failed
     */
    public static <T> T toObj(byte[] json, TypeReference<T> typeReference) {
        try {
            return toObj(StringUtils.newStringForUtf8(json), typeReference);
        } catch (Exception e) {
            throw new DatalinkDeserializationException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param cls  class of object
     * @param <T>  General type
     * @return object
     * @throws DatalinkDeserializationException if deserialize failed
     */
    public static <T> T toObj(String json, Class<T> cls) {
        try {
            return mapper.readValue(json, cls);
        } catch (IOException e) {
            throw new DatalinkDeserializationException(cls, e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param type {@link Type} of object
     * @param <T>  General type
     * @return object
     * @throws DatalinkDeserializationException if deserialize failed
     */
    public static <T> T toObj(String json, Type type) {
        try {
            return mapper.readValue(json, mapper.constructType(type));
        } catch (IOException e) {
            throw new DatalinkDeserializationException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json          json string
     * @param typeReference {@link TypeReference} of object
     * @param <T>           General type
     * @return object
     * @throws DatalinkDeserializationException if deserialize failed
     */
    public static <T> T toObj(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw new DatalinkDeserializationException(typeReference.getClass(), e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param inputStream json string input stream
     * @param type        {@link Type} of object
     * @param <T>         General type
     * @return object
     * @throws DatalinkDeserializationException if deserialize failed
     */
    public static <T> T toObj(InputStream inputStream, Type type) {
        try {
            return mapper.readValue(inputStream, mapper.constructType(type));
        } catch (IOException e) {
            throw new DatalinkDeserializationException(type, e);
        }
    }

    /**
     * Json string deserialize to Jackson {@link JsonNode}.
     *
     * @param json json string
     * @return {@link JsonNode}
     * @throws DatalinkDeserializationException if deserialize failed
     */
    public static JsonNode toObj(String json) {
        try {
            return mapper.readTree(json);
        } catch (IOException e) {
            throw new DatalinkDeserializationException(e);
        }
    }

    public static <T> T toMapObj(byte[] json, Class<?> keyClass, Class<?> valueClass) {
        MapType mapType = TypeFactory.defaultInstance().constructMapType(Map.class, keyClass, valueClass);
        return toObj(json, mapType);
    }

    public static <T> T toListObj(Object obj, Class<?> typeClass) {
        CollectionType collectionType = TypeFactory.defaultInstance().constructCollectionType(List.class, typeClass);
        return mapper.convertValue(obj, collectionType);
    }


    /**
     * Register sub type for child class.
     *
     * @param clz  child class
     * @param type type name of child class
     */
    public static void registerSubtype(Class<?> clz, String type) {
        mapper.registerSubtypes(new NamedType(clz, type));
    }

    /**
     * Create a new empty Jackson {@link ObjectNode}.
     *
     * @return {@link ObjectNode}
     */
    public static ObjectNode createEmptyJsonNode() {
        return new ObjectNode(mapper.getNodeFactory());
    }

    /**
     * Create a new empty Jackson {@link ArrayNode}.
     *
     * @return {@link ArrayNode}
     */
    public static ArrayNode createEmptyArrayNode() {
        return new ArrayNode(mapper.getNodeFactory());
    }

    /**
     * Parse object to Jackson {@link JsonNode}.
     *
     * @param obj object
     * @return {@link JsonNode}
     */
    public static JsonNode transferToJsonNode(Object obj) {
        return mapper.valueToTree(obj);
    }

    /**
     * construct java type -> Jackson Java Type.
     *
     * @param type java type
     * @return JavaType {@link JavaType}
     */
    public static JavaType constructJavaType(Type type) {
        return mapper.constructType(type);
    }
}
