package com.leonardobishop.playerskills2.commands;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.menu.ConfigMenu;
import com.leonardobishop.playerskills2.menu.MenuController;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Set;

public class SkillsadminCommand implements CommandExecutor {

    private PlayerSkills plugin;

    public SkillsadminCommand(PlayerSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("playerskills.admin")) {
            return true;
        }
        if (args.length >= 4) {
            if (args[0].equalsIgnoreCase("setskill")) {
                SPlayer sPlayer;
                if ((sPlayer = getPlayer(sender, args[1])) == null) return true;
                String skillName = args[2];

                int level;
                try {
                    level = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Not a number.");
                    return true;
                }

                Skill s = null;
                for (Skill skill : plugin.getSkillRegistrar().values()) {
                    if (skill.getConfigName().equalsIgnoreCase(skillName)) {
                        s = skill;
                        break;
                    }
                }

                if (s == null) {
                    sender.sendMessage(ChatColor.RED + "Skill could not be found. Skill names used in configurations and commands should contain no spaces and" +
                            " should be all lower case.");
                    return true;
                }

                sPlayer.setLevel(s.getConfigName(), level);
                sender.sendMessage(ChatColor.GREEN + "Skill level for " + s.getName() + " updated to " + sPlayer.getLevel(s.getConfigName()) + ".");
            }
        } else if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("givepoints")) {
                SPlayer sPlayer;
                if ((sPlayer = getPlayer(sender, args[1])) == null) return true;

                int points;
                try {
                    points = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Not a number.");
                    return true;
                }

                sPlayer.setPoints(sPlayer.getPoints() + points);

                sender.sendMessage(ChatColor.GREEN + "Points updated to " + sPlayer.getPoints() + ".");
            }
        } else if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("view")) {
                SPlayer sPlayer;
                if ((sPlayer = getPlayer(sender, args[1])) == null) return true;

                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Skills");
                for (Skill skill : plugin.getSkillRegistrar().values()) {
                    sender.sendMessage(ChatColor.RED + skill.getName() + ": " + ChatColor.GRAY + sPlayer.getLevel(skill.getConfigName()));
                }
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Points: " + ChatColor.GRAY + sPlayer.getPoints());
            } else if (args[0].equalsIgnoreCase("fullreset")) {
                SPlayer sPlayer;
                if ((sPlayer = getPlayer(sender, args[1])) == null) return true;

                sPlayer.setPoints(0);
                Set<String> skills = sPlayer.getSkills().keySet();
                for (String skill : skills) {
                    sPlayer.setLevel(skill, 0);
                }
                sender.sendMessage(ChatColor.GREEN + "Skill data for (" + ChatColor.GRAY + sPlayer.getPlayer() + ChatColor.GREEN + ") " +
                        "has been reset.");
            }
        } else if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("editor") && sender instanceof Player) {
                if (MenuController.isMenuOpenElsewhere(ConfigMenu.class)) {
                    sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Warning! "
                            + ChatColor.RED + "Another user is already using the editor - this may cause unexpected behaviour!");
                }
                sender.sendMessage(ChatColor.GREEN + "Please do not edit the config.yml file while inside the editor, otherwise you may lose any changes.");
                ConfigMenu configMenu = new ConfigMenu(plugin, (Player) sender);
                MenuController.open((Player) sender, configMenu);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "/skillsadmin editor");
            sender.sendMessage(ChatColor.RED + "/skillsadmin view <player>");
            sender.sendMessage(ChatColor.RED + "/skillsadmin givepoints <player> [-]<points>");
            sender.sendMessage(ChatColor.RED + "/skillsadmin setskill <player> <skill name> <level>");
            sender.sendMessage(ChatColor.RED + "/skillsadmin fullreset <player>");
        }
        return true;
    }

    private SPlayer getPlayer(CommandSender sender, String arg) {
        Player player = Bukkit.getPlayer(arg);
        SPlayer sPlayer = null;
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
        } else {
            sPlayer = SPlayer.get(player.getUniqueId());
            if (sPlayer == null) {
                sender.sendMessage(ChatColor.RED + "ERROR: SPlayer could not be found.");
            }
        }
        return sPlayer;
    }

}
