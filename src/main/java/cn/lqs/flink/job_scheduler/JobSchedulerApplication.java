package cn.lqs.flink.job_scheduler;


import cn.lqs.flink.job_scheduler.core.FlinkRuntimeContext;
import cn.lqs.flink.job_scheduler.infrastruction.util.FileUtils;
import cn.lqs.flink.job_scheduler.infrastruction.util.ParameterUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;

/**
 * Flink 相关依赖被置为 provided. 同理其依赖的工具包同为 provided.<br>
 * 不能保证因版本问题最终运行环境中确实会有此相关依赖. <br>
 * 为防止找不到类,应该避免使用 provided 包下的相关类. <br>
 *   例如: org.apache.commons.io.FileUtils
 */
@SpringBootApplication
public class JobSchedulerApplication implements CommandLineRunner {

    static {
        // disable warning.
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);
            Class<?> cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            // ignore
        }
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JobSchedulerApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }


    @Override
    public void run(String... args) throws Exception {
        // 读取命令行参数
        ParameterUtil params = new ParameterUtil("-", args);
        // 读取 JSON 文件, 启动 flink
        createFlinkExecEnvAndRun(params.getParam("config"));
    }


    public void createFlinkExecEnvAndRun(String jsonPath) throws IOException {
        // assume json file in local file system
        String jsonCfg = FileUtils.readFileToString(Path.of(jsonPath).toFile());
        // 创建应用配置 CONTEXT
        FlinkRuntimeContext context = FlinkRuntimeContext.create(jsonCfg);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        context.setExecutionEnvironment(env).run();
    }
}
