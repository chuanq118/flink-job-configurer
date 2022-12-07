package cn.lqs.flink.job_scheduler.redis;

import cn.lqs.flink.job_scheduler.core.SinkConfigurer;
import cn.lqs.flink.job_scheduler.core.job.DataStreamSinkWrapper;
import cn.lqs.flink.job_scheduler.core.job.SourceSinkCfgNames;
import com.alibaba.fastjson2.JSONObject;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.springframework.stereotype.Component;

import static cn.lqs.flink.job_scheduler.core.DataSTypes.SINK_CONFIGURER_POSTFIX;

/**
 * @author @lqs
 */
@Component("redis-string-kv" + SINK_CONFIGURER_POSTFIX)
public class RedisSinkStringKVConfigurer implements SinkConfigurer {


    @Override
    public void configure(DataStreamSinkWrapper<?> sinkWrapper, JSONObject sinkCfg) {
        sinkWrapper.getDataStream().addSink(new RedisSink<>(sinkCfg.getJSONObject(SourceSinkCfgNames.PROPERTY)
                        .toJavaObject(RedisProperty.class)
                        .toFlinkJedisCfg(), new RedisStringKVMapper<>(sinkWrapper.getDataClass())))
                .name(sinkCfg.getString(SourceSinkCfgNames.NAME));
    }

}
