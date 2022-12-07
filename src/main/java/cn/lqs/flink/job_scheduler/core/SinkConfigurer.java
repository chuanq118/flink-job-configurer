package cn.lqs.flink.job_scheduler.core;

import cn.lqs.flink.job_scheduler.core.job.DataStreamSinkWrapper;
import com.alibaba.fastjson2.JSONObject;

/**
 * @author @lqs
 */
public interface SinkConfigurer {

    void configure(DataStreamSinkWrapper<?> sinkWrapper, JSONObject sinkCfg);

}
