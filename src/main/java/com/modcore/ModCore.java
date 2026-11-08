package com.modcore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ModCore extends JavaPlugin {

    public CombatManager combat;
    public VanishManager vanish;
    public PunishManager punish;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        combat = new CombatManager(this);
        vanish = new VanishManager(this);
        punish = new PunishManager(this);

        PluginManager pm = getServer().getPluginManager();
        for (Listener l : new Listener[]{combat, vanish, punish}) pm.registerEvents(l, this);

        for (String c : new String[]{"ban", "unban", "mute", "unmute", "warn", "warnings", "clearwarnings"}) cmd(c, punish);
        cmd("vanish", vanish);

        combat.start();
        vanish.start();
        getLogger().info("ModCore enabled.");
    }

    @Override
    public void onDisable() {
        if (combat != null) combat.stop();
        if (vanish != null) vanish.stop();
    }

    private void cmd(String name, TabExecutor executor) {
        PluginCommand c = getCommand(name);
        if (c == null) {
            getLogger().warning("Command missing from plugin.yml: " + name);
            return;
        }
        c.setExecutor(executor);
        c.setTabCompleter(executor);
    }

    /** Handles /modcore */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            Msg.send(sender, "Config reloaded.");
        } else {
            Msg.send(sender, "Usage: /modcore reload");
        }
        return true;
    }
}
