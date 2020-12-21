package me._xGQD.BetterVanilla;

import org.bukkit.plugin.java.JavaPlugin;


import java.util.logging.Logger;

public class Main extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
    }

}
