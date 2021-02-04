package com.leonardobishop.playerskills2.events;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    private PlayerSkills plugin;

    public JoinEvent(PlayerSkills plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        SPlayer.load(plugin, event.getPlayer().getUniqueId());
    }

}
