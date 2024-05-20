package nicholas.minecraftgpt.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import nicholas.minecraftgpt.Messenger;
import nicholas.minecraftgpt.commands.gpt;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class ChatListener implements Listener {

    private final gpt gptCommand;

    public ChatListener(gpt gptCommand){
        this.gptCommand = gptCommand;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event){
        if(gptCommand.isQuerying(event.getPlayer())){
            gptCommand.handleChatMessage(event.getPlayer(), event.message());

            Component prefix = Component.text("You", NamedTextColor.AQUA).append(Component.text(" | ", NamedTextColor.GRAY));

            PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
            String messageString = serializer.serialize(event.message());

            Messenger.sendInfo(event.getPlayer(), messageString, prefix);

            event.setCancelled(true);
        }
    }
}
