/*
 * See the file "LICENSE.txt", which is provided in this
 * source code package, for the full license governing this code.
 */

package com.leonardobishop.playerskills2.utils;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.regex.Pattern;

public class Config {

    public static ConfigObject get(Plugin plugin, String path) {
        if (plugin.getConfig().contains(path)) {
            Object object = plugin.getConfig().get(path);
            return new ConfigObject(plugin, path, object, plugin.getConfig());
        }
        return new ConfigObject(plugin, path, null, plugin.getConfig());
    }

    public static ConfigObject get(Plugin plugin, String path, Object defaultValue) {
        if (plugin.getConfig().contains(path)) {
            Object object = plugin.getConfig().get(path);
            return new ConfigObject(plugin, path, object, plugin.getConfig(), defaultValue);
        }
        return new ConfigObject(plugin, path, null, plugin.getConfig(), defaultValue);
    }

    public static ConfigObject get(Plugin plugin, FileConfiguration config, String path) {
        if (config.contains(path)) {
            Object object = config.get(path);
            return new ConfigObject(plugin, path, object, config);
        }
        return new ConfigObject(plugin, path, null, config);
    }

    public static ConfigObject get(Plugin plugin, FileConfiguration config, String path, Object defaultValue) {
        if (config.contains(path)) {
            Object object = config.get(path);
            return new ConfigObject(plugin, path, object, config, defaultValue);
        }
        return new ConfigObject(plugin, path, null, config, defaultValue);
    }

    public static class ConfigObject {
        Object object;
        String path;
        Object defaultValue;
        Plugin plugin;
        FileConfiguration config;

        ConfigObject(Plugin plugin, String path, Object object, FileConfiguration config) {
            this.plugin = plugin;
            this.path = path;
            this.object = object;
            this.defaultValue = object;
            this.config = config;
        }

        ConfigObject(Plugin plugin, String path, Object object, FileConfiguration config, Object defaultValue) {
            this.plugin = plugin;
            this.path = path;
            this.object = object;
            this.defaultValue = defaultValue;
            this.config = config;
            if (this.object == null) {
                this.object = this.defaultValue;
            }
        }

        public int getInt() {
            return (Integer) object;
        }

        public double getDouble() {
            return (Double) object;
        }

        public String getString() {
            return (String) object;
        }

        public List<String> getStringList() {
            return (List<String>) object;
        }

        public List<String> getColoredStringList() {
            List<String> stringList = (List<String>) object;
            List<String> coloredStringList = new ArrayList<>();
            for (String s : stringList) {
                coloredStringList.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            return coloredStringList;
        }

        public String getColoredString() {
            return ChatColor.translateAlternateColorCodes('&', (String) object);
        }

        public boolean getBoolean() {
            return (Boolean) object;
        }

        public Set<String> getKeys() {
            return ((ConfigurationSection) object).getKeys(false);
        }

        public boolean isNull() {
            return object == null;
        }

        public ItemStack getItemStack() {
            return getItemStack(new HashMap<>());
        }

        public ItemStack getItemStack(Map<String, String> placeholders) {
            String cName = Config.get(plugin, config, path + ".name").getString();
            int cAmount = Config.get(plugin, config,path + ".amount", 1).getInt();
            String cEnchantments = Config.get(plugin, config,path + ".enchantments", "").getString();
            List<String> cLore = Config.get(plugin, config,path + ".lore").getStringList();
            String cType;
            if (Config.get(plugin, config,path + ".item").getString() != null) {
                cType = Config.get(plugin, config,path + ".item").getString();
            } else if (Config.get(plugin, config,path + ".type").getString() != null) {
                cType = Config.get(plugin, config,path + ".type").getString();
            } else if (Config.get(plugin, config,path + ".material").getString() != null) {
                cType = Config.get(plugin, config,path + ".material").getString();
            } else {
                cType = path + ".type";
            }

            String name;
            Material type = null;
            int data = 0;
            List<String> lore = new ArrayList<>();
            if (cLore != null) {
                for (String s : cLore) {
                    for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                        s = s.replace(placeholder.getKey(), placeholder.getValue());
                    }
                    lore.add(ChatColor.translateAlternateColorCodes('&', s));
                }
            }
            if (StringUtils.isNumeric(cType)) {
                type = Material.getMaterial(Integer.parseInt(cType));
            } else if (Material.getMaterial(cType) != null) {
                type = Material.getMaterial(cType);
            } else if (cType.contains(":")) {
                String[] parts = cType.split(Pattern.quote(":"));
                if (parts.length > 1) {
                    if (StringUtils.isNumeric(parts[0])) {
                        type = Material.getMaterial(Integer.parseInt(parts[0]));
                    } else if (Material.getMaterial(parts[0]) != null) {
                        type = Material.getMaterial(parts[0]);
                    }
                    if (StringUtils.isNumeric(parts[1])) {
                        data = Integer.parseInt(parts[1]);
                    }
                }
            }

            if (type == null) {
                type = Material.STONE;
            }

            ItemStack is = new ItemStack(type, 1, (short) data);
            ItemMeta ism = is.getItemMeta();
            ism.setLore(lore);
            if (cName != null) {
                name = ChatColor.translateAlternateColorCodes('&', cName);
                for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                    name = name.replace(placeholder.getKey(), placeholder.getValue());
                }
                ism.setDisplayName(name);
            }
            String[] enchantments = cEnchantments.split(Pattern.quote(","));
            for (int i = 0; i < enchantments.length; i++) {
                String[] enchantment = enchantments[i].split(Pattern.quote(":"));
                if (enchantment.length < 2) {
                    continue;
                }
                Enchantment e = Enchantment.getByName(enchantment[0]);
                if (e == null) {
                    continue;
                }
                ism.addEnchant(e, Integer.parseInt(enchantment[1]), true);
            }
            is.setItemMeta(ism);
            is.setAmount(cAmount);

            return is;
        }
    }

}
