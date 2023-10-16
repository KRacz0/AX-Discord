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

        // Pobierz instancję LuckPerms
        LuckPerms luckPerms = LuckPermsProvider.get();

        // Zarejestruj nasłuchiwacz w EventBus LuckPerms
        EventBus eventBus = luckPerms.getEventBus();
        eventBus.subscribe(NodeAddEvent.class, this::onNodeAdd);
        eventBus.subscribe(NodeRemoveEvent.class, this::onNodeRemove);
    }

    public void onNodeAdd(NodeAddEvent event) {
        if (event.getTarget() instanceof User) {
            User user = (User) event.getTarget();
            UUID uuid = UUID.fromString(user.getUniqueId().toString());

            String discordId = getDiscordIdByUUID(uuid);
            Guild guild = AXDiscord.getJDA().getGuildById(AXDiscord.getGuildId());
            Member member = guild.retrieveMemberById(discordId).complete();
            roleSynchronizer.synchronizeRolesWithDiscord(member, uuid);
        }
    }

    public void onNodeRemove(NodeRemoveEvent event) {
        if (event.getTarget() instanceof User) {
            User user = (User) event.getTarget();
            UUID uuid = UUID.fromString(user.getUniqueId().toString());

            String discordId = getDiscordIdByUUID(uuid);
            Guild guild = AXDiscord.getJDA().getGuildById(AXDiscord.getGuildId());
            Member member = guild.retrieveMemberById(discordId).complete();

            roleSynchronizer.removeRolesFromDiscord(member, uuid);
        }
    }

}