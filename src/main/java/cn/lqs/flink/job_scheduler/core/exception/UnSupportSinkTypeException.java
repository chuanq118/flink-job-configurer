package cn.lqs.flink.job_scheduler.core.exception;

/**
 * @author @lqs
 */
public class UnSupportSinkTypeException extends Exception{

    public UnSupportSinkTypeException(String message) {
        super(message);
    }

    public UnSupportSinkTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnSupportSinkTypeException(Throwable cause) {
        super(cause);
    }

    protected UnSupportSinkTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
