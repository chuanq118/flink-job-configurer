package cn.lqs.flink.job_scheduler.core.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.streaming.api.datastream.DataStreamSource;

/**
 * @author @lqs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataStreamSourceWrapper<TYPE> {

    /**
     * Flink 返回的抽象数据流
     */
    private DataStreamSource<? extends TYPE> dataStreamSource;
    /**
     * 标明数据类型
     */
    private Class<? extends TYPE> dataClass;

}
