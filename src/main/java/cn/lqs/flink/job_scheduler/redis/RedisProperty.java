package cn.lqs.flink.job_scheduler.redis;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;

/**
 * 定义支持的 Redis 配置项
 * @author @lqs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisProperty {

    private String host;
    @JSONField(name = "require_password", defaultValue = "false")
    private boolean requirePassword;
    private String password;
    private int port;

    @JSONField(name = "db", defaultValue = "0")
    private int database;

    @JSONField(name = "timeout", defaultValue = "5000")
    private int timeout;

    @JSONField(name = "min-conn", defaultValue = "1")
    private int minIdle;

    @JSONField(name = "max-conn", defaultValue = "1")
    private int maxIdle;


    public FlinkJedisConfigBase toFlinkJedisCfg() {
        FlinkJedisPoolConfig.Builder builder = new FlinkJedisPoolConfig.Builder()
                .setHost(this.host)
                .setPort(this.port);
        if (this.requirePassword) {
            builder.setPassword(this.password);
        }
        return builder.setDatabase(this.database)
                .setTimeout(this.timeout)
                .setMinIdle(this.minIdle)
                .setMaxIdle(this.maxIdle).build();
    }
}
