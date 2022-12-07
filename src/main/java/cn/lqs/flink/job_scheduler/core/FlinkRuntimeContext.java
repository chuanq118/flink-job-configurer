package cn.lqs.flink.job_scheduler.core;

import cn.lqs.flink.job_scheduler.core.exception.FailedParseJsonException;
import cn.lqs.flink.job_scheduler.core.exception.UnSupportSourceTypeException;
import cn.lqs.flink.job_scheduler.core.job.DataStream;
import cn.lqs.flink.job_scheduler.core.job.DataStreamSinkWrapper;
import cn.lqs.flink.job_scheduler.core.job.DataStreamSourceWrapper;
import cn.lqs.flink.job_scheduler.core.job.FlinkJob;
import cn.lqs.flink.job_scheduler.core.process.ProcessFunctionWrapper;
import cn.lqs.flink.job_scheduler.infrastruction.util.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
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
    private boolean isInit = false;

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
            isInit = true;
            return this;
        }
        if (config instanceof JSONObject) {
            // 单个任务, 注意此处使用 Unmodifiable List 包裹
            this.jobs = List.of(((JSONObject) config).to(FlinkJob.class));
            configureJobsName(this.jobs);
            log.info("读取到单个 flink 任务. [{}]", this.jobs.get(0).getName());
            isInit = true;
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
        // job.getSettings()
        // todo 解析 setting 并设置 env
        // this.env.setParallelism();
        log.info("任务配置信息 :: ?? 待");
    }

    /**
     * 核心执行方法,全部任务的配置在此处进行
     */
    public void run() throws Exception {
        if (this.env == null || !isInit) {
            log.error("无法在环境未完成装配的情况下启动.");
            return;
        }
        for (FlinkJob job : jobs) {
            log.info("##### 执行启动任务[{}] #####", job.getName());
            configureEnv(job);
            for (DataStream dataStream : job.getDataStreams()) {
                try {
                    // 配置 Source
                    DataStreamSourceWrapper<?> sourceWrapper = configureSource(dataStream.getSource());
                    // 配置 process function
                    DataStreamSinkWrapper<?> sinkWrapper = configureProcessFunc(dataStream.getProcessWrapperFunc(), sourceWrapper);
                    // 配置 Sink
                    // configureSink(dataStream.getSink());
                } catch (UnSupportSourceTypeException e) {
                    log.error("配置 flink 任务发生错误", e);
                    // 当前的 Source -> Sink 将不会执行
                }
            }
            this.env.execute(job.getName());
        }
    }

    /**
     * 配置 SOURCE 的核心方法,我们在 switch 里面决定当前的 source 是否能够被接受处理
     * @param source source config
     * @return {@link DataStreamSourceWrapper} 封装类.包含了 Flink 对此数据源产生的数据流已经数据类型(一般为String)
     * @throws UnSupportSourceTypeException 如果配置了不支持的 source.那么该 data-stream 就停止配置. <br> 如果 job 有多个 ds, 那么下一个 ds 配置不会因此取消.
     */
    private DataStreamSourceWrapper<?> configureSource(JSONObject source) throws UnSupportSourceTypeException {
        String type = source.getString("type").toLowerCase(Locale.ROOT);
        switch (type) {
            case KAFKA:
                log.info("配置 Kafka Source.");
                return this.springCtx.getBean(KAFKA + SOURCE_CONFIGURER_POSTFIX, SourceConfigurer.class).configure(this.env, source);
                // TODO 配置更多数据源
        }
        throw new UnSupportSourceTypeException(type);
    }

    /**
     * 配置处理函数
     * @param clsPath {@link ProcessFunctionWrapper} 实现类路径
     * @param dsWp {@link DataStreamSourceWrapper} 输入数据流
     * @return {@link DataStreamSinkWrapper} 输出数据流 用于 Sink
     * @param <T> 输入流数据泛型
     */
    private <T> DataStreamSinkWrapper<?> configureProcessFunc(String clsPath, DataStreamSourceWrapper<T> dsWp)  {
        ProcessFunctionWrapper<T, ?> func = null;
        try {
            // 从此处可以看出来目前 ProcessFunctionWrapper 的实现类必须包含在空参构造函数
            // TODO 优化
            func = (ProcessFunctionWrapper<T, ?>) Class.forName(clsPath).getDeclaredConstructors()[0].newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            log.error("处理函数的反射执行失败!");
            // ! ############# 这会导致整个进程结束 #############
            throw new RuntimeException(e);
        }
        return new DataStreamSinkWrapper<>(func.process(dsWp), func.getOutCls());
    }

    private void configureSink(JSONObject sink, DataStreamSinkWrapper<?> sinkWrapper) {
        // TODO FINISH Sink Code
    }


    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.springCtx = applicationContext;
    }
}
