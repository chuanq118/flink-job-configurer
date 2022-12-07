package cn.lqs.flink.job_scheduler.core.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.streaming.api.datastream.DataStream;

/**
 * @author @lqs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataStreamSinkWrapper<TYPE> {

    /**
     * Flink 返回的抽象数据流
     */
    private DataStream<? extends TYPE> dataStream;
    /**
     * 标明数据类型
     */
    private Class<? extends TYPE> dataClass;
}
