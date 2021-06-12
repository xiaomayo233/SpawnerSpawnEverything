package com.xiaoma.sse.utils;

import com.xiaoma.sse.Plugin;

import java.io.*;
import java.util.Properties;

/**
 * 配置文件交互类
 * 用于读写配置文件内容
 */
public class Property {
    private final Properties PROPERTIES = new Properties();

    // 更新内容到配置文件
    public void save() {
        try {
            PROPERTIES.store(new FileOutputStream(file), "Comment");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        try {
            FileInputStream in = new FileInputStream(file);
            PROPERTIES.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 写入一个字符串
    public void putString(String key, String value) {
        PROPERTIES.setProperty(key, value);
        save();
    }

    // 写入一个byte
    public void putByte(String key, byte value) {
        PROPERTIES.setProperty(key, Byte.toString(value));
        save();
    }

    // 写入一个short
    public void putShort(String key, short value) {
        PROPERTIES.setProperty(key, Short.toString(value));
        save();
    }

    // 写入一个int
    public void putInt(String key, int value) {
        PROPERTIES.setProperty(key, Integer.toString(value));
        save();
    }

    // 写入一个long
    public void putLong(String key, long value) {
        PROPERTIES.setProperty(key, Long.toString(value));
        save();
    }

    // 写入一个float
    public void putFloat(String key, float value) {
        PROPERTIES.setProperty(key, Float.toString(value));
        save();
    }

    // 写入一个double
    public void putDouble(String key, double value) {
        PROPERTIES.setProperty(key, Double.toString(value));
        save();
    }

    // 写入一个boolean
    public void putBoolean(String key, boolean value) {
        PROPERTIES.setProperty(key, Boolean.toString(value));
        save();
    }


    // 读取一个字符串
    public String getString(String key) {
        load();
        if (!PROPERTIES.containsKey(key)) {
            putString(key, "");
            return "";
        }
        return PROPERTIES.getProperty(key);
    }

    // 读取一个byte
    public byte getByte(String key) {
        load();
        if (!PROPERTIES.containsKey(key)) {
            putString(key, "0");
            return 0;
        }
        return Byte.parseByte(getString(key));
    }

    // 读取一个short
    public short getShort(String key) {
        load();
        if (!PROPERTIES.containsKey(key)) {
            putString(key, "0");
            return 0;
        }
        return Short.parseShort(getString(key));
    }

    // 读取一个int
    public int getInt(String key) {
        load();
        if (!PROPERTIES.containsKey(key)) {
            putString(key, "0");
            return 0;
        }
        return Integer.parseInt(getString(key));
    }

    // 读取一个float
    public float getFloat(String key) {
        load();
        if (!PROPERTIES.containsKey(key)) {
            putString(key, "0.0");
            return 0;
        }
        return Float.parseFloat(getString(key));
    }

    // 读取一个double
    public double getDouble(String key) {
        load();
        if (!PROPERTIES.containsKey(key)) {
            putString(key, "0.0");
            return 0;
        }
        return Double.parseDouble(getString(key));
    }

    // 读取一个boolean
    public boolean getBoolean(String key) {
        load();
        if (!PROPERTIES.containsKey(key)) {
            putString(key, "false");
            return false;
        }
        return Boolean.parseBoolean(getString(key));
    }

    private final File file;

    public File getFile() {
        return file;
    }

    public Property(String f) {
        this.file = new File(Plugin.PLUGIN_DIRECTORY,f);
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new IllegalStateException("create config failed");
                }
            }
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean exists(){
        return file.exists();
    }

}
