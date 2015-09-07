package gg.uhc.purchaseableenchants;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import gg.uhc.purchaseableenchants.commands.OpenInventoryCommand;
import gg.uhc.purchaseableenchants.configuration.*;
import gg.uhc.purchaseableenchants.configuration.parsers.EnchantmentParser;
import gg.uhc.purchaseableenchants.configuration.parsers.MaterialParser;
import gg.uhc.purchaseableenchants.listeners.CraftingListener;
import gg.uhc.purchaseableenchants.listeners.InteractListener;
import gg.uhc.purchaseableenchants.offers.EnchantmentOffers;
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
            setEnabled(false);
            return;
        }

        FileConfiguration configuration = getConfig();
        configuration.options().copyDefaults(true);
        saveConfig();

        MaterialParser materialParser = new MaterialParser();
        MaterialReader materialReader = new MaterialReader(materialParser);
        MaterialListReader materialListReader = new MaterialListReader(materialParser);
        PositiveIntegerReader positiveIntegerReader = new PositiveIntegerReader(false);
        ItemStackReader itemStackReader = new ItemStackReader(materialReader, positiveIntegerReader);
        ItemStackMapReader costMapRader = new ItemStackMapReader(itemStackReader);
        try {
            // read costs to use in tier cost reader
            Map<String, ItemStack> costsMap = costMapRader.readFromSection(configuration, "costs");

            EnchantmentTierCostReader tierCostReader = new EnchantmentTierCostReader(costsMap);
            EnchantmentTypeReader typeReader = new EnchantmentTypeReader(new EnchantmentParser(), tierCostReader);
            EnchantmentOffersReader offersReader = new EnchantmentOffersReader(typeReader);

            // offers + manager
            EnchantmentOffers offers = offersReader.readFromSection(configuration, "enchantments");
            ItemUpgradesManager itemUpgradesManager = new ItemUpgradesManager(offers);

            // trigger/disable items
            List<Material> triggerItems = materialListReader.readFromSection(configuration, "tool items");
            List<Material> disableItems = materialListReader.readFromSection(configuration, "disable items");

            // register events
            PluginManager manager = Bukkit.getPluginManager();
            manager.registerEvents(new CraftingListener(disableItems), this);
            manager.registerEvents(new InteractListener(itemUpgradesManager, triggerItems), this);
            manager.registerEvents(itemUpgradesManager, this);

            // debug command
            getCommand("enchcraft").setExecutor(new OpenInventoryCommand(itemUpgradesManager));
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            setEnabled(false);
        }
    }

    /**
     * @return true if already exists/wrote to file, false if writing to file failed
     */
    protected boolean copyDefaultConfig() {
        File dataFolder = getDataFolder();

        if (!dataFolder.exists() && !dataFolder.mkdir()) {
            return false;
        }

        File configFile = new File(getDataFolder(), "config.yml");

        if (configFile.exists()) return true;

        // write the defaults
        URL defaultConfig = Resources.getResource(this.getClass(), "/default.yml");
        try {
            Files.write(Resources.toByteArray(defaultConfig), configFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
