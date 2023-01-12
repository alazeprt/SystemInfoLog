package org.alazeprt;

import org.alazeprt.command.sysinfocommand;
import org.alazeprt.event.loginfo;
import org.bukkit.plugin.java.JavaPlugin;

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
        System.out.println("§a------------");
        Objects.requireNonNull(getCommand("sysinfo")).setExecutor(new sysinfocommand());
        getServer().getPluginManager().registerEvents(new loginfo(this), this);
        saveDefaultConfig();
    }

    @Override
    public void onDisable(){

    }
}
