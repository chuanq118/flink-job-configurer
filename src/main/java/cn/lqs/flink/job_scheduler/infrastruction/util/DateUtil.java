package cn.lqs.flink.job_scheduler.infrastruction.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author @lqs
 */
public class DateUtil {

    @SuppressWarnings("SpellCheckingInspection")
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String nowDatetimeNumberString() {
        return LocalDateTime.now().format(DTF);
    }
}
