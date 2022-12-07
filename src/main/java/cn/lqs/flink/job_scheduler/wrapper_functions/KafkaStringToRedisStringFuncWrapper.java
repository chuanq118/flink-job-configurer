package cn.lqs.flink.job_scheduler.wrapper_functions;

import cn.lqs.flink.job_scheduler.core.job.DataStreamSourceWrapper;
import cn.lqs.flink.job_scheduler.core.process.ProcessFunctionWrapper;
import cn.lqs.flink.job_scheduler.infrastruction.util.DateUtil;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Locale;

/**
 * @author @lqs
 */
public class KafkaStringToRedisStringFuncWrapper implements ProcessFunctionWrapper<String, String> {
    @Override
    public DataStream<String> process(DataStreamSourceWrapper<String> dsWp) {
        // 将数据加上一个时间前缀后返回
        return dsWp.getDataStreamSource()
                .map(s -> {
                    return DateUtil.nowDatetimeNumberString() + "-" + s.toLowerCase(Locale.CHINA);
                }).disableChaining()
                .returns(String.class);
    }

    @Override
    public Class<String> getOutCls() {
        return String.class;
    }

    @Override
    public Class<String> getInputCls() {
        return String.class;
    }
}
