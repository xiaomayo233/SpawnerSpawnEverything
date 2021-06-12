package com.xiaoma.sse.listener;

import com.xiaoma.sse.Plugin;
import com.xiaoma.sse.copy.ItemStack;
import com.xiaoma.sse.copy.Location;
import com.xiaoma.sse.Spawner;
import com.xiaoma.sse.event.SpawnerSpawnEverythingEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpawnerSpawnEventListener implements Listener {

    private static final int LENGTH = 5; // 刷怪笼有效范围的长

    private static final int WIDTH = 5; // 刷怪笼有效范围的宽

    private static final int HEIGHT = 3; // 刷怪笼有效范围的高

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            org.bukkit.Location loc = event.getLocation(); // 获取刷怪箱的位置

            // 寻找最近刷怪笼位置
            Set<Spawner> spawners = Spawner.getSpawners();
            for (Iterator<Spawner> iterator = spawners.iterator(); iterator.hasNext(); ) {
                Spawner spawner = iterator.next();
                Location spawnerLoc = spawner.getLocation();
                try {

                    CreatureSpawner block = (CreatureSpawner) (Bukkit.getWorld(spawnerLoc.getWorld()).getBlockAt(spawnerLoc.getLocation()).getState());


                    if (block.getSpawnedType() == event.getEntity().getType()) { // 判断是否是同一类型的生物

                        if (
                                Math.abs(spawnerLoc.getX() - loc.getX()) < WIDTH &&
                                        Math.abs(spawnerLoc.getZ() - loc.getZ()) < LENGTH &&
                                        Math.abs(spawnerLoc.getY() - loc.getY()) < HEIGHT
                        ) {
                            List<ItemStack> items = spawner.getItems();

                            event.getEntity().remove();

                            for (ItemStack item : items) {
                                String type = item.getType().toString();
                                if (!Plugin.ITEM_PERMISSION.getBoolean(type)) {
                                    List<Player> nearPlayer = loc.getWorld().getNearbyEntities(loc, 10, 10, 10).stream().filter((e) -> e.getType() == EntityType.PLAYER).map((e) -> (Player) e).collect(Collectors.toList());
                                    for (Player player : nearPlayer) {
                                        player.sendMessage(String.format("检测到%s, 服务器未开启此物品的权限！", type));
                                    }

                                    continue;
                                }
                                SpawnerSpawnEverythingEvent e = new SpawnerSpawnEverythingEvent(spawner, Collections.singletonList(item));
                                Bukkit.getPluginManager().callEvent(e); // 调用事件，告诉事件监听器，SSE触发了

                                if (!e.isCancel()) {
                                    org.bukkit.inventory.ItemStack stack = item.getItemStack();
                                    int max = stack.getMaxStackSize();
                                    while (stack.getAmount() > max) {
                                        stack.setAmount(stack.getAmount() - max);
                                        loc.getWorld().dropItem(loc, new org.bukkit.inventory.ItemStack(stack.getType(), max, stack.getDurability()));
                                    }
                                    loc.getWorld().dropItem(loc, stack);
                                }
                            }

                            break;
                        }
                    }
                } catch (ClassCastException e) {
                    // 由于各种原因 服务器可能会导致未保存而被强制关闭
                    // 插件已保存数据但存档未保存
                    // 导致原本放置刷怪笼的位置变成了其他方块
                    // 会出现ClassCastException异常
                    // 将其捕获并删除该刷怪笼
                    iterator.remove();
                }
            }
        }
    }
}
