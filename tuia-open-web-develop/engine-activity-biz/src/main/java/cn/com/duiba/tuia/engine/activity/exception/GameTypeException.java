package cn.com.duiba.tuia.engine.activity.exception;

/**
 * 错误的类型，用于
 */
public class GameTypeException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public GameTypeException() {
        // 同盾二期方案中用于标识游戏返回类型，应为二期中首先做的流量引导页，所有直投游戏的都会抛出次异常，然后走默认逻辑,目前不需要参数
    }


}
