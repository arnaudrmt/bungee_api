package fr.first92.bungeeapi.party.events;

import fr.first92.commons.PartyTemplate;
import fr.first92.sync.data.providers.PartyProvider;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyJoinEvent implements Listener {

    @EventHandler
    public void onConnect(ServerConnectEvent e) {

        ProxiedPlayer p = e.getPlayer();

        if(e.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)) {

            if(new PartyProvider(p).getParty() != null) {

                PartyTemplate party = new PartyProvider(p).getParty();

                party.getPlayers().keySet().stream().filter(rs -> BungeeCord.getInstance().getPlayer(rs) != null)
                        .forEach(rs -> BungeeCord.getInstance().getPlayer(rs).sendMessage(
                                "§dParty ∙ §6" + p.getName() + "§e joined the game!"
                        ));
            }
        }
    }
}
