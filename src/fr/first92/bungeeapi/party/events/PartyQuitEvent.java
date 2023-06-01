package fr.first92.bungeeapi.party.events;

import fr.first92.bungeeapi.party.commands.PartyCommand;
import fr.first92.bungeeapi.party.schedulers.PartyLeaveScheduler;
import fr.first92.commons.PartyTemplate;
import fr.first92.sync.data.providers.PartyProvider;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PartyQuitEvent implements Listener {

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {

        ProxiedPlayer p = e.getPlayer();

        if(new PartyProvider(p).getParty() != null) {

            PartyProvider partyProvider = new PartyProvider(p);
            PartyTemplate party = partyProvider.getParty();

            party.getPlayers().keySet().stream().filter(rs -> BungeeCord.getInstance().getPlayer(rs) != null)
                    .forEach(rs -> BungeeCord.getInstance().getPlayer(rs).sendMessage(
                            "§dParty ∙ §6" + p.getName() + " §cleft the game."
                    ));

            if(!(new PartyProvider(p).getParty().getPlayers().keySet().stream().filter(rs -> BungeeCord.getInstance()
                    .getPlayer(rs) != null).count() <= 1)) {

                if (party.getOwner().equals(p.getUniqueId())) {

                    UUID key = party.getPlayers().entrySet().stream().filter(rs ->
                            BungeeCord.getInstance().getPlayer(rs.getKey()) != null &&
                                    !BungeeCord.getInstance().getPlayer(rs.getKey()).equals(p)).iterator().next().getKey();

                    party.setOwner(key);
                    BungeeCord.getInstance().getPlayer(key).sendMessage("§cYou're now on the leader of the party!");
                }

                new PartyLeaveScheduler(60, p.getUniqueId());
            } else {
                new PartyProvider(new PartyProvider(p).getUUIDFromRedis()).deleteParty();
            }
        }

        if(PartyCommand.instance.getInvites().entrySet().stream().anyMatch(rs -> rs.getValue().contains(p.getUniqueId()))) {

            Map.Entry<UUID, List<UUID>> uuidListEntry = PartyCommand.instance.getInvites().entrySet().stream().filter(rs -> rs.getValue().contains(p.getUniqueId()))
                    .findFirst().get();

            PartyCommand.instance.getInvites().remove(uuidListEntry.getKey());
        }
    }
}
