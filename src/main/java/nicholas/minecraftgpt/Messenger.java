package nicholas.minecraftgpt;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messenger {

    public static void sendError(CommandSender sender, String message){
        sender.sendMessage(ChatColor.RED + message);
    }

    public static void sendInfo(CommandSender sender, String message){
        sender.sendMessage(ChatColor.GRAY + message);
    }

    public static void sendSuccess(CommandSender sender, String message){
        sender.sendMessage(ChatColor.GREEN + message);
    }

    public static void sendPlayerError(Player player, String message){
        player.sendMessage(ChatColor.RED + message);
    }

    public static void sendPlayerInfo(Player player, String message){
        player.sendMessage(ChatColor.GRAY + message);
    }

    public static void sendPlayerSuccess(Player player, String message){
        player.sendMessage(ChatColor.GREEN + message);
    }

}