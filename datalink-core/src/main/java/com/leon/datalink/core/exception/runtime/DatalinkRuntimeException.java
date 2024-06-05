

package com.leon.datalink.core.exception.runtime;

/**
 * datalink runtime exception.
 *
 * @author Leon
 */
public class DatalinkRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 3513491993982293262L;

    public static final String ERROR_MESSAGE_FORMAT = "errCode: %d, errMsg: %s ";

    private int errCode;

    public DatalinkRuntimeException(int errCode) {
        super();
        this.errCode = errCode;
    }

    public DatalinkRuntimeException(int errCode, String errMsg) {
        super(String.format(ERROR_MESSAGE_FORMAT, errCode, errMsg));
        this.errCode = errCode;
    }

    public DatalinkRuntimeException(int errCode, Throwable throwable) {
        super(throwable);
        this.errCode = errCode;
    }

    public DatalinkRuntimeException(int errCode, String errMsg, Throwable throwable) {
        super(String.format(ERROR_MESSAGE_FORMAT, errCode, errMsg), throwable);
        this.errCode = errCode;
    }
    
    public int getErrCode() {
        return errCode;
    }
    
    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }
}
