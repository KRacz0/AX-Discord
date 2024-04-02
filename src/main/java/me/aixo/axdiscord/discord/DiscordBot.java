package me.aixo.axdiscord.discord;

import me.aixo.axdiscord.AXDiscord;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static me.aixo.axdiscord.database.DatabaseManager.isDiscordUserSynced;
import static me.aixo.axdiscord.database.DatabaseManager.savePlayerInfo;

public class DiscordBot extends ListenerAdapter {
    String codeAcceptedMsgDiscord = AXDiscord.getInstance().getConfig().getString("messages.discord.code-accepted");
    String codeInvalidMsgDiscord = AXDiscord.getInstance().getConfig().getString("messages.discord.code-invalid");
    String codeInvalidOfflineMsgDiscord = AXDiscord.getInstance().getConfig().getString("messages.discord.code-acceptedOffline");
    String successfulSyncBroadcastMsg = ChatColor.translateAlternateColorCodes('&', AXDiscord.getInstance().getConfig().getString("messages.minecraft.successful-sync-broadcast"));

    private RoleSynchronizer roleSynchronizer = new RoleSynchronizer();

    public static JDA startBot() {
        try {
            JDABuilder builder = JDABuilder.createDefault(AXDiscord.getBotToken())
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT);
            builder.addEventListeners(new DiscordBot());
            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Oznaczanie użytkownika w wiadomości
        String mention = event.getAuthor().getAsMention();

        // Ignorowanie wiadomosci od bota
        if (event.getAuthor().isBot()) return;

        if (!event.getChannel().getId().equals(AXDiscord.getChannelId())) return;
        String message = event.getMessage().getContentRaw();

        if (CodeManager.getPlayerCodes().containsValue(message)) {
            UUID playerUUID = null;
            for (Map.Entry<UUID, String> entry : CodeManager.getPlayerCodes().entrySet()) {
                if (entry.getValue().equals(message)) {
                    playerUUID = entry.getKey();
                    break;
                }
            }

            final UUID finalPlayerUUID = playerUUID;
            String discordId = event.getAuthor().getId();

            if (isDiscordUserSynced(discordId)) {
                event.getChannel().sendMessage(mention + " Twoje konto Discord jest już zsynchronizowane z kontem Minecraft!")
                        .queue(msg -> {
                            msg.delete().queueAfter(5, TimeUnit.SECONDS);
                            event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
                        });
                return;
            }

            // scheduler do przeniesienia operacji na główny wątek
            Bukkit.getScheduler().runTask(AXDiscord.getInstance(), () -> {
                Player player = Bukkit.getServer().getPlayer(finalPlayerUUID);
                if (player == null || !player.isOnline()) {
                    event.getChannel().sendMessage(mention + " " + codeInvalidOfflineMsgDiscord)
                            .queue(msg -> {
                                msg.delete().queueAfter(5, TimeUnit.SECONDS);
                                event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
                            });
                    return;
                }

                savePlayerInfo(finalPlayerUUID, player.getName(), discordId);
                List<String> commands = AXDiscord.getInstance().getConfig().getStringList("commands_for_link");
                for (String cmd : commands) {
                    cmd = cmd.replace("%player%", player.getName());
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
                    Bukkit.broadcastMessage(successfulSyncBroadcastMsg.replace("%player%", player.getName()));

                    // zmiana nicku discord na nick mc
                    NicknameManager.setNickname(event, player);

                    // Nadawanie roli użytkownikowi "Gracz serwera"
                    Guild guild = event.getGuild();
                    Role role = guild.getRoleById("933473756553306162");
                    if (role == null) {
                        return;
                    }

                    Member member = guild.retrieveMember(event.getAuthor()).complete();

                    if (member == null) {
                        return;
                    }

                    // Synchronizowanie roli mc z Discordem
                    roleSynchronizer.synchronizeRolesWithDiscord(member, finalPlayerUUID);

                    if (!member.getRoles().contains(role)) {
                        guild.addRoleToMember(member, role).queue();
                    }
                }
                event.getChannel().sendMessage(mention + " " + codeAcceptedMsgDiscord)
                        .queue(msg -> {
                            msg.delete().queueAfter(5, TimeUnit.SECONDS);
                            event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
                        });
                CodeManager.getPlayerCodes().remove(message);
            });
        } else {
            event.getChannel().sendMessage(mention + " " + codeInvalidMsgDiscord)
                    .queue(msg -> {
                        msg.delete().queueAfter(5, TimeUnit.SECONDS);
                        event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
                    });
        }
    }
}
