package com.leonardobishop.playerskills2.menu;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skills.Skill;
import com.leonardobishop.playerskills2.utils.Config;
import com.leonardobishop.playerskills2.utils.ConfigEditWrapper;
import com.leonardobishop.playerskills2.utils.CreatorConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigSkillMenu extends Menu {

    private final PlayerSkills plugin;
    private final Player player;
    private final Skill skill;
    private final HashMap<Integer, CreatorConfigValue> slotToConfigValue = new HashMap<>();
    private int save;
    private int discard;

    public ConfigSkillMenu(PlayerSkills plugin, Player player, Skill skill) {
        this.plugin = plugin;
        this.player = player;
        this.skill = skill;
    }

    @Override
    public Inventory toInventory() {
        int size = skill.getCreatorConfigValues().size();

        if (size < 9) size = 18;
        else if (size < 18) size = 27;
        else if (size < 27) size = 36;
        else if (size < 36) size = 45;
        else size = 54;

        Inventory inventory = Bukkit.createInventory(null, size, "Skill Editor - " + skill.getName());

        int slot = 0;
        for (CreatorConfigValue conf : skill.getCreatorConfigValues()) {
            ItemStack is = new ItemStack(Material.PAPER, 1);
            ItemMeta ism = is.getItemMeta();
            ism.setDisplayName(ChatColor.RED + conf.getKey());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.RED + "Description: " + ChatColor.WHITE + conf.getDescription());
            lore.add(ChatColor.RED + "Value: " + ChatColor.WHITE + conf.getValue());
            lore.add(" ");
            lore.add(ChatColor.RED + "Type: " + ChatColor.YELLOW + conf.getType());
            lore.add(ChatColor.RED + "Accepts: " + ChatColor.YELLOW + conf.getType().getHelp());
            lore.add(ChatColor.RED + "Default: " + ChatColor.DARK_GRAY + conf.getDefault());
            ism.setLore(lore);
            is.setItemMeta(ism);

            inventory.setItem(slot, is);
            slotToConfigValue.put(slot, conf);
            slot++;

            if (slot >= 53) {
                break;
            }
        }

        ItemStack save = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta savem = save.getItemMeta();
        savem.setDisplayName(ChatColor.GREEN + "Save & Reload");
        List<String> savel = new ArrayList<>();
        savel.add(ChatColor.YELLOW + "Write changes to the config file and reload the skills.");
        savem.setLore(savel);
        save.setItemMeta(savem);

        ItemStack discard = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta discardm = discard.getItemMeta();
        discardm.setDisplayName(ChatColor.RED + "Discard");
        List<String> discardl = new ArrayList<>();
        discardl.add(ChatColor.YELLOW + "Discard any changes and reload old values.");
        discardm.setLore(discardl);
        discard.setItemMeta(discardm);

        this.save = inventory.getSize() - 1;
        this.discard = inventory.getSize() - 2;

        inventory.setItem(this.save, save);
        inventory.setItem(this.discard, discard);

        return inventory;
    }

    @Override
    public void onClick(int slot) {
        if (slot == save) {
            player.sendMessage(ChatColor.GREEN + "Writing...");
            plugin.writeSkillConfigCreatorValuesToFile();
            plugin.reloadSkillConfigs();
            player.sendMessage(ChatColor.GREEN + "Done.");
            ConfigMenu menu = new ConfigMenu(plugin, player);
            MenuController.open(player, menu);
        } else if (slot == discard) {
            plugin.reloadSkillConfigs();
            player.closeInventory();
            ConfigMenu menu = new ConfigMenu(plugin, player);
            MenuController.open(player, menu);
        } else if (slotToConfigValue.containsKey(slot)) {
            CreatorConfigValue skill = slotToConfigValue.get(slot);
            ConfigEditWrapper config = new ConfigEditWrapper(skill, this.skill);
            player.closeInventory();
            plugin.lockPlayerEditor(player, config);
            player.sendMessage(ChatColor.GREEN + "Please enter the new value of " + ChatColor.WHITE + skill.getKey() + ChatColor.GREEN + " in chat.");
            player.sendMessage(ChatColor.GREEN + "Type: " + ChatColor.WHITE + skill.getType());
            player.sendMessage(ChatColor.GREEN + "Accepts: " + ChatColor.WHITE + skill.getType().getHelp());
            player.sendMessage(ChatColor.GREEN + "Default: " + ChatColor.DARK_GRAY + skill.getDefault());
        }
    }

    @Override
    public void onClose() {

    }

}
