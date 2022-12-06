package cn.lqs.flink.job_scheduler.infrastruction.util;

import java.util.HashMap;

/**
 * @author @lqs
 */
public class ParameterUtil {

    private final HashMap<String, String> paramMap;

    public ParameterUtil(String prefix, String... args) {
        this.paramMap = new HashMap<>(args.length / 2);
        for (int i = 0; i < args.length; i+=2) {
            if (args[i].startsWith(prefix)) {
                this.paramMap.put(args[i].substring(prefix.length()), args[i + 1]);
            }
        }
    }

    public String getParam(String key) {
        return paramMap.get(key);
    }

    public String getParamOrDefault(String key, String defaultValue) {
        return paramMap.getOrDefault(key, defaultValue);
    }
}
