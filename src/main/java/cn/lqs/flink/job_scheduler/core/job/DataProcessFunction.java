package cn.lqs.flink.job_scheduler.core.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author @lqs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataProcessFunction {

    private long order;
    private String function;

}
