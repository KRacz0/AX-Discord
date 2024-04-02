package me.aixo.axdiscord.commands;
import me.aixo.axdiscord.database.DatabaseManager;
import me.aixo.axdiscord.AXDiscord;
import me.aixo.axdiscord.discord.CodeManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.UUID;

public class DiscordCommand implements CommandExecutor {
    String alreadySyncedMsg = ChatColor.translateAlternateColorCodes('&', AXDiscord.getInstance().getConfig().getString("messages.minecraft.already-synced"));
    String codeGeneratedMsg = ChatColor.translateAlternateColorCodes('&', AXDiscord.getInstance().getConfig().getString("messages.minecraft.code-generated"));
    String noPermissionsMsg = ChatColor.translateAlternateColorCodes('&', AXDiscord.getInstance().getConfig().getString("messages.minecraft.no-permission"));



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

                // sprawdzanie, czy gracz jest w bazie danych
                if (DatabaseManager.isPlayerInDatabase(playerUUID)) {
                    player.sendMessage(alreadySyncedMsg); //konto jest już zsynchronizowane
                    return true;
                }

                String code = CodeManager.generateCode(playerUUID);
                if (code != null) {
                    String formattedMessage = codeGeneratedMsg.replace("%code%", code);
                    TextComponent messageComponent = new TextComponent(formattedMessage);
                    messageComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/lizardmc"));
                    player.spigot().sendMessage(messageComponent);
                } else {
                    player.sendMessage("Nie można wygenerować kodu. Spróbuj ponownie później.");
                }
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("axdiscord.reload")) {
                    sender.sendMessage(noPermissionsMsg);
                    return true;
                }
                AXDiscord.getInstance().reloadConfig();
                sender.sendMessage("AXDiscord została zrelodowany!");
            }
        }
        return true;
    }
}




