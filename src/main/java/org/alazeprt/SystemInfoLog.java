package org.alazeprt;

import org.alazeprt.command.sysinfocommand;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Objects;

public class SystemInfoLog extends JavaPlugin {
    @Override
    public void onEnable(){
        File message = new File("message.yml");
        if(!message.exists()){
            this.saveResource("message.yml",false);
        }
        System.out.println("§a------------");
        System.out.println("§cSystemInfoLog §bv1.0 §dBeta Version");
        System.out.println("§eRunning in Bukkit - " + Bukkit.getServer().getName());
        System.out.println("§a------------");
        Objects.requireNonNull(getCommand("sysinfo")).setExecutor(new sysinfocommand());
        new loginfo(this);
        saveDefaultConfig();
    }

    @Override
    public void onDisable(){

    }

    class loginfo implements Listener {
        private final SystemInfoLog plugin;
        public loginfo(SystemInfoLog plugin) {
            this.plugin = plugin;
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
        @EventHandler
        public void onPluginLoad(ServerLoadEvent event) {
            new BukkitRunnable() {
                @Override
                public void run() {

                }
            }.runTaskTimer(this.plugin, 600,600);
        }
    }
}
