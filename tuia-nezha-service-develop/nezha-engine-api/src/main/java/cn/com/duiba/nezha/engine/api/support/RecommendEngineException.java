package cn.com.duiba.nezha.engine.api.support;

/**
 * Created by lwj on 16/8/5. <br/>
 * 推荐系统一场
 */
public class RecommendEngineException extends RuntimeException {

    /**
     * 错误处理接口
     *
     * @param msg  消息
     */
    public RecommendEngineException(String msg) {
        super(msg);
    }

    /**
     * 错误处理接口
     *
     * @param msg  消息
     * @param e  错误内容
     */
    public RecommendEngineException(String msg, Exception e) {
        super(msg, e);
    }

}
