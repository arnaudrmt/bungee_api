package fr.first92.bungeeapi.dms;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReplyCommand extends Command {

    public ReplyCommand() {
        super("reply", "", "r");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof ProxiedPlayer) {

            ProxiedPlayer p = (ProxiedPlayer) sender;

            if(args.length > 0) {

                if(SendMessageCommand.instance.oldMessages.containsKey(p) && SendMessageCommand.instance.oldMessages.get(p)
                        .isConnected()) {

                    ProxiedPlayer target = SendMessageCommand.instance.oldMessages.get(p);

                    StringBuilder message = new StringBuilder();

                    for(String msg : args) {
                        message.append(msg).append(" ");
                    }

                    p.sendMessage("§dTo " + target.getName() + ": §7" + message);

                    target.sendMessage("§dFrom " + p.getName() + ": §7" + message);

                } else p.sendMessage("§cThis player is not online!");

            } else sendHelpMessage(p);

        } else sender.sendMessage("Only a living creature can execute this command!");
    }

    public void sendHelpMessage(CommandSender p) {

        p.sendMessage("\n§b∙  §eHELP - MESSAGES §b∙\n\n" +

                "  §6∙ §b/message <player> <message>§r: §eSend a message\n\n" +
                "  §6∙ §b/reply <message>§r: §eReply to the last person who sent you a message\n\n");
    }
}
