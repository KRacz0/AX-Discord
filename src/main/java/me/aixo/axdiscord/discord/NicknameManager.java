package me.aixo.axdiscord.discord;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class NicknameManager {

    public static void setNickname(MessageReceivedEvent event, Player player) {
        if (player != null && player.isOnline()) {
            try {
                event.getGuild().modifyNickname(event.getMember(), player.getName()).queue();
            } catch (net.dv8tion.jda.api.exceptions.HierarchyException e) {
                player.sendMessage(ChatColor.RED + "Nie udało się zsynchronizować nicknameu na serwerze discord");
            }
        }
    }
}

