package fr.first92.bungeeapi.whitelist.events;

import fr.first92.commons.Whitelist;
import fr.first92.sync.data.providers.AccountProvider;
import fr.first92.sync.data.providers.WhitelistProvider;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class WhitelistEvent implements Listener {

    @EventHandler
    public void onJoin(ServerConnectEvent e) {

        if(!e.getTarget().getName().equalsIgnoreCase("default")) {

            String server = e.getTarget().getName().split("_")[1];
            ProxiedPlayer player = e.getPlayer();

            if(new WhitelistProvider(server).exist()) {

                Whitelist whitelist = new WhitelistProvider(server).getWhitelist();

                if(whitelist.isWhitelisted()) {
                    if(!whitelist.getPlayerWhitelisted().contains(player.getUniqueId())) {
                        if(!whitelist.getRankWhitelisted().contains(new AccountProvider(player.getUniqueId())
                                .getAccount().getRank())) {
                            player.disconnect("§6---------§7*§6---------§7*§6---------§7*§6---------\n\n" +
                                    "§cThis server is undergoing a maintenance\n" +
                                    "§cPlease come back later\n\n" +
                                    "§6---------§7*§6---------§7*§6---------§7*§6---------");
                        }
                    }
                }
            }
        }
    }
}
