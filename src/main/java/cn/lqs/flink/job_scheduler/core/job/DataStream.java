package cn.lqs.flink.job_scheduler.core.job;


import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 标记数据流向
 * @author @lqs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataStream {

    // 先使用通用的 JSON 实体类接受,之后进行进一步处理.
    private JSONObject source;
    private JSONObject sink;
    private List<DataProcessFunction> process;

}
