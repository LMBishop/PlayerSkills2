package com.leonardobishop.playerskills2.menu;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skills.Skill;
import com.leonardobishop.playerskills2.utils.Config;
import com.leonardobishop.playerskills2.utils.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class SkillsMenu extends Menu {

    private PlayerSkills plugin;
    private Player player;
    private SPlayer sPlayer;

    public SkillsMenu(PlayerSkills plugin, Player player, SPlayer sPlayer) {
        this.plugin = plugin;
        this.player = player;
        this.sPlayer = sPlayer;
    }

    @Override
    public Inventory toInventory() {
        String title = Config.get(plugin, "gui.title").getColoredString();
        int size = Config.get(plugin, "gui.size").getInt();

        Inventory inventory = Bukkit.createInventory(null, size, title);

        if (plugin.getConfig().getBoolean("gui.background.enabled")) {
            ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7);
            ItemMeta backgroundm = background.getItemMeta();
            backgroundm.setDisplayName(" ");
            background.setItemMeta(backgroundm);

            ItemStack config;
            if ((config = Config.get(plugin, "gui.background").getItemStack()) != null) {
                background = config;
            }
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, background);
            }
        }

        StringBuilder experienceBar = new StringBuilder();
        experienceBar.append(ChatColor.GREEN);
        experienceBar.append(player.getLevel());
        experienceBar.append(" ");
        for (double f = 0; f <= player.getExp(); f = f + 0.03) {
            experienceBar.append(ChatColor.GREEN).append("|");
        }
        int toAdd = 30 - ChatColor.stripColor(experienceBar.toString()).length();
        for (int i = 0; i <= toAdd; i++) {
            experienceBar.append(ChatColor.GRAY).append("|");
        }

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{price}", plugin.getFundingSource().appendSymbol(String.valueOf(sPlayer.getNextPointPrice(plugin))));
        placeholders.put("{xpbar}", experienceBar.toString());
        placeholders.put("{points}", String.valueOf(sPlayer.getPoints()));

        for (Skill skill : plugin.getSkillRegistrar().values()) {
            HashMap<String, String> skillPlaceholders = new HashMap<>();
            skillPlaceholders.put("{prev}", skill.getPreviousString(sPlayer));
            if (sPlayer.getLevel(skill.getConfigName()) >= ((int) skill.getConfig().get("max-level"))) {
                skillPlaceholders.put("{next}", Config.get(plugin, "gui.placeholders.next-max", "--").getString());
                skillPlaceholders.put("{skillprice}", Config.get(plugin, "gui.placeholders.skillprice-max", "--").getString());
            } else {
                skillPlaceholders.put("{next}", skill.getNextString(sPlayer));
                skillPlaceholders.put("{skillprice}", String.valueOf(skill.getPriceOverride(sPlayer.getLevel(skill.getConfigName()) + 1)));
            }
            skillPlaceholders.put("{level}", String.valueOf(sPlayer.getLevel(skill.getConfigName())));
            skillPlaceholders.put("{max}", String.valueOf((int) skill.getConfig().get("max-level")));

            skillPlaceholders.putAll(placeholders);

            inventory.setItem((int) skill.getConfig().get("gui-slot"), Config.get(plugin, skill.getItemLocation()).getItemStack(skillPlaceholders));
        }

        for (String s : Config.get(plugin, "gui.other").getKeys()) {
            int slot = Config.get(plugin, "gui.other." + s + ".slot").getInt();
            ItemStack is = Config.get(plugin, "gui.other." + s).getItemStack(placeholders);

            inventory.setItem(slot, is);
        }

        return inventory;
    }

    @Override
    public void onClick(int slot) {
        for (Skill skill : plugin.getSkillRegistrar().values()) {
            if (slot == (int) skill.getConfig().get("gui-slot") && (sPlayer.getLevel(skill.getConfigName()) < ((int) skill.getConfig().get("max-level")))) {
                int price = skill.getPriceOverride(sPlayer.getLevel(skill.getConfigName()) + 1);
                if ((sPlayer.getPoints() >= price)) {
                    Runnable callback = () -> {
                        sPlayer.setLevel(skill.getConfigName(), sPlayer.getLevel(skill.getConfigName()) + 1);
                        sPlayer.setPoints(sPlayer.getPoints() - price);
                        player.playSound(player.getLocation(), Sounds.ORB_PICKUP.bukkitSound(), 2, 2);
                        MenuController.open(player, SkillsMenu.this);
                    };
                    if (Config.get(plugin, "gui-confirmation.enabled.purchase-skills").getBoolean()) {
                        ConfirmationMenu confirmationMenu = new ConfirmationMenu(plugin, player, sPlayer,
                                player.getOpenInventory().getTopInventory().getItem(slot), callback, SkillsMenu.this);
                        MenuController.open(player, confirmationMenu);
                    } else {
                        callback.run();
                    }
                    return;
                } else {
                    player.playSound(player.getLocation(), Sounds.ITEM_BREAK.bukkitSound(), 1, 0.6f);
                }
            }
        }


        if (slot == Config.get(plugin, "gui.other.points.slot").getInt()) {
            int price = sPlayer.getNextPointPrice(plugin);
            Runnable callback = () -> {
                if (plugin.getFundingSource().doTransaction(sPlayer, price, player)) {
                    sPlayer.setPoints(sPlayer.getPoints() + 1);
                    player.playSound(player.getLocation(), Sounds.CLICK.bukkitSound(), 1, 1);
                    MenuController.open(player, SkillsMenu.this);
                } else {
                    player.playSound(player.getLocation(), Sounds.ITEM_BREAK.bukkitSound(), 1, 0.6f);
                }
            };
            if (Config.get(plugin, "gui-confirmation.enabled.purchase-skill-points").getBoolean()) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(plugin, player, sPlayer,
                        player.getOpenInventory().getTopInventory().getItem(slot), callback, SkillsMenu.this);
                MenuController.open(player, confirmationMenu);
            } else {
                callback.run();
            }
        } else if (slot == Config.get(plugin, "gui.other.reset.slot").getInt()) {
            Runnable callback = () -> {
                if (sPlayer.getPoints() >= Config.get(plugin, "points.reset-price").getInt()) {
                    sPlayer.setPoints(sPlayer.getPoints() - Config.get(plugin, "points.reset-price").getInt());
                    for (String s : sPlayer.getSkills().keySet()) {
                        for (int i = 1; i <= sPlayer.getLevel(s); i++) {
                            sPlayer.setPoints(sPlayer.getPoints() + plugin.getSkillRegistrar().get(s).getPriceOverride(i));
                        }
                    }
                    sPlayer.getSkills().clear();
                    player.playSound(player.getLocation(), Sounds.EXPLODE.bukkitSound(), 1, 1);
                    MenuController.open(player, SkillsMenu.this);
                } else {
                    player.playSound(player.getLocation(), Sounds.ITEM_BREAK.bukkitSound(), 1, 0.6f);
                }
            };
            if (Config.get(plugin, "gui-confirmation.enabled.reset-skills").getBoolean()) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(plugin, player, sPlayer,
                        player.getOpenInventory().getTopInventory().getItem(slot), callback, SkillsMenu.this);
                MenuController.open(player, confirmationMenu);
            } else {
                callback.run();
            }
        }
    }

    @Override
    public void onClose() {

    }

}
