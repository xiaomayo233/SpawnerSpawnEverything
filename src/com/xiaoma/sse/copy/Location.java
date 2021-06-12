package com.xiaoma.sse.copy;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.Serializable;
import java.util.Objects;

public class Location implements Serializable {

    private static final long serialVersionUID = 8404674178312567831L;

    private final double x;
    private final double y;
    private final double z;
    private final String world;
    private final float pitch;
    private final float yaw;


    public Location(org.bukkit.Location l) {
        x = l.getX();
        y = l.getY();
        z = l.getZ();
        world = l.getWorld().getName();
        pitch = l.getPitch();
        yaw = l.getYaw();
    }

    public Location(World world, double x, double y, double z) {
        this(world, x, y, z, 0.0F, 0.0F);
    }

    public Location(World world, double x, double y, double z, float yaw, float pitch) {
        this.world = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            Location other = (Location) obj;
            if (!Objects.equals(this.world, other.world)) {
                return false;
            } else if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
                return false;
            } else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
                return false;
            } else if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
                return false;
            } else if (Float.floatToIntBits(this.pitch) != Float.floatToIntBits(other.pitch)) {
                return false;
            } else {
                return Float.floatToIntBits(this.yaw) == Float.floatToIntBits(other.yaw);
            }
        }
    }

    public org.bukkit.Location getLocation() {
        return new org.bukkit.Location(Bukkit.getWorld(world), x, y, z, pitch, yaw);
    }
}
