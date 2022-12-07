package cn.lqs.flink.job_scheduler.core.process;

import cn.lqs.flink.job_scheduler.core.job.DataStreamSourceWrapper;
import org.apache.flink.streaming.api.datastream.DataStream;

/**
 * 包裹 Source -> Sink 的逻辑. <br>
 * INPUT - 输入数据类型. <br>
 * OUT - 输出数据类型.
 * @author @lqs
 */
public interface ProcessFunctionWrapper<INPUT, OUT>  {

    DataStream<OUT> process(DataStreamSourceWrapper<INPUT> dsWp);

    Class<OUT> getOutCls();

    Class<INPUT> getInputCls();

}
