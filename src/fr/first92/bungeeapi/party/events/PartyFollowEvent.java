package fr.first92.bungeeapi.party.events;

import fr.first92.commons.PartyTemplate;
import fr.first92.commons.ServerManager;
import fr.first92.sync.data.providers.PartyProvider;
import fr.first92.sync.data.providers.ServerProvider;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyFollowEvent implements Listener {

    @EventHandler
    public void onSwitchServer(ServerConnectedEvent e) {

        ProxiedPlayer p = e.getPlayer();
        ServerInfo target = e.getServer().getInfo();

        if(new PartyProvider(p).getParty() != null && new PartyProvider(p).getParty().getOwner().equals(p.getUniqueId())) {

            PartyProvider partyProvider = new PartyProvider(p);
            PartyTemplate partyTemplate = partyProvider.getParty();

            ServerProvider serverProvider = new ServerProvider(target.getName());
            ServerManager serverManager = serverProvider.getServerFromRedis();

            partyTemplate.getPlayers().keySet().stream()
                    .filter(rs -> BungeeCord.getInstance().getPlayer(rs) != null)
                    .filter(rs -> partyTemplate.getPlayers().get(rs).equals(true))
                    .forEach(rs -> BungeeCord.getInstance().getPlayer(rs).connect(target));
        }
    }

    @EventHandler
    public void onSeverConnect(ServerConnectEvent e) {

        ProxiedPlayer p = e.getPlayer();
        ServerInfo target = e.getTarget();

        if(new PartyProvider(p).getParty() != null && new PartyProvider(p).getParty().getOwner().equals(p.getUniqueId())) {

            PartyProvider partyProvider = new PartyProvider(p);
            PartyTemplate partyTemplate = partyProvider.getParty();

            ServerProvider serverProvider = new ServerProvider(target.getName());
            ServerManager serverManager = serverProvider.getServerFromRedis();

            if (serverManager.getMaxParty() < partyTemplate.getPlayers().size()) {

                e.setCancelled(true);

                BungeeCord.getInstance().getPlayer(partyTemplate.getOwner()).sendMessage(
                        "§cThis type of server can only accept parties of " + serverManager.getMaxParty() + " or less."
                );
            }
        }
    }
}
