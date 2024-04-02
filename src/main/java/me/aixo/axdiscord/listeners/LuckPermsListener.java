package me.aixo.axdiscord.listeners;

import me.aixo.axdiscord.AXDiscord;
import me.aixo.axdiscord.discord.RoleSynchronizer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.user.User;
import org.bukkit.event.Listener;

import java.util.UUID;

import static me.aixo.axdiscord.database.DatabaseManager.getDiscordIdByUUID;

public class LuckPermsListener implements Listener {

    private final RoleSynchronizer roleSynchronizer;

    public LuckPermsListener(RoleSynchronizer roleSynchronizer) {
        this.roleSynchronizer = roleSynchronizer;

        // Pobieranie LuckPerms
        LuckPerms luckPerms = LuckPermsProvider.get();

        // Nasłuchiwanie LuckPerms
        EventBus eventBus = luckPerms.getEventBus();
        eventBus.subscribe(NodeAddEvent.class, this::onNodeAdd);
        eventBus.subscribe(NodeRemoveEvent.class, this::onNodeRemove);
    }

    public void onNodeAdd(NodeAddEvent event) {
        if (event.getTarget() instanceof User) {
            User user = (User) event.getTarget();
            UUID uuid = UUID.fromString(user.getUniqueId().toString());

            String discordId = getDiscordIdByUUID(uuid);
            // Sprawdzanie czy ID użytkownika Discord nie istnieje lub jest puste
            if (discordId == null || discordId.isEmpty()) {
                return;
            }

            Guild guild = AXDiscord.getJDA().getGuildById(AXDiscord.getGuildId());
            // sprawdzanie czy serwer istnieje
            if (guild == null) {
                return;
            }

            Member member = guild.retrieveMemberById(discordId).complete();
            // sprawdzanie czy uzytkownik nalezy do serwera
            if (member == null) {
                return;
            }

            roleSynchronizer.synchronizeRolesWithDiscord(member, uuid);
        }
    }

    public void onNodeRemove(NodeRemoveEvent event) {
        if (event.getTarget() instanceof User) {
            User user = (User) event.getTarget();
            UUID uuid = UUID.fromString(user.getUniqueId().toString());

            String discordId = getDiscordIdByUUID(uuid);
            // sprawdzanie czy id uzytkownika discord istnieje
            if (discordId == null || discordId.isEmpty()) {
                return;
            }

            Guild guild = AXDiscord.getJDA().getGuildById(AXDiscord.getGuildId());
            // sprawdzanie czy gildia istnieje
            if (guild == null) {
                return;
            }

            Member member = guild.retrieveMemberById(discordId).complete();
            // sprawdzanie czy uzytkownik nalezy do serwera
            if (member == null) {
                return;
            }

            roleSynchronizer.removeRolesFromDiscord(member, uuid);
        }
    }
}