package cn.lqs.flink.job_scheduler.redis;

import cn.lqs.flink.job_scheduler.infrastruction.util.DateUtil;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

/**
 * @author @lqs
 */
public class RedisStringKVMapper<T> implements RedisMapper<T> {

    private final Class<?> dataCls;

    public RedisStringKVMapper(Class<?> dataCls) {
        this.dataCls = dataCls;
    }

    @Override
    public RedisCommandDescription getCommandDescription() {
        return new RedisCommandDescription(RedisCommand.SET);
    }

    @Override
    public String getValueFromData(T data) {
        return (String) dataCls.cast(data);
    }

    @Override
    public String getKeyFromData(T data) {
        return DateUtil.nowDatetimeNumberString();
    }
}
