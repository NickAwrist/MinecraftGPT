package nicholas.minecraftgpt;

import nicholas.minecraftgpt.commands.gpt;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecraftGPT extends JavaPlugin {

    private static MinecraftGPT plugin;
    private static OpenAI openAI;

    @Override
    public void onEnable() {
        // Plugin startup logic

        plugin = this;

        saveDefaultConfig();

        // Create a new instance of the OpenAI class

        Bukkit.getConsoleSender().sendMessage("------ Enabling MinecraftGPT ------");
        openAI = new OpenAI(this);

        // Register the command
        this.getCommand("gpt").setExecutor(new gpt());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        Bukkit.getConsoleSender().sendMessage("------ Disabling MinecraftGPT ------");
    }

    public static MinecraftGPT getPlugin(){
        return plugin;
    }

    public static OpenAI getOpenAI(){
        return openAI;
    }

}
