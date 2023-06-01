package fr.first92.bungeeapi.whitelist.commands;

import fr.first92.sync.utils.PlayerFetcher;
import fr.first92.commons.RankEnum;
import fr.first92.commons.Whitelist;
import fr.first92.sync.data.providers.WhitelistProvider;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;

@SuppressWarnings("deprecation")
public class WhitelistCommand extends Command implements TabExecutor {

    public WhitelistCommand() {
        super("whitelist", "", "w");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(sender.hasPermission("octana.whitelist") || sender.hasPermission("octana.*")) {

            WhitelistProvider whitelistProvider = new WhitelistProvider(args[0]);

            if(args.length > 1 && args.length < 4) {

                if (args.length == 2) {

                    Whitelist whitelist = whitelistProvider.getWhitelist();

                    whitelist.setWhitelisted(args[1].equalsIgnoreCase("on"));

                    whitelistProvider.sendWhitelistToRedis(whitelist);

                    whitelist.setWhitelisted(args[1].equalsIgnoreCase("on"));
                    sender.sendMessage("§eThe §6" + args[0] + "s§e are now " +
                            (args[1].equalsIgnoreCase("on") ? "§cunder maintenance!" : "§6aintenance free!"));

                } else {

                    if (new WhitelistProvider(args[0]).exist()) {

                        Whitelist whitelist = whitelistProvider.getWhitelist();

                        if (getType(args[1]) != null) {

                            Object result = getType(args[2]);

                            if (args[1].equalsIgnoreCase("add")) {

                                if (result instanceof UUID) {

                                    if (!whitelist.getPlayerWhitelisted().contains((UUID) result)) {

                                        whitelist.addPlayer((UUID) result);
                                        sender.sendMessage(
                                                "§6" + new PlayerFetcher().getName((UUID) result) +
                                                        "§e is now whitelisted");

                                    } else sender.sendMessage("§cThis player is already whitelisted.");

                                } else {

                                    if (!whitelist.getRankWhitelisted().contains((RankEnum) result)) {

                                        whitelist.addRank((RankEnum) result);
                                        sender.sendMessage(
                                                "§6" + ((RankEnum) result).getName().substring(0, 1).toUpperCase() +
                                                        ((RankEnum) result).getName().substring(1) +
                                                        " §eis now whitelisted");

                                    } else sender.sendMessage("§cThis rank is already whitelisted.");
                                }

                            } else if (args[1].equalsIgnoreCase("remove")) {

                                if (result instanceof UUID) {

                                    if (whitelist.getPlayerWhitelisted().contains((UUID) result)) {

                                        whitelist.removePlayer((UUID) result);
                                        sender.sendMessage(
                                                "§6" + new PlayerFetcher().getName((UUID) result) +
                                                        "§e is no longer whitelisted");

                                    } else sender.sendMessage("§cThis player is not whitelisted.");

                                } else {

                                    if (whitelist.getRankWhitelisted().contains((RankEnum) result)) {

                                        whitelist.removeRank((RankEnum) result);
                                        sender.sendMessage(
                                                "§6" + ((RankEnum) result).getName().substring(0, 1).toUpperCase() +
                                                        ((RankEnum) result).getName().substring(1) +
                                                        "§e is no longer whitelisted");

                                    } else sender.sendMessage("§cThis rank is not whitelisted.");
                                }

                            } else if (args[1].equalsIgnoreCase("list")) {

                                if (args[2].equalsIgnoreCase("players")) {

                                    List<String> l = new ArrayList<>();

                                    whitelist.getPlayerWhitelisted().forEach(rs -> l.add(new PlayerFetcher().getName(rs)));

                                    sender.sendMessage("\n§7" + l.toString()
                                            .replace("[", "").replace("]", "")
                                            .replace(",", " §6∙ §7") + "\n");

                                } else if (args[2].equalsIgnoreCase("ranks"))

                                    sender.sendMessage("\n§7" + whitelist.getRankWhitelisted().toString()
                                            .replace("[", "").replace("]", "")
                                            .replace(",", " §6∙ §7") + "\n");

                                else sendHelpMessage(sender);

                            } else sendHelpMessage(sender);

                            whitelistProvider.sendWhitelistToRedis(whitelist);

                        } else sender.sendMessage("§cNeither of these object are players or ranks.");

                    } else sender.sendMessage("§cThis type of server does not exist yet!");

                }

            } else sendHelpMessage(sender);

        } else sender.sendMessage("§cYou do not have the permission to execute ths command");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

        List<String> l = new ArrayList<>();

        if(args.length == 1) {

            new WhitelistProvider("").getAllWhitelists().forEach(rs -> l.add(rs.getServer()));

        } else if(args.length == 2){

            l.add("on"); l.add("off");
            l.add("add"); l.add("remove");
            l.add("list");

        } else if(args.length == 3){

            Arrays.stream(RankEnum.values()).forEach(rs -> l.add(rs.getName()));
        }

        return l;
    }

    public Object getType(String s) {

        if(Arrays.stream(RankEnum.values()).anyMatch(rs -> rs.getName().equalsIgnoreCase(s)))
            return Arrays.stream(RankEnum.values()).filter(rs -> rs.getName().equalsIgnoreCase(s)).findFirst().get();

        else if(!Objects.equals(new PlayerFetcher().getUUID(s), null))
            return new PlayerFetcher().getUUID(s);

        return null;
    }

    public void sendHelpMessage(CommandSender p) {

        p.sendMessage("\n§b∙  §eHELP - WHITELIST §b∙               \n\n" +

                "  §6∙ §b/whitelist <server> <on/off>§r: §eManage the maintenance status\n\n" +
                "  §6∙ §b/whitelist <server> <add/remove> <player/rank>§r: §eManage the access of the maintenance\n\n" +
                "  §6∙ §b/whitelist <server> <list> <players/ranks>§r: §eSee who has access to the servers\n\n");
    }
}
