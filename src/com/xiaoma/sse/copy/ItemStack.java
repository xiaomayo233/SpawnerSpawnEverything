package com.xiaoma.sse.copy;

import org.bukkit.Material;

import java.io.Serializable;

/**
 * @see org.bukkit.inventory.ItemStack
 */
public class ItemStack implements Serializable {

    private final String type;
    private final int amount;
    private final short durability;

    private static final long serialVersionUID = -4379130890923694346L;

    public ItemStack(org.bukkit.inventory.ItemStack item){
        type = item.getType().toString();
        amount = item.getAmount();
        durability = item.getDurability();
    }

    public Material getType() {
        return Material.getMaterial(type);
    }

    public int getAmount() {
        return amount;
    }

    public short getDurability() {
        return durability;
    }

    public org.bukkit.inventory.ItemStack getItemStack(){
        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(getType());
        item.setAmount(amount);
        item.setDurability(durability);
        return item;
    }
}

