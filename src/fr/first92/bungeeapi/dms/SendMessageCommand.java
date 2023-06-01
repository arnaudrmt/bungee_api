package fr.first92.bungeeapi.dms;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;

public class SendMessageCommand extends Command implements TabExecutor {

    public static SendMessageCommand instance;

    public Map<ProxiedPlayer, ProxiedPlayer> oldMessages = new HashMap<>();

    public SendMessageCommand() {
        super("message", "", "msg");
        instance = this;
    }


    @Override
    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof ProxiedPlayer) {

            ProxiedPlayer p = (ProxiedPlayer) sender;

            if(args.length > 1) {

                if(BungeeCord.getInstance().getPlayer(args[0]) != null && BungeeCord.getInstance().getPlayer(args[0]) != p) {

                    ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[0]);

                    StringBuilder message = new StringBuilder();

                    for(int i = 1; i < args.length; i++) {
                        message.append(args[i]).append(" ");
                    }

                    p.sendMessage("§dTo " + target.getName() + ": §7" + message);

                    target.sendMessage("§dFrom " + p.getName() + ": §7" + message);

                    oldMessages.put(target, p);

                } else p.sendMessage("§cThis player is not online!");

            } else sendHelpMessage(p);

        } else sender.sendMessage("Only a living creature can execute this command!");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

        List<String> l = new ArrayList<>();

        if(args.length == 1) {

            BungeeCord.getInstance().getPlayers().forEach(rs -> l.add(rs.getName()));
        }

        return l;
    }

    public void sendHelpMessage(CommandSender p) {

        p.sendMessage("\n§b∙  §eHELP - MESSAGES §b∙\n\n" +

                "  §6∙ §b/message <player> <message>§r: §eSend a message\n\n" +
                "  §6∙ §b/reply <message>§r: §eReply to the last person who sent you a message\n\n");
    }
}
