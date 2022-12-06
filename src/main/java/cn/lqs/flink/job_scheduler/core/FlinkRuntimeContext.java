package cn.lqs.flink.job_scheduler.core;

import cn.lqs.flink.job_scheduler.core.exception.FailedParseJsonException;
import cn.lqs.flink.job_scheduler.core.exception.UnSupportSourceTypeException;
import cn.lqs.flink.job_scheduler.core.job.DataStream;
import cn.lqs.flink.job_scheduler.core.job.FlinkJob;
import cn.lqs.flink.job_scheduler.infrastruction.util.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static cn.lqs.flink.job_scheduler.core.DataSTypes.KAFKA;
import static cn.lqs.flink.job_scheduler.core.DataSTypes.SOURCE_CONFIGURER_POSTFIX;

/**
 * @author @lqs
 */
@Slf4j
@Component
public final class FlinkRuntimeContext implements ApplicationContextAware {

    private StreamExecutionEnvironment env;

    private List<FlinkJob> jobs;
    private ApplicationContext springCtx;

    @Autowired
    private FlinkRuntimeContext(){}

    public List<FlinkJob> getJobs() {
        return jobs;
    }

    /**
     *
     * @param json json string
     * @return {@link FlinkRuntimeContext}
     */
    public FlinkRuntimeContext initFromJsonCfg(String json) {
        json = json.trim();
        Object config = JSON.parse(json);
        if (config instanceof JSONArray) {
            // 多个任务
            this.jobs = ((JSONArray) config).toJavaList(FlinkJob.class);
            configureJobsName(this.jobs);
            log.info("读取到[{}]个任务.", this.jobs.size());
            return this;
        }
        if (config instanceof JSONObject) {
            // 单个任务, 注意此处使用 Unmodifiable List 包裹
            this.jobs = List.of(((JSONObject) config).to(FlinkJob.class));
            configureJobsName(this.jobs);
            log.info("读取到单个 flink 任务. [{}]", this.jobs.get(0).getName());
            return this;
        }
        throw new FailedParseJsonException("无法正确解析 JSON 配置文件.");
    }

    private void configureJobsName(List<FlinkJob> jobs) {
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
        switch (type) {
            case KAFKA:
                log.info("配置 KAFKA-SOURCE");
                this.springCtx.getBean(KAFKA + SOURCE_CONFIGURER_POSTFIX, SourceConfigurer.class).configure(this.env);
                break;
        }
        if (KAFKA.equals(type)) {

            List<String> topics = source.getJSONArray("topic").toJavaList(String.class);
            source.getJSONObject("props");
            this.env.addSource(new FlinkKafkaConsumer<>(topics, new SimpleStringSchema(), null));
        }
        throw new UnSupportSourceTypeException(type);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springCtx = applicationContext;
    }
}
