package cn.lqs.flink.job_scheduler.core;

import cn.lqs.flink.job_scheduler.core.exception.FailedParseJsonException;
import cn.lqs.flink.job_scheduler.core.exception.UnSupportSourceTypeException;
import cn.lqs.flink.job_scheduler.core.job.DataStream;
import cn.lqs.flink.job_scheduler.core.job.FlinkJob;
import cn.lqs.flink.job_scheduler.core.job.SourceType;
import cn.lqs.flink.job_scheduler.infrastruction.util.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * @author @lqs
 */
@Slf4j
public final class FlinkRuntimeContext {

    private StreamExecutionEnvironment env;

    private final List<FlinkJob> jobs;

    private FlinkRuntimeContext(List<FlinkJob> jobs){
        this.jobs = jobs;
    }

    public List<FlinkJob> getJobs() {
        return jobs;
    }

    /**
     *
     * @param json json string
     * @return {@link FlinkRuntimeContext}
     */
    public static FlinkRuntimeContext create(String json) {
        json = json.trim();
        Object config = JSON.parse(json);
        if (config instanceof JSONArray) {
            // 多个任务
            List<FlinkJob> flinkJobs = ((JSONArray) config).toJavaList(FlinkJob.class);
            configureJobsName(flinkJobs);
            log.info("读取到[{}]个任务.", flinkJobs.size());
            return new FlinkRuntimeContext(flinkJobs);
        }
        if (config instanceof JSONObject) {
            // 单个任务, 注意此处使用 Unmodifiable List 包裹
            List<FlinkJob> jobs = List.of(((JSONObject) config).to(FlinkJob.class));
            configureJobsName(jobs);
            log.info("读取到单个 flink 任务. [{}]", jobs.get(0).getName());
            return new FlinkRuntimeContext(jobs);
        }
        throw new FailedParseJsonException("无法正确解析 JSON 配置文件.");
    }

    private static void configureJobsName(List<FlinkJob> jobs) {
        for (FlinkJob job : jobs) {
            if (!StringUtils.hasText(job.getName())) {
                job.setName(DateUtil.nowDatetimeNumberString() + "_" + UUID.randomUUID().toString().split("-")[0]);
            }
        }
    }

    public StreamExecutionEnvironment getExecutionEnvironment() {
        return this.env;
    }

    public FlinkRuntimeContext setExecutionEnvironment(StreamExecutionEnvironment env) {
        this.env = env;
        return this;
    }

    private void configureEnv(FlinkJob job) {
        // todo 解析 setting 并设置 env
        // this.env.setParallelism();
        log.info("任务配置信息 :: ?? 待");
    }


    public void run() {
        for (FlinkJob job : jobs) {
            log.info("##### 执行启动任务[{}] #####", job.getName());
            configureEnv(job);
            for (DataStream dataStream : job.getDataStreams()) {
                try {
                    addSource(dataStream.getSource());
                } catch (UnSupportSourceTypeException e) {
                    log.error("配置 flink 任务发生错误", e);
                    // 当前的 Source -> Sink 将不会执行
                }
            }
        }
    }

    private void addSource(JSONObject source) throws UnSupportSourceTypeException {
        String type = source.getString("type").toLowerCase(Locale.ROOT);
        if (match(SourceType.KAFKA, type)) {
            log.info("配置 KAFKA-SOURCE");
            List<String> topics = source.getJSONArray("topic").toJavaList(String.class);
            source.getJSONObject("props");
            this.env.addSource(new FlinkKafkaConsumer<>(topics, new SimpleStringSchema(), null));
        }
        throw new UnSupportSourceTypeException(type);
    }



    private boolean match(SourceType sourceType, String type) {
        return sourceType.toString().equals(type);
    }
}
