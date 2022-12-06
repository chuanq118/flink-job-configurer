package cn.lqs.flink.job_scheduler.core;

import cn.lqs.flink.job_scheduler.core.exception.FailedParseJsonException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author @lqs
 */
public final class FlinkRuntimeContext {

    private StreamExecutionEnvironment env;

    private FlinkRuntimeContext(){}

    /**
     *
     * @param json json string
     * @return {@link FlinkRuntimeContext}
     */
    public static FlinkRuntimeContext create(String json) {
        json = json.trim();
        Object config = JSON.parse(json);
        if (config instanceof JSONArray) {
            // 处理多个任务
            return new FlinkRuntimeContext();
        }
        if (config instanceof JSONObject) {
            // 处理单个任务
            return new FlinkRuntimeContext();
        }
        throw new FailedParseJsonException("无法正确解析 JSON 配置文件.");
    }

    public StreamExecutionEnvironment getExecutionEnvironment() {
        return this.env;
    }

    public FlinkRuntimeContext setExecutionEnvironment(StreamExecutionEnvironment env) {
        this.env = env;
        return this;
    }

    public void run() {
        // new FlinkKafkaConsumer<>("", new SimpleStringSchema(StandardCharsets.UTF_8), new Properties());
        // this.env.addSource()
        //         .addSink();
        // this.env.addSource()
        //         .process()
        //         .addSink();
        // this.env.execute()
    }
}
