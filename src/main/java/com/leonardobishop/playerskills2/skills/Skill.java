package com.leonardobishop.playerskills2.skills;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.utils.CreatorConfigValue;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Skill implements Listener {

    private PlayerSkills plugin;
    private String name;
    private String configName;
    private HashMap<String, Object> config = new HashMap<>();
    private HashMap<Integer, Integer> pointPriceOverrides = new HashMap<>();
    private ArrayList<CreatorConfigValue> creatorConfigValues = new ArrayList<>();
    private String itemLocation;

    public Skill(PlayerSkills plugin, String name, String configName) {
        this.plugin = plugin;
        this.name = name;
        this.configName = configName;
    }

    public final String getName() {
        return name;
    }

    public final String getConfigName() {
        return configName;
    }

    public final PlayerSkills getPlugin() {
        return plugin;
    }

    public HashMap<String, Object> getConfig() {
        return config;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }

    public abstract String getPreviousString(SPlayer player);

    public abstract String getNextString(SPlayer player);

    public ArrayList<CreatorConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    public void enable(PlayerSkills plugin) {

    }

    public int getPriceOverride(int level) {
        return pointPriceOverrides.getOrDefault(level, 1);
    }

    public HashMap<Integer, Integer> getPointPriceOverrides() {
        return pointPriceOverrides;
    }

    public double getDecimalNumber(String location) {
        Object obj = getConfig().get(location);
        if (obj == null) {
            return 0;
        }

        if (obj instanceof Integer) {
            return ((double) ((int) obj));
        } else {
            return (double) obj;
        }
    }
}
