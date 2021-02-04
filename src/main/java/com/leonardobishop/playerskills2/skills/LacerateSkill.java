package com.leonardobishop.playerskills2.skills;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.utils.Config;
import com.leonardobishop.playerskills2.utils.ConfigType;
import com.leonardobishop.playerskills2.utils.CreatorConfigValue;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class LacerateSkill extends Skill {

    private HashMap<LivingEntity, BukkitTask> cutEntities = new HashMap<>();

    public LacerateSkill(PlayerSkills plugin) {
        super(plugin, "Lacerate", "lacerate");

        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.INTEGER, "max-level",
                4, "The maximum level the player can attain.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.INTEGER, "gui-slot",
                23, "The slot in the GUI where the skill will be put in.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.DOUBLE, "percent-increase",
                4, "Percentage increase in chance to cut an enemy.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.INTEGER, "bleed-cycles",
                8, "Amount of times a player will take damage.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.INTEGER, "bleed-interval",
                50, "Interval, in ticks, of each cycle.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.INTEGER, "bleed-damage",
                2, "The damage each bleed cycle will do.", true));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.BOOLEAN, "apply-to-non-players",
                false, "Whether or not this skill will cause non players to bleed.", false));
        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.LIST, "only-in-worlds",
                Arrays.asList("world", "world_nether", "world_the_end"), "Permitted in certain worlds.", false));
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || (!(event.getEntity() instanceof Player)
                && (!(super.getConfig().containsKey("apply-to-non-players")) || !((boolean) super.getConfig().get("apply-to-non-players"))))) {
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

        int lacerateLevel = sPlayer.getLevel(this.getConfigName());

        double chance = lacerateLevel * super.getDecimalNumber("percent-increase");

        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            LivingEntity victim = (LivingEntity) event.getEntity();

            bleed(victim);

            if (!Config.get(super.getPlugin(), "messages.bleeding-enemy").getColoredString().equals("")) {
                player.sendMessage(Config.get(super.getPlugin(), "messages.bleeding-enemy").getColoredString());
            }
            if (!Config.get(super.getPlugin(), "messages.bleeding-self").getColoredString().equals("")) {
                victim.sendMessage(Config.get(super.getPlugin(), "messages.bleeding-self").getColoredString());
            }
        }
    }

    private void bleed(LivingEntity player) {
        if (cutEntities.containsKey(player)) {
            return;
        }
        BukkitTask bt = new BukkitRunnable() {
            int times = 0;
            @Override
            public void run() {
                player.damage((int) LacerateSkill.super.getConfig().get("bleed-damage"), null);
                times++;
                if (times >= (int) LacerateSkill.super.getConfig().get("bleed-cycles")) {
                    LacerateSkill.this.cutEntities.remove(player);
                    try {
                        this.cancel();
                    } catch (Throwable ignored) {
                        // cancelled check throws error in 1.8
                    }
                }
            }
        }.runTaskTimer(super.getPlugin(), (int) super.getConfig().get("bleed-interval"), (int) super.getConfig().get("bleed-interval"));
        cutEntities.put(player, bt);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (cutEntities.containsKey(event.getEntity())) {
            BukkitTask bt = cutEntities.get(event.getEntity());
            try {
                bt.cancel();
            } catch (Throwable ignored) {
                // cancelled check throws error in 1.8
            }
            cutEntities.remove(event.getEntity());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (cutEntities.containsKey(event.getPlayer())) {
            BukkitTask bt = cutEntities.get(event.getPlayer());
            try {
                bt.cancel();
            } catch (Throwable ignored) {
                // cancelled check throws error in 1.8
            }
            cutEntities.remove(event.getPlayer());
        }
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int lacerateLevel = player.getLevel(this.getConfigName());
        double damage = lacerateLevel * super.getDecimalNumber("percent-increase");
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }

    @Override
    public String getNextString(SPlayer player) {
        int lacerateLevel = player.getLevel(this.getConfigName()) + 1;
        double damage = lacerateLevel * super.getDecimalNumber("percent-increase");
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }
}
