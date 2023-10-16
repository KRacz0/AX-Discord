package me.aixo.axdiscord.commands;
import me.aixo.axdiscord.database.DatabaseManager;
import me.aixo.axdiscord.AXDiscord;
import me.aixo.axdiscord.discord.CodeManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DiscordCommand implements CommandExecutor {
    String alreadySyncedMsg = ChatColor.translateAlternateColorCodes('&', AXDiscord.getInstance().getConfig().getString("messages.minecraft.already-synced"));
    String codeGeneratedMsg = ChatColor.translateAlternateColorCodes('&', AXDiscord.getInstance().getConfig().getString("messages.minecraft.code-generated"));
    String noPermissionsMsg = ChatColor.translateAlternateColorCodes('&', AXDiscord.getInstance().getConfig().getString("messages.minecraft.no-permission"));
    String incorrectUsage = ChatColor.translateAlternateColorCodes('&', AXDiscord.getInstance().getConfig().getString("messages.minecraft.incorrect-usage"));



    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("discord")) {
            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Tylko gracze mogą używać tej komendy!");
                    return true;
                }
                Player player = (Player) sender;
                UUID playerUUID = player.getUniqueId();

                // Sprawdzanie, czy gracz jest w bazie danych
                if (DatabaseManager.isPlayerInDatabase(playerUUID)) {
                    player.sendMessage(alreadySyncedMsg); //Twoje konto jest już zsynchronizowane
                    return true;
                }

                // Użyj nowej metody generateCode() z klasy CodeManager
                String code = CodeManager.generateCode(playerUUID);
                if (code != null) {
                    player.sendMessage(codeGeneratedMsg.replace("%code%", code));
                } else {
                    player.sendMessage("Nie można wygenerować kodu. Spróbuj ponownie później.");
                }
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("axdiscord.reload")) {
                    sender.sendMessage(noPermissionsMsg);
                    return true;
                }
                AXDiscord.getInstance().reloadConfig();
                sender.sendMessage("&2AXDiscord została zrelodowany!");
            }
        }
        return true;
    }
}




