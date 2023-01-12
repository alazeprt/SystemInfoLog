package org.alazeprt.event;

import org.alazeprt.SystemInfoLog;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class loginfo implements Listener {
    private final SystemInfoLog plugin;
    public loginfo(SystemInfoLog plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onPluginLoad(PluginEnableEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {

            }
        }.runTaskTimer(this.plugin, 600,600);
    }
}
