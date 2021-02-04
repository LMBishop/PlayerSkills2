package com.leonardobishop.playerskills2.skills;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.utils.ConfigType;
import com.leonardobishop.playerskills2.utils.CreatorConfigValue;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GluttonySkill extends Skill {

    public GluttonySkill(PlayerSkills plugin) {
        super(plugin, "Gluttony", "gluttony");

        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.INTEGER, "max-level",
                4, "The maximum level the player can attain.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.INTEGER, "gui-slot",
                21, "The slot in the GUI where the skill will be put in.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.DOUBLE, "percent-increase",
                50, "Percentage increase in food intake per level.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.LIST, "only-in-worlds",
                Arrays.asList("world", "world_nether", "world_the_end"), "Permitted in certain worlds.", false));
    }

    @EventHandler(ignoreCancelled = true)
    public void onFood(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (this.getConfig().containsKey("only-in-worlds")) {
            List<String> listOfWorlds = (List<String>) this.getConfig().get("only-in-worlds");
            if (!listOfWorlds.contains(player.getLocation().getWorld().getName())) {
                return;
            }
        }

        if (player.getFoodLevel() >= event.getFoodLevel()) {
            return;
        }

        event.setCancelled(true);
        SPlayer sPlayer = SPlayer.get(player.getUniqueId());

        if (sPlayer == null) {
            if (super.getPlugin().isVerboseLogging()) {
                super.getPlugin().logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
            }
            return;
        }

        int diff = event.getFoodLevel() - player.getFoodLevel();
        int gluttonyLevel = sPlayer.getLevel(this.getConfigName());
        // java.lang.Integer cannot be cast to java.lang.Double
        // my fucking ass
        double multiplier = 1D + (((double) gluttonyLevel) * (super.getDecimalNumber("percent-increase") / 100D));

        double newLevel = diff * multiplier;
        player.setFoodLevel(player.getFoodLevel() + (int) newLevel);
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int gluttonyLevel = player.getLevel(this.getConfigName());
        double heal = 100 + (gluttonyLevel * super.getDecimalNumber("percent-increase"));
        return getPlugin().getPercentageFormat().format(heal) + "%";
    }

    @Override
    public String getNextString(SPlayer player) {
        int gluttonyLevel = player.getLevel(this.getConfigName()) + 1;
        double heal = 100 + (gluttonyLevel * super.getDecimalNumber("percent-increase"));
        return getPlugin().getPercentageFormat().format(heal) + "%";
    }
}
