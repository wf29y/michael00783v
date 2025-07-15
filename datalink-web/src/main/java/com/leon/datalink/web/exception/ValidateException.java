package com.leon.datalink.web.exception;

import com.leon.datalink.core.exception.runtime.DatalinkRuntimeException;

public class ValidateException extends DatalinkRuntimeException {

    public static final int ERROR_CODE = 103;

    private static final long serialVersionUID = -2742350751684666728L;

    private static final String DEFAULT_MSG = "参数校验失败";

    public ValidateException() {
        super(ERROR_CODE, DEFAULT_MSG);
    }

    public ValidateException(String errMsg) {
        super(ERROR_CODE, errMsg);
    }

}
