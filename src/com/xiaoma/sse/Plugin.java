package com.xiaoma.sse;

import com.xiaoma.sse.listener.PlayerEventListener;
import com.xiaoma.sse.listener.SpawnerSpawnEventListener;
import com.xiaoma.sse.utils.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Plugin extends JavaPlugin {
    public static final File PLUGIN_DIRECTORY = new File("plugins/SpawnerSpawnEverything/");

    public static Plugin PLUGIN;

    public static final Property CONFIG = new Property("config.properties");

    public static final Property ITEM_PERMISSION = new Property("item_permission.properties");

    static {
        // 提前生成好物品权限文件
        for (Material value : Material.values()) {
            if ("".equals(ITEM_PERMISSION.getString(value.toString()))) {
                ITEM_PERMISSION.putBoolean(value.toString(), true);
            }
        }
    }


    @Override
    public void onEnable() {
        PLUGIN = this;

        if (!PLUGIN_DIRECTORY.exists()) {
            PLUGIN_DIRECTORY.mkdirs();
        }

        Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), PLUGIN); // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(new SpawnerSpawnEventListener(), PLUGIN); // 注册事件监听器
    }
}
