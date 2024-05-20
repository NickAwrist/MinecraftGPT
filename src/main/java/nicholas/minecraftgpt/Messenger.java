package nicholas.minecraftgpt;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class Messenger {

    private static final Component prefix = Component.text("MinecraftGPT", NamedTextColor.GOLD).append(Component.text(" | ", NamedTextColor.GRAY));

    public static void sendError(CommandSender sender, String message){
        sender.sendMessage(prefix.append(Component.text(message, NamedTextColor.RED)));
    }

    public static void sendSuccess(CommandSender sender, String message){
        sender.sendMessage(prefix.append(Component.text(message, NamedTextColor.GREEN)));
    }

    public static void sendInfo(CommandSender sender, String message){
        sender.sendMessage(prefix.append(Component.text(message, NamedTextColor.GRAY)));
    }

    public static void sendInfo(CommandSender sender, String message, Component prefix){
        sender.sendMessage(prefix.append(Component.text(message, NamedTextColor.GRAY)));
    }

}