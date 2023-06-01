package fr.first92.bungeeapi.events;

import fr.first92.commons.Account;
import fr.first92.commons.RankPermissions;
import fr.first92.sync.data.providers.AccountProvider;
import fr.first92.sync.data.providers.PermissionsProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Comparator;

public class Events implements Listener {

    @EventHandler
    public void onPreLogin(ServerConnectEvent e) {

        if(e.getTarget().getName().equalsIgnoreCase("default")) {

            if(ProxyServer.getInstance().getServers().entrySet().stream().noneMatch(rs -> rs.getValue().getName()
                    .contains("lobby"))) {

                e.getPlayer().disconnect("§cNo servers available!");
                return;
            }

            e.setTarget(ProxyServer.getInstance().getServers().entrySet().stream().filter(s -> s.getValue().getName()
                            .contains("lobby"))
                    .sorted(Comparator.comparing(s -> s.getValue().getPlayers().size()))
                    .reduce((first, second) -> second).get().getValue());
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e) {

        final ProxiedPlayer player = e.getPlayer();


        final AccountProvider accountProvider = new AccountProvider(player.getUniqueId());
        final Account account = accountProvider.getAccount();

        final PermissionsProvider permissionsProvider = new PermissionsProvider(account.getRank());
        final RankPermissions rankPermissions = permissionsProvider.getRankPermissions();

        accountProvider.sendAccountToRedis(account);
        permissionsProvider.sendRankPermissionsToRedis(rankPermissions);
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {

        ProxiedPlayer player = e.getPlayer();
    }
}
