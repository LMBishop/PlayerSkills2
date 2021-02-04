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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigMenu extends Menu {

    private PlayerSkills plugin;
    private Player player;
    private HashMap<Integer, Skill> slotToSkill = new HashMap<>();

    public ConfigMenu(PlayerSkills plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public Inventory toInventory() {
        int size = plugin.getSkillRegistrar().size();

        if (size < 9) size = 9;
        else if (size < 18) size = 18;
        else if (size < 27) size = 27;
        else if (size < 36) size = 36;
        else if (size < 45) size = 45;
        else size = 54;

        Inventory inventory = Bukkit.createInventory(null, size, "Skill Editor - Menu");

        slotToSkill.clear();
        int slot = 0;
        for (Skill skill : plugin.getSkillRegistrar().values()) {
            ItemStack is = new ItemStack(Config.get(plugin, skill.getItemLocation()).getItemStack().getType());
            ItemMeta ism = is.getItemMeta();
            ism.setDisplayName(ChatColor.RED + skill.getName());
            is.setItemMeta(ism);

            inventory.setItem(slot, is);

            slotToSkill.put(slot, skill);
            slot++;

            if (slot >= 53) {
                break;
            }
        }

        return inventory;
    }

    @Override
    public void onClick(int slot) {
        if (slotToSkill.containsKey(slot)) {
            Skill skill = slotToSkill.get(slot);
            ConfigSkillMenu config = new ConfigSkillMenu(plugin, player, skill);
            MenuController.open(player, config);
        }
    }

    @Override
    public void onClose() {

    }

}
