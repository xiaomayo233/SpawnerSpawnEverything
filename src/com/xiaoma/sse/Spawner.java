package com.xiaoma.sse;

import com.xiaoma.sse.copy.ItemStack;
import com.xiaoma.sse.copy.Location;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.util.*;

public class Spawner implements Serializable {

    private static final long serialVersionUID = 7692875969003899113L; // 序列化ID

    private static final Set<Spawner> SPAWNERS = new HashSet<>(); // 存储所有刷怪箱

    private static final File SPAWNERS_FILE = new File(Plugin.PLUGIN_DIRECTORY ,"spawners.sse"); // 刷怪箱对象的存放路径

    private final List<ItemStack> items; // 刷怪对象
    private final Location loc; // 刷怪箱位置

    static {
        SPAWNERS.addAll(read());
    }

    // 构造方法
    public Spawner(Location loc, ItemStack... items) {
        this.loc = loc;
        this.items = Arrays.asList(items);
    }

    public Spawner(Location loc, Collection<ItemStack> item) {
        this(loc, item.toArray(new ItemStack[0]));
    }

    public static Set<Spawner> getSpawners() {
        return SPAWNERS;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public Location getLocation() {
        return loc;
    }

    // 放置刷怪箱
    public void place() {
        if (SPAWNERS.contains(this)) {
            throw new IllegalStateException("放置失败，该刷怪笼已放置");
        }
        SPAWNERS.add(this);

        save(SPAWNERS);
    }

    // 摧毁刷怪箱
    public void destroy() {
        if (!SPAWNERS.contains(this)) {
            throw new IllegalStateException("删除失败，该刷怪笼未放置");
        }

        SPAWNERS.remove(this);

        save(SPAWNERS);
    }

    public org.bukkit.inventory.ItemStack getDropItem(){
        List<String> lore = new ArrayList<>(); // 设置lore
        for (ItemStack item : items) {
            lore.add(String.format("§c[SSE]生成: %s,数量: %d,Durability: %d", item.getItemStack().getType(), item.getAmount(), item.getDurability()));
        }

        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(Material.MOB_SPAWNER);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    // 保存所有刷怪箱信息到文件
    public static void save(Set<Spawner> spawners) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SPAWNERS_FILE))) {
            for (Spawner spawner : spawners) {
                // 以二进制的形式向文件写入刷怪箱对象
                out.writeObject(spawner);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 读取所有刷怪箱信息到文件
    public static Set<Spawner> read() {
        Set<Spawner> spawners = new HashSet<>();

        if (!SPAWNERS_FILE.exists()) {
            return new HashSet<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SPAWNERS_FILE))) {
            // 以二进制的形式向文件读取刷怪箱对象

            while (true) {
                Object o = in.readObject();
                if (o instanceof Spawner) {
                    spawners.add((Spawner) o);
                }
            }
        } catch (EOFException e) {
            // do nothing
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return spawners;
    }

    // 比较两个刷怪箱是否相同
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Spawner) {
            Spawner o = (Spawner) obj;
            return Objects.equals(o.loc, loc);
        }

        return false;
    }
}
