# Flink Job Configurer
> 读取配置文件启动一个 flink 任务

## FlinkRuntimeContext
> 执行逻辑
> 1. 遍历 data stream 进行配置
> 2. 通过指定的 source-configurer 配置 source
> 3. 调用使用指定的 wrapper-function 进行数据处理
> 4. 通过指定的 sink-configurer 配置 sink
> 5. 调用 flink execution environment 执行任务

### SourceConfigurer
> <code>SourceWrapper<INPUT_DATATYPE> flinkRuntimeContext.configureSource(cfg)</code>  
> type 需要是已经支持的数据源类型

以配置一个 kafka source 为例,
1. 需要指定 Source 配置器实现类名称(此处为"kafka-as-string") 并注入到 Spring 容器中 
```java
@Component("kafka-as-string" + SOURCE_CONFIGURER_POSTFIX)
public class KafkaSourceAsStringConfigurer implements SourceConfigurer {
    @Override
    public DataStreamSourceWrapper<String> configure(StreamExecutionEnvironment env, JSONObject cfg) {
        // 读取 topic 列表
        List<String> topics = cfg.getJSONArray(KAFKA_TOPIC_NAME).toJavaList(String.class);
        // 读取定义 source name
        String sourceName = cfg.getString(SourceSinkCfgNames.NAME);
        // 读取 kafka 的 props
        Properties properties = new Properties();
        properties.putAll(cfg.getJSONObject(PROPERTY).toJavaObject(Map.class));
        // 自定义配置 source 并且返回一个 source wrapper
        return new DataStreamSourceWrapper<String>(
                env.addSource(new FlinkKafkaConsumer<>(topics, new SimpleStringSchema(), properties), sourceName),
                String.class);
    }
}
```
2. 在配置文件中配置此 source
```json5
{
    // 指定配置数据源类型为 kafka
    "type": "kafka",
    // 自定义的 source name
    "name": "remote-kafka-server-source",
    // 前面定义的 configurer 名称 
    "configurer": "kafka-as-string",
    // 自定义属性
    "topic": [
      "aaa",
      "bbb"
    ],
    // 与 kafka 连接的 properties
    "props": {
      "bootstrap.servers": "host:port"
    }
}
```

### ProcessFunctionWrapper
> 需要实现接口 ProcessFunctionWrapper<INPUT, OUT> 并指定整个处理流程的输入数据类型和最终输出数据类型  
> ProcessFunctionWrapper 会接受来自 Source 的数据,因此其 INPUT 类型应于 Source 返回的类型相同  
> process 方法中可以实现自定义的数据处理逻辑,并最终返回指定输出数据类型 DataStream<OUT>(flink)

1. 参考配置类
```java
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
```
2. 在配置文件中指定全限定类名
```json5
[
  {
    "source": {
      // ....
    },
    "sink": {
      // ....
    },
    "process_wrapper": "cn.lqs.flink.job_scheduler.wrapper_functions.KafkaStringToRedisStringFuncWrapper"
  }
]
```


### SinkConfigurer
> 同 SourceConfigurer



## 配置参考
```json5
{
  // 自定义任务名称
  "name": "demo",
  "settings": {
    "speed": {
      "channel": 1,
      "bytes": 0
    },
    "errorLimit": {
      "record": 100
    },
    "restore": {
      "maxRowNumForCheckpoint": 0,
      "isRestore": false,
      "restoreColumnName": "",
      "restoreColumnIndex": 0
    },
    "log": {
      "isLogger": false,
      "level": "debug",
      "path": "",
      "pattern": ""
    }
  },
  // 数据流 Source -> Sink. 可以启动多个
  "data_stream": [
    {
      "source": {
        // 枚举值 KAFKA,REDIS,MYSQL ...
        "type": "",
        // 自定义 source function 名称
        "name": "",
        // 配置类名称
        "configurer": "",
        // 连接的 properties
        "props": {
          "bootstrap.servers": "localhost:9092"
        }
      },
      "sink": {
        // 同 source.type
        "type": "",
        // 自定义名称
        "name": "",
        // 配置类名称
        "configurer": "",
        // 参考配置属性
        "props": {
          "key": "value"
        }
      },
      // 自定义的处理函数
      "process_wrapper": "full class name to wrapper function."
    }
  ]
}
```


## Connectors 配置参考

### Kafka As Source


### Redis AS Sink

- Properties 配置详情  
```properties
# redis property 配置解析与默认值
# host
host = localhost
# port
port = 6379
# 是否需要密码
require_password = false
# 需要 require_password = true
password=??
# 要连接的 db 编号 默认为 0
db = 0
# 连接超时时间
timeout = 5000,
# 最小闲置连接(连接池)
min-conn = 1
# 最大闲置连接(连接池)
max-conn = 1
```