package fr.first92.bungeeapi.friends.events;

import fr.first92.bungeeapi.friends.commands.FriendsCommand;
import fr.first92.sync.data.providers.AccountProvider;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FriendsQuitEvent implements Listener {

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {

        ProxiedPlayer p = e.getPlayer();

        AccountProvider accountProvider = new AccountProvider(p.getUniqueId());

        if(FriendsCommand.instance.getInvites().entrySet().stream().anyMatch(rs -> rs.getValue().contains(p.getUniqueId()))) {

            Map.Entry<UUID, List<UUID>> uuidListEntry = FriendsCommand.instance.getInvites().entrySet().stream().filter(rs -> rs.getValue().contains(p.getUniqueId()))
                    .findFirst().get();

            FriendsCommand.instance.getInvites().values().remove(uuidListEntry.getValue());
        }

        accountProvider.getAccount().getFriends().stream().filter(rs -> BungeeCord.getInstance().getPlayer(rs) != null)
                .forEach(rs -> BungeeCord.getInstance().getPlayer(rs).sendMessage(
                        "§aFriends ∙ §6" + p.getName() + "§c left the game."));
    }
}
