package cn.lqs.flink.job_scheduler.core.job;

import java.util.Locale;

/**
 * @author @lqs
 */
public enum SourceType {
    KAFKA;

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}
