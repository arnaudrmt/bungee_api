package fr.first92.bungeeapi.party.schedulers;

import fr.first92.bungeeapi.BungeeApi;
import fr.first92.sync.utils.PlayerFetcher;
import fr.first92.commons.PartyTemplate;
import fr.first92.sync.data.providers.PartyProvider;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PartyLeaveScheduler {

    private int timer;
    private UUID p;

    public PartyLeaveScheduler(int timer, UUID p) {
        this.timer = timer;
        this.p = p;

        Runnable t = task.getTask();
        t.run();
    }

    ScheduledTask task = BungeeCord.getInstance().getScheduler().schedule(BungeeApi.getInstance(), new Runnable() {

        @Override
        public void run() {

            if(timer < 60 && (BungeeCord.getInstance().getPlayer(p) != null || new PartyProvider(p).getParty() == null)) {
                task.cancel();
            }

            if(timer == 0) {

                PartyTemplate party = new PartyProvider(p).getParty();

                if(party.getPlayers().keySet().stream().filter(rs ->
                        BungeeCord.getInstance().getPlayer(rs) != null).count() <= 1) {

                    party.getPlayers().keySet().stream().filter(rs ->
                            BungeeCord.getInstance().getPlayer(rs) != null).forEach(rs -> BungeeCord.getInstance()
                            .getPlayer(rs).sendMessage("§cThe party has been disbanded!"));

                    new PartyProvider(new PartyProvider(p).getUUIDFromRedis()).deleteParty();

                } else {

                    party.getPlayers().remove(p);

                    new PartyProvider(new PartyProvider(p).getUUIDFromRedis()).sendPartyToRedis(party);

                    party.getPlayers().keySet().stream().filter(rs -> BungeeCord.getInstance().getPlayer(rs) != null)
                            .forEach(rs -> BungeeCord.getInstance().getPlayer(rs).sendMessage(
                                    "§6" + new PlayerFetcher().getName(p) + "§d has left the party!"
                            ));
                }

                task.cancel();
            }

            timer --;
        }

    }, 1, 1, TimeUnit.SECONDS);
}
