package com.leonardobishop.playerskills2.skills;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.utils.Config;
import com.leonardobishop.playerskills2.utils.ConfigType;
import com.leonardobishop.playerskills2.utils.CreatorConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;
import java.util.List;

public class ResistanceSkill extends Skill {

    public ResistanceSkill(PlayerSkills plugin) {
        super(plugin, "Resistance", "resistance");

        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.INTEGER, "max-level",
                10, "The maximum level the player can attain.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.INTEGER, "gui-slot",
                12, "The slot in the GUI where the skill will be put in.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.DOUBLE, "damage-drop",
                3, "Percentage increase in damage per level.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.LIST, "only-in-worlds",
                Arrays.asList("world", "world_nether", "world_the_end"), "Permitted in certain worlds.", false));
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
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

        int resistanceLevel = sPlayer.getLevel(this.getConfigName());

        double percentile = event.getDamage() / 100;
        percentile = percentile * super.getDecimalNumber("damage-drop");
        double weightedDamage = resistanceLevel * percentile;
        event.setDamage(event.getDamage() - weightedDamage);
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int resistanceLevel = player.getLevel(this.getConfigName());
        double damage = 100 - (resistanceLevel * super.getDecimalNumber("damage-drop"));
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }

    @Override
    public String getNextString(SPlayer player) {
        int resistanceLevel = player.getLevel(this.getConfigName()) + 1;
        double damage = 100 - (resistanceLevel * super.getDecimalNumber("damage-drop"));
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }
}
