package cn.com.duiba.tuia.engine.activity.api;

/**
 * API接口错误码
 */
public class ApiCode {

    public static final String SUCCESS                 = "0";     // 成功

    // 没有数据
    public static final String EMPTY_RESULT            = "10001"; // NOSONAR

    // 连续曝光达到上限
    public static final String SLOT_EXPOSE_REACH_LIMIT = "10002"; // NOSONAR

    // 应用或广告位不存在
    public static final String APPSLOT_NOT_EXIST       = "10003"; // NOSONAR

    // 参数错误
    public static final String PARAM_ERROR             = "20001"; // NOSONAR

    // 令牌错误
    public static final String TOKEN_ERROR             = "20002"; // NOSONAR

    // 令牌过期
    public static final String TOKEN_OVERDUE           = "20003"; // NOSONAR

    // 系统错误
    public static final String SYSTEM_ERROR            = "99999"; // NOSONAR

    private ApiCode() {

    }

}
