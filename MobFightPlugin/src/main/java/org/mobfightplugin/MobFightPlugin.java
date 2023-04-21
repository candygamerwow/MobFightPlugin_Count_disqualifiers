package org.mobfightplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class MobFightPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register the event listener
        getServer().getPluginManager().registerEvents(new MobFightListener(this), this);
        // Plugin startup logic
        getLogger().info("MobFightPlugin has been enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("MobFightPlugin has been disabled");
    }
}
