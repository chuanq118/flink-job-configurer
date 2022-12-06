package cn.lqs.flink.job_scheduler.core.exception;

/**
 * @author @lqs
 */
public class UnSupportSourceTypeException extends Exception{

    public UnSupportSourceTypeException(String message) {
        super(message);
    }

    public UnSupportSourceTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnSupportSourceTypeException(Throwable cause) {
        super(cause);
    }

    protected UnSupportSourceTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
