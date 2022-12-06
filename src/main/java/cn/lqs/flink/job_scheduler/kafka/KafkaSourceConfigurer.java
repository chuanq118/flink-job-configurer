package cn.lqs.flink.job_scheduler.kafka;

import cn.lqs.flink.job_scheduler.core.SourceConfigurer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.stereotype.Component;

import static cn.lqs.flink.job_scheduler.core.DataSTypes.KAFKA;
import static cn.lqs.flink.job_scheduler.core.DataSTypes.SOURCE_CONFIGURER_POSTFIX;

/**
 * @author @lqs
 */
@Component(KAFKA + SOURCE_CONFIGURER_POSTFIX)
public class KafkaSourceConfigurer implements SourceConfigurer {
    @Override
    public void configure(StreamExecutionEnvironment env) {
        // 装配 kafka source 的核心逻辑

    }
}
