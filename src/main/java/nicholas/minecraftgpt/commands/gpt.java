package nicholas.minecraftgpt.commands;

import nicholas.minecraftgpt.Messenger;
import nicholas.minecraftgpt.MinecraftGPT;
import nicholas.minecraftgpt.OpenAI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.validation.constraints.NotNull;
import java.util.concurrent.CompletableFuture;

public class gpt implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(commandSender instanceof Player)){
            return false;
        }

        Player player = (Player) commandSender;

        int maxTokens = MinecraftGPT.getPlugin().getConfig().getInt("max_tokens_per_request");
        int tokens = strings.length;

        String prompt = String.join(" ", strings);

        if(tokens > maxTokens){
            Messenger.sendError(player, "You have exceeded the maximum number of tokens per request. "+maxTokens+" tokens are allowed per request. You entered "+tokens+" tokens.");
            return true;
        }

        // Call the OpenAI class to generate a response

        Messenger.sendInfo(player, "Generating response...");

        OpenAI openAI = MinecraftGPT.getOpenAI();

        // Perform the OpenAI query asynchronously
        CompletableFuture.supplyAsync(() -> openAI.queryGPT(prompt))
                .thenAccept(response -> {
                    // Send the response back to the player on the main server thread
                    Bukkit.getScheduler().runTask(MinecraftGPT.getPlugin(), () -> {
                        Messenger.sendInfo(player, response);
                    });
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });

        return true;
    }
}
