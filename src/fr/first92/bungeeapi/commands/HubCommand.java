package fr.first92.bungeeapi.commands;

import fr.first92.bungeeapi.BungeeApi;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class HubCommand extends Command {

    public HubCommand() {
        super("hub", "", "l", "lobby", "h");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof ProxiedPlayer) {

            ProxiedPlayer p = (ProxiedPlayer) sender;

            if(args.length == 0) p.connect(BungeeApi.getInstance().getProxy().getServerInfo("Default"));

        } else sender.sendMessage("Only a living creature can execute this command!");
    }
}
