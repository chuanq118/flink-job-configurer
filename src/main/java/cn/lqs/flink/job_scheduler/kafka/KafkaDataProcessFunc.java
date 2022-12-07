package cn.lqs.flink.job_scheduler.kafka;


import cn.lqs.flink.job_scheduler.core.job.DataStreamSourceWrapper;
import cn.lqs.flink.job_scheduler.core.process.ProcessFunctionWrapper;
import cn.lqs.flink.job_scheduler.infrastruction.util.DateUtil;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Locale;

/**
 * 此类仅仅作为一个参考, 包装了 Source -> Sink 的处理逻辑. <br>
 * 通过定义不同的实现类, 并在配置文件中进行声明, 来执行不同的处理逻辑.
 * @author @lqs
 */
public class KafkaDataProcessFunc implements ProcessFunctionWrapper<String, String> {


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
