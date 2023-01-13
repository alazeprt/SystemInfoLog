package org.alazeprt.command;

import org.alazeprt.SystemInfoLog;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import cn.hutool.system.oshi.OshiUtil;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

public class sysinfocommand implements CommandExecutor {
    Yaml yaml = new Yaml();
    private double[] tps;

    private String readMessage() {
        String encoding = "UTF-8";
        File file = new File(org.alazeprt.SystemInfoLog.getPlugin(org.alazeprt.SystemInfoLog.class).getDataFolder(), "message.yml");
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private String readConfig() {
        String encoding = "UTF-8";
        File file = new File(org.alazeprt.SystemInfoLog.getPlugin(org.alazeprt.SystemInfoLog.class).getDataFolder(), "config.yml");
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    private void sendsysinfo(@NotNull CommandSender sender, @NotNull String key_class, @NotNull String content, @Nullable String score, boolean hasnum){
        Map map = (Map) yaml.load(readMessage());
        String prefix = map.get("prefix").toString().replace("&","§");
        Object command = map.get("command");
        Map commands = (Map) command;
        Object keyall = commands.get(key_class);
        Map keyallm = (Map) keyall;
        String value = keyallm.get(content).toString().replace("&","§");
        prefix = prefix + " ";
        if(hasnum && !(score == null)){
            value = value.replace("[num]",String.valueOf(score));
            sender.sendMessage(prefix + value);
        } else if(hasnum && (score == null)){
            sendsysinfo(sender, "command", "development_error", null, false);
        } else if(!hasnum && !(score == null)){
            sender.sendMessage(prefix + value + score);
        } else if(!hasnum && (score == null)){
            sender.sendMessage(prefix + value);
        }
    }
    private void sendhelp(@NotNull CommandSender sender) {
        sendsysinfo(sender, "help", "title", null, false);
        sendsysinfo(sender, "help", "tps", null, false);
        sendsysinfo(sender, "help", "cpu", null, false);
        sendsysinfo(sender, "help", "memory", null, false);
        sendsysinfo(sender, "help", "disk", null, false);
        sendsysinfo(sender, "help", "network", null, false);
    }

    private void sendgetdata(@NotNull CommandSender sender){
        Map map = (Map) yaml.load(readMessage());;
        String prefix = map.get("prefix").toString();
        Object command = map.get("command");
        Map commands = (Map) command;
        sender.sendMessage(commands.get("get_data").toString().replace("&","§"));
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // Threads
        class NetworkThread extends Thread{
            @Override
            public void run() {
                Map map = (Map)yaml.load(readConfig());;
                Object command = map.get("command");
                Map map2 = (Map)command;
                Object networko = map2.get("network");
                Map network = (Map)networko;
                for(int i = 0;i <= OshiUtil.getNetworkIFs().size() - 1; i++){
                    int j = i + 1;
                    sendsysinfo(sender, "network", "serial", String.valueOf(j), true);
                    if(network.get("name").toString().equals("true")){
                        sendsysinfo(sender, "network",  "name", OshiUtil.getNetworkIFs().get(i).getName(), false);
                    }
                    if(network.get("display_name").toString().equals("true")){
                        sendsysinfo(sender, "network", "display", OshiUtil.getNetworkIFs().get(i).getDisplayName(), false);
                    }
                    if(network.get("mac").toString().equals("true")){
                        sendsysinfo(sender, "network", "mac", OshiUtil.getNetworkIFs().get(i).getMacaddr(),false);
                    }
                    sendsysinfo(sender, "network", "ipv4", Arrays.toString(OshiUtil.getNetworkIFs().get(i).getIPv4addr()), false);
                    if(network.get("ipv6").toString().equals("true")){
                        sendsysinfo(sender, "network", "ipv6", Arrays.toString(OshiUtil.getNetworkIFs().get(i).getIPv6addr()), false);
                    }
                    if(network.get("recv_size").toString().equals("true")){
                        sendsysinfo(sender, "network", "recv_size", String.valueOf(OshiUtil.getNetworkIFs().get(i).getBytesRecv()), false);
                    }
                    if(network.get("recv_num").toString().equals("true")) {
                        sendsysinfo(sender, "network", "recv_packets",
                                String.valueOf(OshiUtil.getNetworkIFs().get(i).getPacketsRecv()), false);
                    }
                    if(network.get("sent_size").toString().equals("true")){
                        sendsysinfo(sender, "network", "sent_size", String.valueOf(OshiUtil.getNetworkIFs().get(i).getBytesSent()), false);
                    }
                    if(network.get("sent_num").toString().equals("true")){
                        sendsysinfo(sender, "network", "sent_packets",
                                String.valueOf(OshiUtil.getNetworkIFs().get(i).getPacketsSent()), false);
                    }
                }
            }
        }
        class CPUThread extends Thread{
            @Override
            public void run() {
                sendsysinfo(sender, "cpu", "name", OshiUtil.getCpuInfo().getCpuModel().split("\n")[0], false);
                sendsysinfo(sender, "cpu", "num", OshiUtil.getCpuInfo().getCpuNum().toString(), false);
            }
        }
        class TPSThread extends Thread{
            @Override
            public void run() {
                Server server = Bukkit.getServer();
                try{
                    Field consoleField = server.getClass().getDeclaredField("console");
                    consoleField.setAccessible(true);
                    Object minecraftServer = consoleField.get(server);
                    Field recentTps = minecraftServer.getClass().getSuperclass().getDeclaredField("recentTps");
                    recentTps.setAccessible(true);
                    tps = (double[]) recentTps.get(minecraftServer);
                } catch (NoSuchFieldException | IllegalAccessException fe){
                    sendsysinfo(sender, "tps", "error", null, false);
                    sender.sendMessage(fe.getMessage());
                } finally {
                    ArrayList<String> newtps = new ArrayList<>();
                    for (double i : tps) {
                        String j;
                        if (i >= 20) {
                            j = "§a20.00";
                        } else {
                            if (i >= 18) {
                                j = "§a" + String.format("%.2f", i);
                            } else if (i >= 16) {
                                j = "§e" + String.format("%.2f", i);
                            } else {
                                j = "§c" + String.format("%.2f", i);
                            }
                        }
                        newtps.add(j);
                    }
                    Map map = (Map) yaml.load(readMessage());;
                    String prefix = map.get("prefix").toString().replace("&","§");
                    int size = newtps.size();
                    if(size == 3){
                        sendsysinfo(sender, "tps", "tps_size_3", null, false);
                        sender.sendMessage(prefix + " " + newtps.get(0) + ", " + newtps.get(1) + ", " + newtps.get(2));
                    } else if(size == 4){
                        sendsysinfo(sender, "tps", "tps_size_4", null, false);
                        sender.sendMessage(prefix + " " + newtps.get(0) + ", " + newtps.get(1) + ", " + newtps.get(2) + ", " + newtps.get(3));
                    } else{
                        sendsysinfo(sender, "tps", "tps_size_other", null, false);
                        for(int i=0;i<size;i++){
                            sender.sendMessage(newtps.get(i) + ", ");
                        }
                    }
                }
            }
        }
        class diskThread extends Thread{
            @Override
            public void run() {
                Map map = (Map)yaml.load(readConfig());;
                Object command = map.get("command");
                Map map2 = (Map)command;
                Object disko = map2.get("disk");
                Map disk = (Map)disko;
                for (int i = 0; i <= OshiUtil.getDiskStores().size() - 1; i++) {
                    sendsysinfo(sender, "disk", "serial", String.valueOf(i), true);
                    if(disk.get("diskname").toString().equals("true")){
                        sender.sendMessage("§e[§cSystemInfoLog§e] §a" + OshiUtil.getDiskStores().get(i).getModel().replace(" (标准磁盘驱动器)", "") + ": ");
                    }
                    long size = OshiUtil.getDiskStores().get(i).getSize();
                    String formatsize;
                    if (size >= 1000 && size < 1000000) {
                        String content = String.format("%.2f", size / 1000.0);
                        formatsize = content + "KB";
                    } else if (size >= 1000000 && size < 1000000000) {
                        String content = String.format("%.2f", size / 1000000.0);
                        formatsize = content + "MB";
                    } else if (size >= 1000000000 && size < 1e+012) {
                        String content = String.format("%.2f", size / 1000000000.0);
                        formatsize = content + "GB";
                    } else if (size >= 1e+012) {
                        String content = String.format("%.2f", size / 1e+012);
                        formatsize = content + "TB";
                    } else {
                        formatsize = size + "B";
                    }
                    sendsysinfo(sender, "disk", "size", formatsize, false);
                    String formatread;
                    String formatwrite;
                    long read = OshiUtil.getDiskStores().get(i).getReadBytes();
                    long write = OshiUtil.getDiskStores().get(i).getWriteBytes();
                    if(disk.get("readspeed").toString().equals("true")){
                        if (read >= 1000 && read < 1000000) {
                            String content = String.format("%.2f", read / 1000.0);
                            formatread = content + "KB";
                        } else if (read >= 1000000 && read < 1000000000) {
                            String content = String.format("%.2f", read / 1000000.0);
                            formatread = content + "MB";
                        } else if (read >= 1000000000 && read < 1e+012) {
                            String content = String.format("%.2f", read / 1000000000.0);
                            formatread = content + "GB";
                        } else if (read >= 1e+012) {
                            String content = String.format("%.2f", read / 1e+012);
                            formatread = content + "TB";
                        } else {
                            formatread = read + "B";
                        }
                        sendsysinfo(sender, "disk", "read", formatread, false);
                    }
                    if(disk.get("writespeed").toString().equals("true")){
                        if (write >= 1000 && write < 1000000) {
                            String content = String.format("%.2f", write / 1000.0);
                            formatwrite = content + "KB";
                        } else if (write >= 1000000 && write < 1000000000) {
                            String content = String.format("%.2f", write / 1000000.0);
                            formatwrite = content + "MB";
                        } else if (write >= 1000000000 && write < 1e+012) {
                            String content = String.format("%.2f", write / 1000000000.0);
                            formatwrite = content + "GB";
                        } else if (write >= 1e+012) {
                            String content = String.format("%.2f", write / 1e+012);
                            formatwrite = content + "TB";
                        } else {
                            formatwrite = write + "B";
                        }
                        sendsysinfo(sender, "disk", "write", formatwrite, false);
                    }
                }
            }
        }
        // Commands
        int length = strings.length;
        if(length == 0){
            sendhelp(sender);
        } else if(length == 1){
            if(strings[0].equals("tps")){
                TPSThread tps = new TPSThread();
                tps.start();
            }
            else if(strings[0].equals("cpu")){
                sendsysinfo(sender, strings[0], "prompt", null, false);
                sendgetdata(sender);
                CPUThread cpu = new CPUThread();
                cpu.start();
            } else if(strings[0].equals("memory")){
                sendsysinfo(sender, strings[0], "prompt", null, false);
                sendgetdata(sender);
                sendsysinfo(sender, strings[0], "usage",
                        OshiUtil.getMemory().toString().replace("Available: ","").replace("GiB","GB"), false);
            } else if(strings[0].equals("disk")) {
                sendsysinfo(sender, strings[0], "prompt", null, false);
                sendgetdata(sender);
                diskThread disk = new diskThread();
                disk.start();
            } else if(strings[0].equals("network")){
                Thread network = new Thread();
                sendsysinfo(sender, strings[0], "prompt", null, false);
                sendgetdata(sender);
                NetworkThread nw = new NetworkThread();
                nw.start();
            } else if(strings[0].equals("reload")){
                sendsysinfo(sender, strings[0], "prompt", null, false);
                SystemInfoLog.getPlugin(SystemInfoLog.class).reloadConfig();
            } else{
                sendhelp(sender);
            }
        } else{
            sendhelp(sender);
        }
        return false;
    }
}
