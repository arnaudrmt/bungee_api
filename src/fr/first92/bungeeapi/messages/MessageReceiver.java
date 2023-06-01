package fr.first92.bungeeapi.messages;

import fr.first92.bungeeapi.BungeeApi;
import fr.first92.bungeeapi.servers.ServerManager;
import fr.first92.sync.data.redis.redisaccess.RedisAccess;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.redisson.api.RedissonClient;

import java.util.List;

public class MessageReceiver {

    RedissonClient redissonClient = RedisAccess.instance.getRedissonClient();

    public void subscribe() {

        redissonClient.getTopic("bungee").addListener((s, o) -> {

            List<String> l = (List<String>) o;

            if(l.get(0).equalsIgnoreCase("api")) {

                if(l.get(1).equalsIgnoreCase("server")) {

                    if(l.get(2).equalsIgnoreCase("register")) {

                        BungeeApi.getInstance().getProxy().getServers().put(l.get(3), new ServerManager().createServerInfo(
                                l.get(3), Integer.valueOf(l.get(4))));

                    } else if(l.get(2).equalsIgnoreCase("unregister")) {

                        BungeeApi.getInstance().getProxy().getServers().remove(l.get(3));

                    } else if(l.get(2).equalsIgnoreCase("connect")) {

                        ProxiedPlayer p = BungeeApi.getInstance().getProxy().getPlayer(l.get(4));
                        String name = l.get(3);

                        p.connect(BungeeApi.getInstance().getProxy().getServerInfo(name));
                    }
                }
            }
        });
    }
}
