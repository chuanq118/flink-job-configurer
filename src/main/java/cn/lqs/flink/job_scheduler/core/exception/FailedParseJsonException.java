package cn.lqs.flink.job_scheduler.core.exception;

/**
 * @author @lqs
 */
public class FailedParseJsonException extends RuntimeException{

    public FailedParseJsonException(String message) {
        super(message);
    }

    public FailedParseJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedParseJsonException(Throwable cause) {
        super(cause);
    }

    protected FailedParseJsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
