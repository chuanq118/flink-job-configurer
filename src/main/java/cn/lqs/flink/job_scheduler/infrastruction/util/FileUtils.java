package cn.lqs.flink.job_scheduler.infrastruction.util;

import java.io.*;

/**
 * @author @lqs
 */
public class FileUtils {

    private final static int NEED_BUFFER_LIMIT = 1 << 12;

    public static void writeByteArrayFile() throws IOException {
        throw new UnsupportedOperationException("未实现");
    }


    public static void writeStringToFile(String text, File file) throws IOException {
        if (text.length() > NEED_BUFFER_LIMIT) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
                bw.write(text);
            }
        } else {
           try (FileWriter fw = new FileWriter(file)){
               fw.write(text);
           }
        }
    }

    public static String readFileToString(File target) throws IOException {
        Reader reader = null;
        try {
            if (target.length() > NEED_BUFFER_LIMIT) {
                reader = new BufferedReader(new FileReader(target));
            } else {
                reader = new FileReader(target);
            }
            char[] buffer = new char[1 << 10];
            StringBuilder sb = new StringBuilder();
            int len;
            while ((len = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, len);
            }
            return sb.toString();
        }finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
