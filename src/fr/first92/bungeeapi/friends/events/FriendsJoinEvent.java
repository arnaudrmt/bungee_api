package fr.first92.bungeeapi.friends.events;

import fr.first92.commons.Account;
import fr.first92.sync.data.providers.AccountProvider;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class FriendsJoinEvent implements Listener {

    @EventHandler
    public void onConnect(ServerConnectEvent e){


        if(e.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)) {

            ProxiedPlayer p = e.getPlayer();

            Account account = new AccountProvider(p.getUniqueId()).getAccount();

            account.getFriends().stream().filter(rs -> BungeeCord.getInstance().getPlayer(rs) != null)
                    .forEach(rs -> BungeeCord.getInstance().getPlayer(rs).sendMessage(
                            "§aFriends ∙ §6" + p.getName() + "§e joined the game!"));
        }
    }
}
