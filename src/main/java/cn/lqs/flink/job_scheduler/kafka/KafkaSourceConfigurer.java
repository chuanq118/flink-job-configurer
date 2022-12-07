package cn.lqs.flink.job_scheduler.kafka;

import cn.lqs.flink.job_scheduler.core.SourceConfigurer;
import cn.lqs.flink.job_scheduler.core.job.DataStreamSourceWrapper;
import com.alibaba.fastjson2.JSONObject;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static cn.lqs.flink.job_scheduler.core.DataSTypes.KAFKA;
import static cn.lqs.flink.job_scheduler.core.DataSTypes.SOURCE_CONFIGURER_POSTFIX;

/**
 * 此类包含了配置 kafka 作为 source 的核心逻辑.<br>
 * 假定接受的数据类型为 String 因此采用了固定的序列化方式.
 * @author @lqs
 */
@Component(KAFKA + SOURCE_CONFIGURER_POSTFIX)
public class KafkaSourceConfigurer implements SourceConfigurer {

    @Override
    public DataStreamSourceWrapper<String> configure(StreamExecutionEnvironment env, JSONObject cfg) {
        List<String> topics = cfg.getJSONArray("topic").toJavaList(String.class);
        Properties properties = new Properties();
        properties.putAll(cfg.getJSONObject("props").toJavaObject(Map.class));
        return new DataStreamSourceWrapper<String>(
                env.addSource(new FlinkKafkaConsumer<>(topics, new SimpleStringSchema(), properties)),
                String.class);
    }


}
