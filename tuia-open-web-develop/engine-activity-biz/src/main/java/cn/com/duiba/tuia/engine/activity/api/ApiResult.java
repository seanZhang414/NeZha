package cn.com.duiba.tuia.engine.activity.api;

import java.io.Serializable;

/**
 * API返回结果
 */
public class ApiResult implements Serializable {

    private static final long serialVersionUID = -7736068263660999504L;

    private String            error_code;                              // NOSONAR

    private String            message;

    /** 构造函数 */
    public ApiResult() {
        super();
    }

    /**
     * 构造函数
     * 
     * @param code
     */
    public ApiResult(String code) {
        super();
        this.error_code = code;
    }

    /**
     * 构造函数
     * 
     * @param code
     * @param message
     */
    public ApiResult(String code, String message) {
        super();
        this.error_code = code;
        this.message = message;
    }

    public String getError_code() {// NOSONAR
        return error_code;
    }

    public void setError_code(String error_code) {// NOSONAR
        this.error_code = error_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
