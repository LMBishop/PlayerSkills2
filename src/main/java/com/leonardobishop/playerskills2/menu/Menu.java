package com.leonardobishop.playerskills2.menu;

import org.bukkit.inventory.Inventory;

public abstract class Menu {

    public abstract Inventory toInventory();
    public abstract void onClick(int slot);
    public abstract void onClose();

}
