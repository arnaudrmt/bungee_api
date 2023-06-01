package fr.first92.bungeeapi.party.schedulers;

import fr.first92.bungeeapi.BungeeApi;
import fr.first92.bungeeapi.party.commands.PartyCommand;
import fr.first92.sync.utils.PlayerFetcher;
import fr.first92.sync.data.providers.PartyProvider;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PartyInviteScheduler {

    private int timer;
    private final UUID p;
    private final UUID target;

    public PartyInviteScheduler(int timer, UUID p, UUID target) {
        this.timer = timer;
        this.p = p;
        this.target = target;

        Runnable t = task.getTask();
        t.run();
    }

    ScheduledTask task = BungeeCord.getInstance().getScheduler().schedule(BungeeApi.getInstance(), new Runnable() {

        @Override
        public void run() {

            if(new PartyProvider(target).getParty() != null || PartyCommand.instance.getInvites().entrySet().stream()
                    .noneMatch(rs -> rs.getValue().get(0).equals(p) && rs.getValue().get(1).equals(target))) {

                if(PartyCommand.instance.getInvites().entrySet().stream().anyMatch(rs -> rs.getValue().get(0).equals(p)
                        && rs.getValue().get(1).equals(target))) {

                    Map.Entry<UUID, List<UUID>> uuidListEntry = PartyCommand.instance.getInvites().entrySet().stream().filter(
                            rs -> rs.getValue().get(0).equals(p) && rs.getValue().get(1).equals(target)).findFirst().get();

                    PartyCommand.instance.getInvites().remove(uuidListEntry.getKey());
                }

                task.cancel();
            }

            if(timer == 0) {



                if(PartyCommand.instance.getInvites().entrySet().stream().anyMatch(rs -> rs.getValue().get(0).equals(p)
                        && rs.getValue().get(1).equals(target))) {

                    Map.Entry<UUID, List<UUID>> uuidListEntry = PartyCommand.instance.getInvites().entrySet().stream().filter(
                            rs -> rs.getValue().get(0).equals(p) && rs.getValue().get(1).equals(target)).findFirst().get();

                    PartyCommand.instance.getInvites().remove(uuidListEntry.getKey());
                }

                if(BungeeCord.getInstance().getPlayer(p) != null) {

                    BungeeCord.getInstance().getPlayer(p).sendMessage(
                            "§cThe party invite to §6" + new PlayerFetcher().getName(target) + "§c has expired!");
                }

                if(BungeeCord.getInstance().getPlayer(target) != null) {

                    BungeeCord.getInstance().getPlayer(target).sendMessage(
                            "§cThe party invite from §6" + new PlayerFetcher().getName(p) + "§c has expired!");
                }

                task.cancel();
            }

            timer --;
        }

    }, 1, 1, TimeUnit.SECONDS);
}
