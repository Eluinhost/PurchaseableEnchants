package gg.uhc.purchaseableenchants;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import gg.uhc.purchaseableenchants.commands.OpenInventoryCommand;
import gg.uhc.purchaseableenchants.configuration.*;
import gg.uhc.purchaseableenchants.configuration.parsers.EnchantmentParser;
import gg.uhc.purchaseableenchants.configuration.parsers.MaterialParser;
import gg.uhc.purchaseableenchants.listeners.CraftingListener;
import gg.uhc.purchaseableenchants.listeners.InteractListener;
import gg.uhc.purchaseableenchants.listeners.InventoryOpenListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class Entry extends JavaPlugin {

    @Override
    public void onEnable() {
        // save the default if none exists already
        if(!copyDefaultConfig()) {
            // something went wrong with config writing
            setEnabled(false);
            return;
        }

        FileConfiguration configuration = getConfig();
        configuration.options().copyDefaults(true);
        saveConfig();

        // readers for materials
        MaterialParser materialParser = new MaterialParser();
        MaterialReader materialReader = new MaterialReader(materialParser);
        MaterialListReader materialListReader = new MaterialListReader(materialParser);

        // integer reader
        PositiveIntegerReader positiveIntegerReader = new PositiveIntegerReader(false);

        // boolean reader
        BooleanReader booleanReader = new BooleanReader();

        // item stacks
        ItemStackReader itemStackReader = new ItemStackReader(materialReader, positiveIntegerReader);
        ItemStackMapReader costMapRader = new ItemStackMapReader(itemStackReader);

        try {
            // read costs to use in tier cost reader
            Map<String, ItemStack> costsMap = costMapRader.readFromSection(configuration, "costs");

            // cost tiers
            EnchantmentTierCostReader tierCostReader = new EnchantmentTierCostReader(costsMap);

            // enchantment types
            EnchantmentTypeReader typeReader = new EnchantmentTypeReader(new EnchantmentParser(), tierCostReader);
            EnchantmentTypeListReader offersReader = new EnchantmentTypeListReader(typeReader);

            // trigger/disable items
            List<Material> triggerItems = materialListReader.readFromSection(configuration, "tool items");
            List<Material> disableItems = materialListReader.readFromSection(configuration, "disable items");

            boolean removeEnchantsOnRepair = booleanReader.readFromSection(configuration, "remove enchants on repair");
            boolean disableAnvil = booleanReader.readFromSection(configuration, "disable anvil use");
            boolean disableTable = booleanReader.readFromSection(configuration, "disable enchanting table use");

            // create the manager
            ItemUpgradesManager itemUpgradesManager = new ItemUpgradesManager(offersReader.readFromSection(configuration, "enchantments"));

            // register events
            PluginManager manager = Bukkit.getPluginManager();
            manager.registerEvents(itemUpgradesManager, this);
            manager.registerEvents(new CraftingListener(disableItems, removeEnchantsOnRepair), this);
            manager.registerEvents(new InteractListener(itemUpgradesManager, triggerItems), this);
            manager.registerEvents(new InventoryOpenListener(disableAnvil, disableTable), this);

            // debug command
            getCommand("enchcraft").setExecutor(new OpenInventoryCommand(itemUpgradesManager));
        } catch (InvalidConfigurationException e) {
            // invalid configuration file, print stack and disable plugin
            e.printStackTrace();
            setEnabled(false);
        }
    }

    /**
     * @return true if already exists/wrote to file, false if writing to file failed
     */
    protected boolean copyDefaultConfig() {
        File dataFolder = getDataFolder();

        // make data folder if it isn't there
        if (!dataFolder.exists() && !dataFolder.mkdir()) {
            // data folder creation failed
            return false;
        }

        File configFile = new File(getDataFolder(), "config.yml");

        // config file already exists
        if (configFile.exists()) return true;

        // write the defaults
        URL defaultConfig = Resources.getResource(this.getClass(), "/default.yml");
        try {
            // write /default.yml to the config.yml file
            Files.write(Resources.toByteArray(defaultConfig), configFile);
        } catch (IOException e) {
            e.printStackTrace();
            // something failed during writing
            return false;
        }

        // config wrote successfully
        return true;
    }
}
