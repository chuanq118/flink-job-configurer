# Flink Job Configurer
> 读取配置文件启动 flink 任务

## FlinkRuntimeContext
1. 读取配置文件
2. 遍历并尝试配置每个 job


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
password=lqservice.cn
# 要连接的 db 编号 默认为 0
db = 0
# 连接超时时间
timeout = 5000,
# 最小闲置连接(连接池)
min-conn = 1
# 最大闲置连接(连接池)
max-conn = 1
```