package com.xiaoma.sse.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static Map<String, String> getParams(String lore) {
        String[] data = lore.split(",");
        data[0] = data[0].substring("§c[SSE]".length());

        Map<String, String> map = new HashMap<>();
        for (String datum : data) {
            String[] KV = datum.split(": ");
            map.put(KV[0], join(Arrays.copyOfRange(KV, 1, KV.length)));
        }

        return map;
    }

    public static String join(String[] args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg);
        }
        return builder.toString();
    }
}
