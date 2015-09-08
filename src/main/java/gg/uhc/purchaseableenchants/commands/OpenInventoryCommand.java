package gg.uhc.purchaseableenchants.commands;

import gg.uhc.purchaseableenchants.ItemUpgradesManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenInventoryCommand implements CommandExecutor {

    public static final String PERMISSION = "uhc.deterministiccrafting.command";

    protected final ItemUpgradesManager manager;

    /**
     * Debug command that directy opens the tool for the player that ran it
     *
     * @param manager the upgrade manager
     */
    public OpenInventoryCommand(ItemUpgradesManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be ran as a player");
            return true;
        }

        manager.showUpgrades((Player) sender);

        return true;
    }
}
