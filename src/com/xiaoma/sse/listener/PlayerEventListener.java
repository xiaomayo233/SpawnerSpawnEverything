package com.xiaoma.sse.listener;

import com.xiaoma.sse.Plugin;
import com.xiaoma.sse.Spawner;
import com.xiaoma.sse.copy.ItemStack;
import com.xiaoma.sse.copy.Location;
import com.xiaoma.sse.event.SpawnerDestroyEvent;
import com.xiaoma.sse.utils.Property;
import com.xiaoma.sse.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerEventListener implements Listener {
    static {
        // 提前生成好配置文件
        Plugin.CONFIG.getBoolean("crafting");
        Plugin.CONFIG.getBoolean("wash");
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock(); // 获取触发事件的方块对象
        if (block.getType() == Material.MOB_SPAWNER) { // 判断触发事件的方块是否是刷怪笼
            org.bukkit.inventory.ItemStack hand = event.getItemInHand();
            List<String> lorries = hand.getItemMeta().getLore();
            List<ItemStack> items = new ArrayList<>();

            if (lorries != null) {
                for (String lore : lorries) {
                    if (lore.startsWith("§c[SSE]")) { // 判断是否是SSE刷怪笼
                        Map<String, String> params = Utils.getParams(lore);
                        String material = params.get("生成");
                        int amount = Integer.parseInt(params.get("数量"));
                        short durability = Short.parseShort(params.get("Durability"));

                        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(Material.getMaterial(material), amount);
                        item.setDurability(durability);
                        items.add(new ItemStack(item));
                    }
                }
            }

            if (items.size() > 0) {
                Location location = new Location(event.getBlockPlaced().getLocation()); // 获取触发事件的方块的位置

                Spawner spawner = new Spawner(location, items); // 实例化SSE刷怪笼对象

                spawner.place(); // 放置刷怪笼
            }

        }
    }

    @EventHandler
    public void onDestroy(BlockBreakEvent event) {
        Location loc = new Location(event.getBlock().getLocation()); // 获取触发事件的方块的位置
        Set<Spawner> spawners = Spawner.getSpawners(); // 获取所有的SSE刷怪笼对象
        for (Spawner spawner : spawners) {
            // 判断是否是SSE刷怪笼
            if (Objects.equals(loc, spawner.getLocation())) {

                org.bukkit.inventory.ItemStack dropItem = spawner.getDropItem();
                SpawnerDestroyEvent e = new SpawnerDestroyEvent(spawner, dropItem, event.getPlayer());
                Bukkit.getPluginManager().callEvent(e); // 告诉别的监听器SSE刷怪笼被破坏了

                if (!e.isCancel()) {
                    spawner.destroy();
                    loc.getLocation().getWorld().dropItem(loc.getLocation(), dropItem);
                }

                break;
            }
        }
    }

    @EventHandler
    public void onExtend(BlockPistonExtendEvent event) {
        if (Plugin.CONFIG.getBoolean("crafting")) { // 判断是否开启合成功能
            if (event.getDirection() == BlockFace.DOWN) {
                org.bukkit.Location loc = event.getBlock().getLocation();
                loc.setY(loc.getY() - 0.5);
                loc.setX(loc.getX() + 0.5);
                loc.setZ(loc.getZ() + 0.5);

                // 获取该位置附近0.5格内的所有实体
                Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5);

                // lambda表达式 获取活塞下的所有物品
                List<Entity> items = entities.stream().filter((e) -> e.getType() == EntityType.DROPPED_ITEM).collect(Collectors.toList());

                List<org.bukkit.inventory.ItemStack> spawners = new ArrayList<>();

                // lambda表达式 获取活塞下的所有刷怪笼物品
                items.stream().filter((e) -> ((Item) e).getItemStack().getType() == Material.MOB_SPAWNER).forEach((e) -> {
                    int amount = ((Item) e).getItemStack().getAmount();
                    for (int i = 0; i < amount; i++) { // 将一个物品栈中多个物品进行拆分成一个个的物品
                        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(Material.MOB_SPAWNER);
                        item.setItemMeta(((Item) e).getItemStack().getItemMeta());
                        spawners.add(item);
                    }
                });

                // lambda表达式 获取距离活塞10格以内的玩家
                List<Player> nearPlayer = loc.getWorld().getPlayers().stream().filter((p) -> p.getLocation().distance(loc) < 10).collect(Collectors.toList());

                // 判断刷怪笼数量
                switch (spawners.size()) {
                    case 1:
                        org.bukkit.inventory.ItemStack spawner = spawners.get(0);
                        ItemMeta itemMeta = spawner.getItemMeta();

                        // 获取到原本刷怪笼的lore属性
                        List<String> lore = itemMeta.getLore();
                        lore = lore == null ? new ArrayList<>() : lore;

                        for (Entity e : items) {
                            org.bukkit.inventory.ItemStack item = ((Item) e).getItemStack();
                            Material type = item.getType();
                            if (type != Material.MOB_SPAWNER) {
                                if (!Plugin.ITEM_PERMISSION.getBoolean(type.toString())) {
                                    for (Player player : nearPlayer) {
                                        player.sendMessage(String.format("检测到%s, 服务器未开启此物品的权限！", type.toString()));
                                    }
                                    continue;
                                }
                                boolean flag = false;
                                // 修改lore
                                for (int i = 0; i < lore.size(); i++) {
                                    String l = lore.get(i);
                                    Map<String, String> params = Utils.getParams(l);
                                    if (item.getType().toString().equals(params.get("生成")) &&
                                            Integer.toString(item.getDurability()).equals(params.get("Durability"))) {
                                        int amount = Integer.parseInt(params.get("数量")) + item.getAmount();
                                        lore.set(i, String.format("§c[SSE]生成: %s,数量: %d,Durability: %d", item.getType(), amount, item.getDurability()));
                                        flag = true;
                                    }
                                }
                                if (!flag) {
                                    lore.add(String.format("§c[SSE]生成: %s,数量: %d,Durability: %d", item.getType(), item.getAmount(), item.getDurability()));

                                }
                            }
                            // 移除这个掉落物，包括刷怪笼，后面会将刷怪笼重新返回
                            e.remove();
                        }

                        // 更新lore
                        itemMeta.setLore(lore);
                        spawner.setItemMeta(itemMeta);
                        // 使物品掉落在世界中
                        loc.getWorld().dropItem(loc, spawner);
                        break;
                    default:
                        for (Player player : nearPlayer) {
                            player.sendMessage("每次合成仅允许一个刷怪笼！");
                        }
                    case 0:
                }
            }
        }
    }

    @EventHandler
    public void onWash(PlayerInteractEvent event) {
        if (Plugin.CONFIG.getBoolean("wash")) {
            Block block = event.getClickedBlock();
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.CAULDRON) {
                // 玩家用炼药锅洗刷怪笼
                org.bukkit.inventory.ItemStack hand = event.getPlayer().getItemInHand();
                ItemMeta meta = hand.getItemMeta();
                List<String> lore = meta.getLore();
                lore = lore == null ? new ArrayList<>() : lore;
                // 去除lore中的所有SSE标签
                lore.removeIf(s -> s.startsWith("§c[SSE]"));
                meta.setLore(lore);
                hand.setItemMeta(meta);
                event.setCancelled(true);
            }
        }
    }
}
