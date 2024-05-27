package nicholas.minecraftgpt.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import nicholas.minecraftgpt.Messenger;
import nicholas.minecraftgpt.MinecraftGPT;
import nicholas.minecraftgpt.OpenAI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public class gpt implements CommandExecutor {

    private final OpenAI openAI;
    private final HashSet<Player> queryingPlayers = new HashSet<>();

    public gpt(OpenAI openAI){
        this.openAI = openAI;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(commandSender instanceof Player)){
            return false;
        }

        Player player = (Player) commandSender;

        if(queryingPlayers.contains(player)){
            queryingPlayers.remove(player);
            Messenger.sendInfo(player, "GPT Conversation Mode Deactivated.");
        }else{
            queryingPlayers.add(player);
            Messenger.sendInfo(player, "GPT Conversation Mode Activated. Type your prompt in chat to generate a response. Do /gpt again to deactivate conversation mode.");
        }

        return true;
    }

    public void handleChatMessage(Player player, Component message){

        if(queryingPlayers.contains(player)){

            // Check if the player is rate limited
            if(openAI.isRateLimited(player.getUniqueId())){

                int playerCooldown = openAI.getPlayerCooldown(player.getUniqueId());

                Messenger.sendError(player, "You are rate limited. Please wait before making another request. Number of requests reset in "+playerCooldown+" minutes.");
                return;
            }else{
                openAI.incrementRequests(player.getUniqueId());
            }


            // Check if the number of tokens in the message exceeds the maximum allowed
            int maxTokens = MinecraftGPT.getPlugin().getConfig().getInt("max_tokens_per_request");

            PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
            String messageString = serializer.serialize(message);
            String[] tokens = messageString.split(" ");

            if(tokens.length > maxTokens){
                Messenger.sendError(player, "You have exceeded the maximum number of tokens per request. "+maxTokens+" tokens are allowed per request. You entered "+tokens.length+" tokens.");
                return;
            }

            Bukkit.getLogger().info("Conversation mode| "+player.getName()+": "+messageString);


            // Call the OpenAI class to generate a response
            Messenger.sendInfo(player, "Generating response...");

            OpenAI openAI = MinecraftGPT.getOpenAI();

            // Perform the OpenAI query asynchronously
            CompletableFuture.supplyAsync(() -> openAI.queryGPT(messageString))
                    .thenAccept(response -> {
                        // Send the response back to the player on the main server thread
                        Bukkit.getScheduler().runTask(MinecraftGPT.getPlugin(), () -> {
                            sendResponse(player, response);
                        });
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
        }
    }

    private void sendResponse(Player player, String response){

        Bukkit.getLogger().info("Conversation mode| Sending response to "+player.getName()+": "+response);
        Messenger.sendInfo(player, response);

    }

    public boolean isQuerying(Player player){
        return queryingPlayers.contains(player);
    }

}
