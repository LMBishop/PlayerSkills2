package com.leonardobishop.playerskills2.commands;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.menu.MenuController;
import com.leonardobishop.playerskills2.menu.SkillsMenu;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.utils.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SkillsCommand implements CommandExecutor {

    private PlayerSkills plugin;

    public SkillsCommand(PlayerSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Please use /skillsadmin instead.");
            return true;
        }
        Player player = (Player) sender;
        SPlayer sPlayer = SPlayer.get(player.getUniqueId());

        if (!Config.get(plugin, "options.menu-world-restriction").isNull()) {
            List<String> listOfWorlds = Config.get(plugin, "options.menu-world-restriction").getStringList();
            if (!listOfWorlds.contains(player.getLocation().getWorld().getName())) {
                player.sendMessage(Config.get(plugin, "messages.menu-world-restriction").getColoredString());
                return true;
            }
        }

        MenuController.open(player, new SkillsMenu(plugin, player, sPlayer));
        return true;
    }

}
