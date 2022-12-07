package cn.lqs.flink.job_scheduler.core;

import cn.lqs.flink.job_scheduler.core.job.DataStreamSourceWrapper;
import com.alibaba.fastjson2.JSONObject;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author @lqs
 */
public interface SourceConfigurer {

    DataStreamSourceWrapper<?> configure(StreamExecutionEnvironment env, JSONObject cfg);

}
