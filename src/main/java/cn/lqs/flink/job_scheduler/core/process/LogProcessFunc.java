package cn.lqs.flink.job_scheduler.core.process;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @author @lqs
 */
@Slf4j
public class LogProcessFunc extends ProcessFunction<Object, Void> {

    @Override
    public void processElement(Object o,
                               ProcessFunction<Object, Void>.Context context,
                               Collector<Void> collector) throws Exception {
        log.info("[{}] process::{}", context.timestamp(), o);
    }

}
