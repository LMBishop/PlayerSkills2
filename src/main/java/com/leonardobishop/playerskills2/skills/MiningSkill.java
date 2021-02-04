//package com.leonardobishop.playerskills2.skills;
//
//import com.comphenix.protocol.PacketType;
//import com.comphenix.protocol.events.ListenerPriority;
//import com.comphenix.protocol.events.PacketAdapter;
//import com.comphenix.protocol.events.PacketContainer;
//import com.comphenix.protocol.events.PacketEvent;
//import com.comphenix.protocol.wrappers.EnumWrappers;
//import com.leonardobishop.playerskills2.PlayerSkills;
//import com.leonardobishop.playerskills2.player.SPlayer;
//import com.leonardobishop.playerskills2.utils.ConfigType;
//import com.leonardobishop.playerskills2.utils.CreatorConfigValue;
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.block.BlockDamageEvent;
//import org.bukkit.event.entity.EntityDamageEvent;
//import org.bukkit.event.player.PlayerAnimationEvent;
//
//public class MiningSkill extends Skill {
//
////    private HashMap<Player, >
//
//    public MiningSkill(PlayerSkills plugin) {
//        super(plugin, "Mining", "mining");
//
//        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.INTEGER, "max-level", 4, "The maximum level the player can attain.", true));
//        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.INTEGER, "gui-slot", 10, "The slot in the GUI where the skill will be put in.", true));
//        super.getCreatorConfigValues().add(new CreatorConfigValue(ConfigType.DOUBLE, "percent-increase", 10, "Percentage increase in mining speed.", true));
//
//        interceptPackets();
//    }
//
//    private void interceptPackets() {
//        super.getPlugin().protocolManager.addPacketListener(new PacketAdapter(super.getPlugin(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
//            @Override
//            public void onPacketReceiving(PacketEvent event){
//                PacketContainer packet = event.getPacket();
//                EnumWrappers.PlayerDigType digType = packet.getPlayerDigTypes().getValues().get(0);
//                Bukkit.broadcastMessage("DigType: "+digType.name());
//            }
//        });
//    }
//
//    @EventHandler(ignoreCancelled = true)
//    public void onHit(BlockDamageEvent event) {
//        Bukkit.broadcastMessage("damage");
////        event.getBlock().getType().getData()
////        if (!(event.getEntity() instanceof Player)) {
////            return;
////        }
////
////        Player player = (Player) event.getEntity();
////        SPlayer sPlayer = SPlayer.get(player.getUniqueId());
////
////        if (sPlayer == null) {
////            if (super.getPlugin().isVerboseLogging()) {
////                super.getPlugin().logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
////            }
////            return;
////        }
////
////        int resistanceLevel = sPlayer.getLevel(this.getConfigName());
////
////        double percentile = event.getDamage() / 100;
////        percentile = percentile * super.getDecimalNumber("damage-drop");
////        double weightedDamage = resistanceLevel * percentile;
////        event.setDamage(event.getDamage() - weightedDamage);
//    }
////
//    public void onAnimate(PlayerAnimationEvent event) {
//        Bukkit.broadcastMessage("animate");
//    }
//
//    @Override
//    public String getPreviousString(SPlayer player) {
//        int miningLevel = player.getLevel(this.getConfigName());
//        double damage = 100 - (miningLevel * super.getDecimalNumber("percent-increase"));
//        return getPlugin().getPercentageFormat().format(damage) + "%";
//    }
//
//    @Override
//    public String getNextString(SPlayer player) {
//        int miningLevel = player.getLevel(this.getConfigName()) + 1;
//        double damage = 100 - (miningLevel * super.getDecimalNumber("percent-increase"));
//        return getPlugin().getPercentageFormat().format(damage) + "%";
//    }
//}
