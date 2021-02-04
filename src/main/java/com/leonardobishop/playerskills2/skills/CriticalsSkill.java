package com.leonardobishop.playerskills2.skills;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.utils.Config;
import com.leonardobishop.playerskills2.utils.ConfigType;
import com.leonardobishop.playerskills2.utils.CreatorConfigValue;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CriticalsSkill extends Skill {

    public CriticalsSkill(PlayerSkills plugin) {
        super(plugin, "Criticals", "criticals");

        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.INTEGER, "max-level",
                3, "The maximum level the player can attain.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.INTEGER, "gui-slot",
                14, "The slot in the GUI where the skill will be put in.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.DOUBLE, "percent-increase",
                4, "Percentage increase in damage per level.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.DOUBLE, "critical-multiplier",
                1.5, "Multiplier for a critical attack.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.LIST, "only-in-worlds",
                Arrays.asList("world", "world_nether", "world_the_end"), "Permitted in certain worlds.", false));
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        if (this.getConfig().containsKey("only-in-worlds")) {
            List<String> listOfWorlds = (List<String>) this.getConfig().get("only-in-worlds");
            if (!listOfWorlds.contains(player.getLocation().getWorld().getName())) {
                return;
            }
        }

        SPlayer sPlayer = SPlayer.get(player.getUniqueId());

        if (sPlayer == null) {
            if (super.getPlugin().isVerboseLogging()) {
                super.getPlugin().logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
            }
            return;
        }

        int criticalLevel = sPlayer.getLevel(this.getConfigName());

        double chance = criticalLevel * super.getDecimalNumber("percent-increase");

        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            if (!Config.get(super.getPlugin(), "messages.critical").getColoredString().equals("")) {
                player.sendMessage(Config.get(super.getPlugin(), "messages.critical").getColoredString());
            }
            event.setDamage(event.getDamage() * (double) super.getConfig().get("critical-multiplier"));
        }
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int criticalLevel = player.getLevel(this.getConfigName());
        double damage = criticalLevel * super.getDecimalNumber("percent-increase");
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }

    @Override
    public String getNextString(SPlayer player) {
        int criticalLevel = player.getLevel(this.getConfigName()) + 1;
        double damage = criticalLevel * super.getDecimalNumber("percent-increase");
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }
}
