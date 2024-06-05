

package com.leon.datalink.core.exception.runtime;

import java.lang.reflect.Type;

/**
 * datalink deserialization exception.
 *
 * @author Leon
 */
public class DatalinkDeserializationException extends DatalinkRuntimeException {

    public static final int ERROR_CODE = 101;

    private static final long serialVersionUID = -2742350751684273728L;

    private static final String DEFAULT_MSG = "datalink deserialize failed. ";

    private static final String MSG_FOR_SPECIFIED_CLASS = "datalink deserialize for class [%s] failed. ";

    private Class<?> targetClass;

    public DatalinkDeserializationException() {
        super(ERROR_CODE);
    }

    public DatalinkDeserializationException(Class<?> targetClass) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()));
        this.targetClass = targetClass;
    }

    public DatalinkDeserializationException(Type targetType) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetType.toString()));
    }

    public DatalinkDeserializationException(Throwable throwable) {
        super(ERROR_CODE, DEFAULT_MSG, throwable);
    }

    public DatalinkDeserializationException(Class<?> targetClass, Throwable throwable) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()), throwable);
        this.targetClass = targetClass;
    }

    public DatalinkDeserializationException(Type targetType, Throwable throwable) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetType.toString()), throwable);
    }
    
    public Class<?> getTargetClass() {
        return targetClass;
    }
}
