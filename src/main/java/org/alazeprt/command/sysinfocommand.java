package org.alazeprt.command;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import cn.hutool.system.oshi.OshiUtil;

public class sysinfocommand implements CommandExecutor {
    private double[] tps;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        class NetworkThread extends Thread{
            @Override
            public void run() {
                for(int i = 0;i <= OshiUtil.getNetworkIFs().size() - 1; i++){
                    int j = i + 1;
                    sender.sendMessage("§e[§cSystemInfoLog§e] §a" + "Network " + j + ": ");
                    sender.sendMessage("§e[§cSystemInfoLog§e] §aNetwork name: " + OshiUtil.getNetworkIFs().get(i).getName());
                    sender.sendMessage("§e[§cSystemInfoLog§e] §aNetwork display name:" + OshiUtil.getNetworkIFs().get(i).getDisplayName());
                    sender.sendMessage("§e[§cSystemInfoLog§e] §aNetwork mac address: " + OshiUtil.getNetworkIFs().get(i).getMacaddr());
                    sender.sendMessage("§e[§cSystemInfoLog§e] §aNetwork ipv4 address: " + Arrays.toString(OshiUtil.getNetworkIFs().get(i).getIPv4addr()));
                    sender.sendMessage("§e[§cSystemInfoLog§e] §aNetwork ipv6 address: " + Arrays.toString(OshiUtil.getNetworkIFs().get(i).getIPv6addr()));
                    sender.sendMessage("§e[§cSystemInfoLog§e] §aNetwork ↓recv bytes: " + OshiUtil.getNetworkIFs().get(i).getBytesRecv());
                    sender.sendMessage("§e[§cSystemInfoLog§e] §aNetwork ↓recv packets: " + OshiUtil.getNetworkIFs().get(i).getPacketsRecv());
                    sender.sendMessage("§e[§cSystemInfoLog§e] §aNetwork ↑send bytes: " + OshiUtil.getNetworkIFs().get(i).getBytesSent());
                    sender.sendMessage("§e[§cSystemInfoLog§e] §aNetwork ↑send packets: " + OshiUtil.getNetworkIFs().get(i).getPacketsSent());
                }
            }
        }
        class CPUThread extends Thread{
            @Override
            public void run() {
                sender.sendMessage("§e[§cSystemInfoLog§e] §aCPU Name: " + OshiUtil.getCpuInfo().getCpuModel().split("\n")[0]);
                sender.sendMessage("§e[§cSystemInfoLog§e] §aCPU Core Number: " + OshiUtil.getCpuInfo().getCpuNum());
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
                    sender.sendMessage("§e[§cSystemInfoLog§e] §cAn error occurred while reading tps:");
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
                    sender.sendMessage("§e[§cSystemInfoLog§e] §aServer TPS From last 5s, 1m, 5m, 15m: ");
                    sender.sendMessage("§e[§cSystemInfoLog§e] " + newtps.get(0) + ", " + newtps.get(1) + ", " + newtps.get(2) + ", " + newtps.get(3));
                }
            }
        }
        class diskThread extends Thread{
            @Override
            public void run() {
                for (int i = 0; i <= OshiUtil.getDiskStores().size() - 1; i++) {
                    sender.sendMessage("§e[§cSystemInfoLog§e] §aDisk " + i + ": ");
                    sender.sendMessage("§e[§cSystemInfoLog§e] §a" + OshiUtil.getDiskStores().get(i).getModel().replace(" (标准磁盘驱动器)", "") + ": ");
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
                    sender.sendMessage("§e[§cSystemInfoLog§e] §aSize: " + formatsize);
                    String formatread;
                    String formatwrite;
                    long read = OshiUtil.getDiskStores().get(i).getReadBytes();
                    long write = OshiUtil.getDiskStores().get(i).getWriteBytes();
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
                    sender.sendMessage("§e[§cSystemInfoLog§e] §aRead: " + formatread);
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
                    sender.sendMessage("§e[§cSystemInfoLog§e] §aWrite: " + formatwrite);
                }
            }
        }
        int length = strings.length;
        if(length == 0){
            sender.sendMessage("§cSystemInfoLog Help");
            sender.sendMessage("§e/sysinfo tps §6View server TPS");
            sender.sendMessage("§e/sysinfo cpu §6View CPU information and occupancy");
            sender.sendMessage("§e/sysinfo memory §6View memory usage");
            sender.sendMessage("§e/sysinfo disk §6View disk information");
            sender.sendMessage("§e/sysinfo network §6View network information and stage");
        } else if(length == 1){
            if(strings[0].equals("tps")){
                TPSThread tps = new TPSThread();
                tps.start();
            }
            else if(strings[0].equals("cpu")){
                sender.sendMessage("§e[§cSystemInfoLog§e] §aCPU Information: ");
                sender.sendMessage("§e[§cSystemInfoLog§e] §b§oGetting data... Please wait...");
                CPUThread cpu = new CPUThread();
                cpu.start();
            } else if(strings[0].equals("memory")){
                sender.sendMessage("§e[§cSystemInfoLog§e] §aMemory Information: ");
                sender.sendMessage("§e[§cSystemInfoLog§e] §b§oGetting data... Please wait...");
                sender.sendMessage("§e[§cSystemInfoLog§e] §aMemory Usage: "+OshiUtil.getMemory().toString().replace("Available: ","").replace("GiB","GB"));
            } else if(strings[0].equals("disk")) {
                sender.sendMessage("§e[§cSystemInfoLog§e] §aDisk Information: ");
                sender.sendMessage("§e[§cSystemInfoLog§e] §b§oGetting data... Please wait...");
                diskThread disk = new diskThread();
                disk.start();
            } else if(strings[0].equals("network")){
                Thread network = new Thread();
                sender.sendMessage("§e[§cSystemInfoLog§e] §aMemory Information: ");
                sender.sendMessage("§e[§cSystemInfoLog§e] §b§oGetting data... Please wait...");
                NetworkThread nw = new NetworkThread();
                nw.start();
            } else{
                sender.sendMessage("§cSystemInfoLog Help");
                sender.sendMessage("§e/sysinfo tps §6View server TPS");
                sender.sendMessage("§e/sysinfo cpu §6View CPU information and occupancy");
                sender.sendMessage("§e/sysinfo memory §6View memory usage");
                sender.sendMessage("§e/sysinfo disk §6View disk information");
                sender.sendMessage("§e/sysinfo network §6View network information and stage");
            }
        } else{
            sender.sendMessage("§cSystemInfoLog Help");
            sender.sendMessage("§e/sysinfo tps §6View server TPS");
            sender.sendMessage("§e/sysinfo cpu §6View CPU information and occupancy");
            sender.sendMessage("§e/sysinfo memory §6View memory usage");
            sender.sendMessage("§e/sysinfo disk §6View disk information");
            sender.sendMessage("§e/sysinfo network §6View network information and stage");
        }
        return false;
    }
}
