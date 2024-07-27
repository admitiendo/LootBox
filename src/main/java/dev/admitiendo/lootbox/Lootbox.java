package dev.admitiendo.lootbox;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import dev.admitiendo.lootbox.commands.LootboxCommand;
import dev.admitiendo.lootbox.listeners.LootboxListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Lootbox extends JavaPlugin {
    @Getter public static Lootbox instance;

    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveConfig();
        
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Lootbox] " + ChatColor.GREEN + " has been successfully enabled.");

        this.getCommand("lootbox").setExecutor(new LootboxCommand());
        this.getServer().getPluginManager().registerEvents(new LootboxListener(), this);
    }
}
