## Flink Job
> 读取配置文件启动 flink 任务

### Parallel to IOC
核心执行类独立于 Spring 容器.可随时去除 Spring 依赖.

### 配置
```json5
{
  "job": {
    "name": "demo",  // 自定义任务名称
    "setting": {
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
    "data-stream": [
      {
        "source": {
          "type": "kafka", // 枚举值 KAFKA,REDIS,MYSQL ...
          "name": "kafka-source-demo", // 自定义 source function 名称
          "topic": ["aaa", "bbb"],  // 此 key 仅在 type=kafka 下有效
          // 连接的 properties
          "props": {
            "bootstrap.servers": "localhost:9092"
          }
        },
        "sink": {
        },
        // 自定义的处理函数
        "process": [
          {
            "order": 6,
            "function": "cn.lqs.flink.job_scheduler.core.process.LogProcessFunc"
          }
        ]
      }
    ]
  }
}
```

