package fr.first92.bungeeapi.registers;

import fr.first92.bungeeapi.BungeeApi;
import fr.first92.bungeeapi.commands.HubCommand;
import fr.first92.bungeeapi.dms.ReplyCommand;
import fr.first92.bungeeapi.dms.SendMessageCommand;
import fr.first92.bungeeapi.events.Events;
import fr.first92.bungeeapi.friends.commands.FriendsCommand;
import fr.first92.bungeeapi.friends.events.FriendsJoinEvent;
import fr.first92.bungeeapi.friends.events.FriendsQuitEvent;
import fr.first92.bungeeapi.party.commands.PartyCommand;
import fr.first92.bungeeapi.party.events.PartyFollowEvent;
import fr.first92.bungeeapi.party.events.PartyJoinEvent;
import fr.first92.bungeeapi.party.events.PartyQuitEvent;
import fr.first92.bungeeapi.whitelist.commands.WhitelistCommand;
import fr.first92.bungeeapi.whitelist.events.WhitelistEvent;
import net.md_5.bungee.api.plugin.PluginManager;

public class RegistersHandler {

    BungeeApi api = BungeeApi.getInstance();
    PluginManager pm = api.getProxy().getPluginManager();

    public void registerCommands() {

        pm.registerCommand(api, new HubCommand());
        pm.registerCommand(api, new SendMessageCommand());
        pm.registerCommand(api, new ReplyCommand());
        pm.registerCommand(api, new FriendsCommand());
        pm.registerCommand(api, new PartyCommand());
        pm.registerCommand(api, new WhitelistCommand());
    }

    public void registerEvents() {

        pm.registerListener(api, new Events());
        pm.registerListener(api, new FriendsJoinEvent());
        pm.registerListener(api, new FriendsQuitEvent());
        pm.registerListener(api, new PartyJoinEvent());
        pm.registerListener(api, new PartyQuitEvent());
        pm.registerListener(api, new PartyFollowEvent());
        pm.registerListener(api, new WhitelistEvent());
    }
}
