package com.leonardobishop.playerskills2.fundingsource;

import com.leonardobishop.playerskills2.player.SPlayer;
import org.bukkit.entity.Player;

public class XPFundingSource implements FundingSource {

    @Override
    public String appendSymbol(String price) {
        return price + " XP";
    }

    @Override
    public boolean doTransaction(SPlayer sPlayer, int price, Player player) {
        if (player.getLevel() >= price) {
            player.setLevel(player.getLevel() - price);
            return true;
        }
        return false;
    }


}
