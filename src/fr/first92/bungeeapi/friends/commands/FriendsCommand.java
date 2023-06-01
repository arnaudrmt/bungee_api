package fr.first92.bungeeapi.friends.commands;

import fr.first92.bungeeapi.friends.schedulers.FriendInviteScheduler;
import fr.first92.sync.utils.PlayerFetcher;
import fr.first92.commons.Account;
import fr.first92.sync.data.providers.AccountProvider;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;

@SuppressWarnings("deprecation")
public class FriendsCommand extends Command implements TabExecutor {

    public static FriendsCommand instance;

    private final Map<UUID, List<UUID>> invites = new HashMap<>();

    public FriendsCommand() {
        super("friends", "", "f");
        instance = this;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer p = (ProxiedPlayer) sender;

            AccountProvider accountProvider = new AccountProvider(p.getUniqueId());
            Account account = accountProvider.getAccount();

            if (!(args.length > 0 && args.length < 3)) sendHelpMessage(sender);

            if (args.length == 1) {

                if (args[0].equalsIgnoreCase("list")) {

                    if(!account.getFriends().isEmpty()) {

                        StringBuilder s = new StringBuilder();

                        for(UUID friends : account.getFriends()) {

                            s.append(BungeeCord.getInstance().getPlayer(friends) != null ? "§a" : "§c")
                                    .append(new PlayerFetcher().getName(friends))
                                    .append(" §6∙  ");
                        }

                        sender.sendMessage("\n§7" + s + "\n");

                    } else sender.sendMessage("§cYou do not have any friendship.");

                } else if (BungeeCord.getInstance().getPlayer(args[0]) != null) {

                    ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[0]);

                    if(!target.equals(p)) {

                        if (!account.getFriends().contains(target.getUniqueId())) {

                            if (invites.entrySet().stream().noneMatch(rs -> rs.getValue().contains(p.getUniqueId()) &&
                                    rs.getValue().contains(target.getUniqueId()))) {

                                invites.put(UUID.randomUUID(), Arrays.asList(p.getUniqueId(), target.getUniqueId()));

                                new FriendInviteScheduler(60, p.getUniqueId(), target.getUniqueId());

                                p.sendMessage("§eYou sent a friendship request to §6" + target.getName());

                                TextComponent textComponent = new TextComponent("§cClick here to accept the invite!");

                                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        "/friends accept " + p.getName()));
                                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder("§7" + "Click to accept!").create()));

                                target.sendMessage("\n§6*§e-----§6*§e-----§6* §aFRIENDS §6*§e-----§6*§e-----§6*\n" +
                                        "§6" + target.getName() + "§e sent you a friendship request");
                                target.sendMessage(textComponent);
                                target.sendMessage("§6*§e-----§6*§e-----§6*§e---------§6*§e-----§6*§e-----§6*");

                            } else p.sendMessage("§aA friendship request is already pending between you too!");

                        } else p.sendMessage("§cYou're already friend with §6" + target.getName());

                    } else p.sendMessage("§cYou can't be friends with yourself (only in real life ;)");

                } else p.sendMessage("§cThis player is not online!");

            } else if (args.length == 2) {

                if (new PlayerFetcher().getUUID(args[1]) != null) {

                    UUID target = new PlayerFetcher().getUUID(args[1]);

                    AccountProvider targetAccountProvider = new AccountProvider(target);
                    Account targetAccount = targetAccountProvider.getAccount();

                    if (args[0].equalsIgnoreCase("remove")) {

                        if (account.getFriends().contains(target)) {

                            account.removeFriend(target);
                            targetAccount.removeFriend(p.getUniqueId());
                            p.sendMessage("§cYou are no longer friend with §6" + args[1]);

                            if(BungeeCord.getInstance().getPlayer(args[1]) != null)
                                BungeeCord.getInstance().getPlayer(args[1])
                                        .sendMessage("§6" + p.getName() + "§c ended your friendship.");

                        } else p.sendMessage("§cYou're not friend with this player!");

                    } else if(args[0].equalsIgnoreCase("accept")) {

                        if (invites.entrySet().stream().anyMatch(rs -> rs.getValue().get(0).equals(target) &&
                                rs.getValue().get(1).equals(p.getUniqueId()))) {

                            account.getFriends().add(target);
                            targetAccount.getFriends().add(p.getUniqueId());

                            p.sendMessage("§eYou started a friendship with §6" + new PlayerFetcher()
                                    .getName(target));

                            if(BungeeCord.getInstance().getPlayer(target) != null)
                                BungeeCord.getInstance().getPlayer(target).sendMessage("§eYou started a friendship with" +
                                        " §6" + p.getName());
                        }

                    } else if(args[0].equalsIgnoreCase("deny")) {

                        if (invites.entrySet().stream().anyMatch(rs -> rs.getValue().get(0).equals(target) &&
                                rs.getValue().get(1).equals(p.getUniqueId()))) {

                            account.getFriends().remove(target);
                            targetAccount.getFriends().remove(p.getUniqueId());

                            p.sendMessage("§cYou broke a friendship with §6" +
                                    new PlayerFetcher().getName(target));

                            if (BungeeCord.getInstance().getPlayer(target) != null)
                                BungeeCord.getInstance().getPlayer(target).sendMessage("§6" + p.getName() +
                                        "§c broke your friendship.");
                        }

                    } else sendHelpMessage(p);

                    targetAccountProvider.sendAccountToRedis(targetAccount);

                } else p.sendMessage("§cThis player does not exist!");

            } else sendHelpMessage(p);

            accountProvider.sendAccountToRedis(account);


        } else sender.sendMessage("This command can only be executed by a living creature!");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

        List<String> l = new ArrayList<>();

        ProxiedPlayer p = (ProxiedPlayer) sender;

        if(args.length == 1) {

            l.add("remove"); l.add("list"); l.add("accept"); l.add("deny");
            BungeeCord.getInstance().getPlayers().stream().filter(Objects::nonNull).forEach(rs -> l.add(rs.getName()));

        } else if(args.length == 2) {

            if(!new AccountProvider(p.getUniqueId()).getAccount().getFriends().isEmpty()) {

                Account account = new AccountProvider(p.getUniqueId()).getAccount();

                if(args[0].equalsIgnoreCase("remove")) {

                    account.getFriends().forEach(rs -> l.add(new PlayerFetcher().getName(rs)));
                } else if(args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny")) {

                    invites.entrySet().stream().filter(rs -> rs.getValue().contains(p.getUniqueId())).forEach(rs -> {
                        List<String> ls = new ArrayList<>();
                        rs.getValue().forEach(rs2 -> ls.add(new PlayerFetcher().getName(rs2)));
                        ls.remove(p.getName());
                        l.addAll(ls);
                    });
                }
            }
        }

        return l;
    }

    public Map<UUID, List<UUID>> getInvites() {
        return invites;
    }

    public void sendHelpMessage(CommandSender p) {

        p.sendMessage("\n§b∙  §eHELP - FRIENDS §b∙\n\n" +

                "  §6∙ §b/friends <player>§r: §eSend a friendship invite\n\n" +
                "  §6∙ §b/friends <remove> <player>§r: §eEnd a friendship\n\n" +
                "  §6∙ §b/friends <accept> <player>§r: §eStart a friendship\n\n" +
                "  §6∙ §b/friends <deny> <player>§r: §eBreak a friendship\n\n" +
                "  §6∙ §b/friends <list>§r: §eDisplay the list of your friendships\n\n");
    }
}
