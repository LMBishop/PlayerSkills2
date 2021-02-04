package com.leonardobishop.playerskills2.skills;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.utils.Config;
import com.leonardobishop.playerskills2.utils.ConfigType;
import com.leonardobishop.playerskills2.utils.CreatorConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HealthSkill extends Skill {

    private HashMap<UUID, Integer> hitpointsFromPlayerskills = new HashMap<>();
    private HashMap<UUID, Double> knownMaxHitpoints = new HashMap<>();
    private HashMap<UUID, Double> hitpoints = new HashMap<>();

    public HealthSkill(PlayerSkills plugin) {
        super(plugin, "Health", "health");

        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.INTEGER, "max-level",
                5, "The maximum level the player can attain.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.INTEGER, "gui-slot",
                22, "The slot in the GUI where the skill will be put in.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.INTEGER, "extra-health-per-level",
                1, "Extra hearts per level.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.BOOLEAN, "compatibility-mode",
                false, "Attempts to work alongside other plugins that change max-health.", false));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.LIST, "only-in-worlds",
                Arrays.asList("world", "world_nether", "world_the_end"), "Permitted in certain worlds.", false));
    }

    @Override
    public void enable(PlayerSkills plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    SPlayer sPlayer = SPlayer.get(player.getUniqueId());
                    if (sPlayer == null) {
                        if (HealthSkill.super.getPlugin().isVerboseLogging()) {
                            HealthSkill.super.getPlugin().logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
                        }
                        continue;
                    }
                    if (HealthSkill.this.getConfig().containsKey("only-in-worlds")) {
                        List<String> listOfWorlds = (List<String>) HealthSkill.this.getConfig().get("only-in-worlds");
                        if (!listOfWorlds.contains(player.getLocation().getWorld().getName())) {
                            return;
                        }
                    }

                    int hpNeeded = (sPlayer.getLevel(HealthSkill.super.getConfigName()) * (((int) HealthSkill.super.getConfig().get
                            ("extra-health-per-level")) * 2));
                    if (HealthSkill.super.getConfig().containsKey("compatibility-mode") &&
                            (boolean) HealthSkill.super.getConfig().get("compatibility-mode")) {
                        int hpApplied;
                        if (!knownMaxHitpoints.containsKey(player.getUniqueId()) ||
                                knownMaxHitpoints.get(player.getUniqueId()) != player.getMaxHealth()) {
                            // maxhitpoints has changed from when playerskills altered it, assume health from playerskills
                            // is 0
                            hpApplied = 0;
                        } else {
                            hpApplied = hitpointsFromPlayerskills.getOrDefault(player.getUniqueId(), 0);
                        }

                        int hpDiff;
                        if (hpApplied != hpNeeded) {
                            hpDiff = hpNeeded - hpApplied;

                            double newMaxHealth = player.getMaxHealth() + hpDiff;
                            player.setMaxHealth(newMaxHealth);
                            knownMaxHitpoints.put(player.getUniqueId(), newMaxHealth);
                            hitpointsFromPlayerskills.put(player.getUniqueId(), hpNeeded);


                            if (hitpoints.containsKey(player.getUniqueId())) {
                                double health = hitpoints.get(player.getUniqueId());
                                if (health > player.getMaxHealth()) {
                                    player.setHealth(player.getMaxHealth());
                                } else {
                                    player.setHealth(health);
                                }
                            }
                        }

                        hitpoints.put(player.getUniqueId(), player.getHealth());
                    } else {
                        player.setMaxHealth(20 + hpNeeded);
                    }

                }
            }
        }.runTaskTimer(plugin, (HealthSkill.super.getConfig().containsKey("compatibility-mode") &&
                (boolean) HealthSkill.super.getConfig().get("compatibility-mode") ? 1L : 20L),
                (HealthSkill.super.getConfig().containsKey("compatibility-mode") && (boolean) HealthSkill.super.getConfig().get("compatibility-mode") ? 1L : 20L));
    }

    @EventHandler
    public void onWorldSwitch(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        SPlayer sPlayer = SPlayer.get(player.getUniqueId());

        if (HealthSkill.this.getConfig().containsKey("only-in-worlds")) {
            String to = player.getLocation().getWorld().getName();
            List<String> listOfWorlds = (List<String>) HealthSkill.this.getConfig().get("only-in-worlds");
            if (!listOfWorlds.contains(to)) {
                if (HealthSkill.super.getConfig().containsKey("compatibility-mode") &&
                        (boolean) HealthSkill.super.getConfig().get("compatibility-mode")) {
                    int hpApplied;
                    if (!knownMaxHitpoints.containsKey(player.getUniqueId()) ||
                            knownMaxHitpoints.get(player.getUniqueId()) != player.getMaxHealth()) {
                        // maxhitpoints has changed from when playerskills altered it, assume health from playerskills
                        // is 0
                        return;
                    } else {
                        hpApplied = hitpointsFromPlayerskills.getOrDefault(player.getUniqueId(), 0);
                    }

                    int hpNeeded = (sPlayer.getLevel(HealthSkill.super.getConfigName()) * (((int) HealthSkill.super.getConfig().get
                            ("extra-health-per-level")) * 2));

                    if (hpApplied == hpNeeded) {
                        player.setMaxHealth(player.getMaxHealth() - hpNeeded);
                    }

                    hitpoints.put(player.getUniqueId(), player.getHealth());
                } else {
                    player.setMaxHealth(20);
                }
            }
        }
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int healthLevel = player.getLevel(this.getConfigName());
        int hp = healthLevel * ((int) HealthSkill.super.getConfig().get("extra-health-per-level"));
        return hp + (hp == 1 ? " heart" : " hearts");
    }

    @Override
    public String getNextString(SPlayer player) {
        int healthLevel = player.getLevel(this.getConfigName()) + 1;
        int hp = healthLevel * ((int) HealthSkill.super.getConfig().get("extra-health-per-level"));
        return hp + (hp == 1 ? " heart" : " hearts");
    }
}
