package cn.com.duiba.tuia.exception;

/**
 * @author xuyenan
 * @createTime 2016/12/19
 */
public class TuiaRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 3273124902336437696L;

    /**
     * Constructor
     *
     * @param cause
     */
    public TuiaRuntimeException(Throwable cause) {
        super(cause);
    }
}
