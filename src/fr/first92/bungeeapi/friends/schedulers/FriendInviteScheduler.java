package fr.first92.bungeeapi.friends.schedulers;

import fr.first92.bungeeapi.BungeeApi;
import fr.first92.bungeeapi.friends.commands.FriendsCommand;
import fr.first92.sync.utils.PlayerFetcher;
import fr.first92.sync.data.providers.AccountProvider;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FriendInviteScheduler {

    private int timer;
    private UUID p;
    private UUID target;

    public FriendInviteScheduler(int timer, UUID p, UUID target) {
        this.timer = timer;
        this.p = p;
        this.target = target;

        Runnable t = task.getTask();
        t.run();
    }

    ScheduledTask task = BungeeCord.getInstance().getScheduler().schedule(BungeeApi.getInstance(), new Runnable() {

        @Override
        public void run() {

            if(new AccountProvider(p).getAccount().getFriends().contains(target) ||
                    BungeeCord.getInstance().getPlayer(target) == null ||
                    FriendsCommand.instance.getInvites().entrySet().stream().noneMatch(rs ->
                    rs.getValue().get(0).equals(p) && rs.getValue().get(1).equals(target))) {

                if(FriendsCommand.instance.getInvites()
                        .entrySet().stream().anyMatch(rs -> rs.getValue().get(0).equals(p) &&
                                rs.getValue().get(1).equals(target))) {

                    Map.Entry<UUID, List<UUID>> uuidListEntry = FriendsCommand.instance.getInvites()
                            .entrySet().stream().filter(rs -> rs.getValue().get(0).equals(p) &&
                                    rs.getValue().get(1).equals(target)).findFirst().get();

                    FriendsCommand.instance.getInvites().remove(uuidListEntry.getKey());
                }

                task.cancel();
            }

            if(timer == 0) {

                if(BungeeCord.getInstance().getPlayer(p) != null) {

                    BungeeCord.getInstance().getPlayer(p).sendMessage("§cYou're friendship request to §6" +
                            new PlayerFetcher().getName(target) + "§c expired");
                }

                if(BungeeCord.getInstance().getPlayer(target) != null) {

                    BungeeCord.getInstance().getPlayer(target).sendMessage("§cYou're friendship request from §6" +
                            new PlayerFetcher().getName(p) + "§c expired");
                }

                Map.Entry<UUID, List<UUID>> uuidListEntry = FriendsCommand.instance.getInvites()
                        .entrySet().stream().filter(rs -> rs.getValue().get(0).equals(p) &&
                                rs.getValue().get(1).equals(target)).findFirst().get();

                FriendsCommand.instance.getInvites().remove(uuidListEntry.getKey());

                task.cancel();
            }

            timer --;
        }

    }, 1, 1, TimeUnit.SECONDS);
}
