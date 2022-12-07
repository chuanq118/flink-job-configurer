package cn.lqs.flink.job_scheduler.local;

import cn.lqs.flink.job_scheduler.infrastruction.util.FileUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author @lqs
 */
public class JsonTests {

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

    @Test void parseJson() throws IOException {
        String json = FileUtils.readFileToString(new File("D:\\Java\\flink\\job-scheduler\\docs\\job-config-example.json"));
        System.out.println(JSON.parse(json) instanceof JSONArray);
        System.out.println(JSON.parse(json) instanceof JSONObject);
    }
}
