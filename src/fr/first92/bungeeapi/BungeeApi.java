package fr.first92.bungeeapi;

import fr.first92.bungeeapi.messages.MessageReceiver;
import fr.first92.bungeeapi.registers.RegistersHandler;
import fr.first92.sync.data.redis.messages.RedisMessageSender;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Arrays;

public class BungeeApi extends Plugin {

    private static BungeeApi instance;

    @Override
    public void onEnable() {

        instance = this;

        new RegistersHandler().registerCommands();
        new RegistersHandler().registerEvents();

        new MessageReceiver().subscribe();

        new RedisMessageSender().sendToBungee(Arrays.asList("docker", "server", "register_open"));
    }

    public static BungeeApi getInstance() {
        return instance;
    }
}
