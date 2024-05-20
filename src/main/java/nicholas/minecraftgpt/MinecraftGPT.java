package nicholas.minecraftgpt;

import nicholas.minecraftgpt.commands.gpt;
import nicholas.minecraftgpt.events.ChatListener;
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
        openAI = new OpenAI(this);

        // Register the command
        gpt gptCommand = new gpt(openAI);
        getCommand("gpt").setExecutor(gptCommand);

        // Register the event listener
        Bukkit.getPluginManager().registerEvents(new ChatListener(gptCommand), this);

        Bukkit.getConsoleSender().sendMessage("------ MinecraftGPT Enabled ------");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        Bukkit.getConsoleSender().sendMessage("------ MinecraftGPT Disabled ------");
    }

    public static MinecraftGPT getPlugin(){
        return plugin;
    }

    public static OpenAI getOpenAI(){
        return openAI;
    }

}
