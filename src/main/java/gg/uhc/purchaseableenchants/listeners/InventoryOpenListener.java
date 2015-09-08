package gg.uhc.purchaseableenchants.listeners;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryOpenListener implements Listener {

    protected final boolean disableAnvil;
    protected final boolean disableTable;

    public InventoryOpenListener(boolean disableAnvil, boolean disableTable) {
        this.disableAnvil = disableAnvil;
        this.disableTable = disableTable;
    }


    @EventHandler
    public void on(InventoryOpenEvent event) {
        InventoryType type = event.getInventory().getType();

        if ((disableAnvil && type == InventoryType.ANVIL) || (disableTable && type == InventoryType.ENCHANTING)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "This block has been disabled");
        }
    }
}
