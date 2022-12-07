package cn.lqs.flink.job_scheduler.core.job;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author @lqs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlinkJob implements Serializable {

    private final static long serialVersionUID = 61228923139L;

    private String name;
    // TODO parse setting to entity.
    private Map<String, Object> settings;

    @JSONField(name = "data_stream")
    private List<DataStream> dataStreams;
}
