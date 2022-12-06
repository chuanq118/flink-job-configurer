package cn.lqs.flink.job_scheduler.core;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author @lqs
 */
public interface SourceConfigurer {

    void configure(StreamExecutionEnvironment env);

}
