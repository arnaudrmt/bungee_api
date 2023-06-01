package fr.first92.bungeeapi.party.commands;

import fr.first92.bungeeapi.party.schedulers.PartyInviteScheduler;
import fr.first92.sync.utils.PlayerFetcher;
import fr.first92.commons.PartyTemplate;
import fr.first92.sync.data.providers.PartyProvider;
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
public class PartyCommand extends Command implements TabExecutor {

    public static PartyCommand instance;

    public PartyCommand() {
        super("party", "", "p");
        instance = this;
    }

    private final Map<UUID, List<UUID>> invites = new HashMap<>();

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof ProxiedPlayer) {

            ProxiedPlayer p = (ProxiedPlayer)sender;

            if(!(args.length > 0 && args.length < 3)) sendHelpMessage(sender);

            if(args.length == 2) {

                if(args[0].equalsIgnoreCase("join")) {

                    System.out.println(invites);

                    if (new PlayerFetcher().getUUID(args[1]) != null) {

                        UUID target = new PlayerFetcher().getUUID(args[1]);

                        PartyProvider partyProvider = new PartyProvider(p);
                        PartyProvider partyProviderTarget = new PartyProvider(target);

                        if(PartyCommand.instance.getInvites().entrySet().stream().anyMatch(rs -> rs.getValue().get(0)
                                .equals(target) && rs.getValue().get(1).equals(p.getUniqueId()))) {

                            if (partyProvider.getParty() == null) {

                                if(BungeeCord.getInstance().getPlayer(target) != null) {

                                    p.sendMessage("§eYou joined §6" + p.getName() + "'s§e party!");
                                }

                                System.out.println(invites);

                                Map.Entry<UUID, List<UUID>> uuidListEntry = invites.entrySet().stream().filter(rs -> rs.getValue().contains(p.getUniqueId())
                                        && rs.getValue().contains(target)).findFirst().get();

                                PartyCommand.instance.getInvites().remove(uuidListEntry.getKey());

                                Map<UUID, Boolean> playerList = new HashMap<>();

                                if(partyProviderTarget.getParty() == null) playerList.put(target, true);
                                else playerList.putAll(partyProviderTarget.getParty().getPlayers());

                                playerList.entrySet().stream().filter(rs ->
                                        BungeeCord.getInstance().getPlayer(rs.getKey()) != null).forEach(rs ->
                                        BungeeCord.getInstance().getPlayer(rs.getKey()).sendMessage(
                                                "§6" + p.getName() + "§e joined the party!"));

                                playerList.put(p.getUniqueId(), true);

                                partyProviderTarget.sendPartyToRedis(new PartyTemplate(target, playerList));

                            } else {

                                TextComponent textComponent = new TextComponent("§cClick here to leave the party!");

                                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party leave"));
                                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder("§7" + "Click to join!").create()));

                                p.sendMessage("\n§6*§e----§6*§e-----§6* §dPARTY §6*§e-----§6*§e-----§6*\n" +
                                        "§eYou are in §6" + new PlayerFetcher().getName(
                                                partyProvider.getParty().getOwner()) + "'s party");
                                p.sendMessage(textComponent);
                                p.sendMessage("§6*§e----§6*§e-----§6*§e-------§6*§e-----§6*§e-----§6*");
                            }
                        } else p.sendMessage("§cYou didn't received any invite from this player!");
                    } else p.sendMessage("§cThis player is not online!");

                } else if(args[0].equalsIgnoreCase("remove")) {

                    if (new PartyProvider(p).getParty() != null) {

                        PartyProvider partyProvider = new PartyProvider(p);
                        PartyTemplate partyTemplate = partyProvider.getParty();

                        if(partyTemplate.getOwner().equals(p.getUniqueId())) {

                            if (new PlayerFetcher().getUUID(args[1]) != null) {

                                UUID target = new PlayerFetcher().getUUID(args[1]);

                                if (partyTemplate.getPlayers().containsKey(target)) {

                                    partyTemplate.getPlayers().remove(target);

                                    if(BungeeCord.getInstance().getPlayer(target) != null)
                                        BungeeCord.getInstance().getPlayer(target).sendMessage("§cYou've been kicked" +
                                                " from the party.");

                                    p.sendMessage("§cYou kicked §6" + args[1] + "§c from the party");

                                    partyProvider.sendPartyToRedis(partyTemplate);

                                    if(partyTemplate.getPlayers().size() <= 1) {

                                        partyTemplate.getPlayers().entrySet().stream().filter(rs ->
                                                BungeeCord.getInstance().getPlayer(rs.getKey()) != null)
                                                .forEach(rs -> BungeeCord.getInstance().getPlayer(rs.getKey()).sendMessage(
                                                        "§cYou're the only one left in this party, " +
                                                                "therefore the party has been disbanded"
                                                ));

                                        partyProvider.deleteParty();
                                    }

                                } else p.sendMessage("§cThis player is not in your party!");

                            } else p.sendMessage("§cThis player is not online");

                        } else p.sendMessage("§cYou don't have access to this command!");

                    } else p.sendMessage("§cYou are not in a party!");

                } else if(args[0].equalsIgnoreCase("promote")) {

                    PartyProvider partyProvider = new PartyProvider(p);

                    if(BungeeCord.getInstance().getPlayer(args[1]) != null) {

                        ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[1]);

                        if (partyProvider.getParty() != null) {

                            if(partyProvider.getParty().getOwner().equals(p.getUniqueId())) {
                                if (partyProvider.getParty().getPlayers().containsKey(target.getUniqueId())) {

                                    PartyTemplate partyTemplate = partyProvider.getParty();
                                    partyTemplate.setOwner(target.getUniqueId());

                                    p.sendMessage("§eYou promoted §6" + target.getName() + "§e to party leader!");
                                    target.sendMessage("§eYou've promoted to party leader!");

                                    partyProvider.sendPartyToRedis(partyTemplate);
                                }
                            } else sender.sendMessage("§cYou can't execute this command!");

                        } else p.sendMessage("§cYou can't do this!");

                    } else p.sendMessage("§cThis player is not online!");

                } else if(args[0].equalsIgnoreCase("follow")) {

                    if (new PartyProvider(p).getParty() != null && !new PartyProvider(p).getParty().getOwner()
                            .equals(p.getUniqueId())) {

                        PartyProvider partyProvider = new PartyProvider(p);
                        PartyTemplate partyTemplate = partyProvider.getParty();

                        partyTemplate.getPlayers().replace(p.getUniqueId(), !args[1].equalsIgnoreCase("off"));
                        p.sendMessage("§eYou're " + (args[1].equalsIgnoreCase("off") ? "§cnot §6" : "")
                                + "§egoing to be teleported with the owner " +
                                (args[1].equalsIgnoreCase("off") ? "§canymore" : "§6from now on") + "§e!");

                        new PartyProvider(new PartyProvider(p).getUUIDFromRedis()).sendPartyToRedis(partyTemplate);

                    } else p.sendMessage("§cYou can't execute this command!");

                } else sendHelpMessage(p);

            } else if (args.length == 1) {

                if(args[0].equalsIgnoreCase("leave")) {

                    if(new PartyProvider(p).getParty() != null) {

                        PartyProvider partyProvider = new PartyProvider(p);
                        PartyTemplate partyTemplate = partyProvider.getParty();

                        if(!(partyTemplate.getPlayers().keySet().stream().filter(rs ->
                                BungeeCord.getInstance().getPlayer(rs) != null).count() - 1 <= 1)) {

                            if(partyTemplate.getOwner().equals(p.getUniqueId())) {

                                UUID key = partyTemplate.getPlayers().entrySet().stream().filter(rs ->
                                        BungeeCord.getInstance().getPlayer(rs.getKey()) != null &&
                                                !BungeeCord.getInstance().getPlayer(rs.getKey()).equals(p)).iterator().next().getKey();

                                partyTemplate.setOwner(key);

                                BungeeCord.getInstance().getPlayer(key).sendMessage("§eThe party owner left, you're from now on the new leader!");

                            } else partyTemplate.getPlayers().keySet().forEach(rs -> BungeeCord.getInstance().getPlayer(rs).sendMessage("§6" + p.getName() + "§c left the party"));
                        } else {

                            BungeeCord.getInstance().getPlayer(partyTemplate.getOwner()).sendMessage("§cYou're the only one left in this party, " +
                                    "therefore the party has been disbanded");

                            new PartyProvider(new PartyProvider(p).getUUIDFromRedis()).deleteParty();

                            return;
                        }

                        partyTemplate.getPlayers().remove(p.getUniqueId());

                        new PartyProvider(new PartyProvider(p).getUUIDFromRedis()).sendPartyToRedis(partyTemplate);

                    } else p.sendMessage("§cYou are not in a party!");

                } else if(args[0].equalsIgnoreCase("warp")) {

                    if(new PartyProvider(p).getParty() != null) {

                        PartyProvider partyProvider = new PartyProvider(p);
                        PartyTemplate partyTemplate = partyProvider.getParty();

                        if(partyTemplate.getOwner().equals(p.getUniqueId())) {
                            partyTemplate.getPlayers().keySet().stream()
                                    .filter(rs -> BungeeCord.getInstance().getPlayer(rs) != null)
                                    .filter(rs -> !BungeeCord.getInstance().getPlayer(rs).getServer().equals(p.getServer()))
                                    .forEach(rs -> {
                                        BungeeCord.getInstance().getPlayer(rs).connect(p.getServer().getInfo());
                                        BungeeCord.getInstance().getPlayer(rs).sendMessage("§eYou've been warped!");
                                    });

                        } else p.sendMessage("§cYou can't execute this command!");

                    } else p.sendMessage("§cYou can't execute this action!");

                } else if(args[0].equalsIgnoreCase("list")) {

                    PartyProvider partyProvider = new PartyProvider(p);

                    if(partyProvider.getParty() != null) {

                        PartyTemplate partyTemplate = partyProvider.getParty();

                        StringBuilder s = new StringBuilder();

                        for(UUID players : partyTemplate.getPlayers().keySet()) {

                            s.append(BungeeCord.getInstance().getPlayer(players) != null ? "§a" : "§c")
                                    .append(new PlayerFetcher().getName(players))
                                    .append(" §6∙  ");
                        }

                        sender.sendMessage("\n§7" + s + "\n");

                    } else sender.sendMessage("§cYou do not have any party");

                } else if(BungeeCord.getInstance().getPlayer(args[0]) != null) {

                    ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[0]);

                    if(target != p) {

                        if ((new PartyProvider(p).getParty() != null && new PartyProvider(p)
                                .getParty().getOwner().equals(p.getUniqueId()) &&
                                !new PartyProvider(p).getParty().getPlayers()
                                        .containsKey(BungeeCord.getInstance().getPlayer(args[0]).getUniqueId()))
                                || new PartyProvider(p).getParty() == null) {


                            TextComponent textComponent = new TextComponent("§cClick here to accept the invite!");

                            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + p.getName()));
                            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7" +
                                    "Click to join!")
                                    .create()));

                            target.sendMessage("\n§6*§e-----§6*§e-----§6* §dPARTY §6*§e-----§6*§e-----§6*\n" +
                                    "§6" + target.getName() + "§e sent you a party invite");
                            target.sendMessage(textComponent);
                            target.sendMessage("§6*§e-----§6*§e-----§6*§e-------§6*§e-----§6*§e-----§6*");

                            sender.sendMessage("\n§eYou've sent a party's invite to §6" + target.getName());

                            invites.put(UUID.randomUUID(), Arrays.asList(p.getUniqueId(), target.getUniqueId()));

                            new PartyInviteScheduler(60, p.getUniqueId(), target.getUniqueId());

                        } else p.sendMessage("§cYou can't invite this player to the party");

                    } else p.sendMessage("§cYou can't invite yourself to your party");

                } else sendHelpMessage(p);

            } else sendHelpMessage(p);

        } else sender.sendMessage("Only a living creature can execute this command!");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

        List<String> l = new ArrayList<>();

        ProxiedPlayer p = (ProxiedPlayer) sender;

        if(args.length == 1) {

            l.add("leave"); l.add("warp"); l.add("list");
            BungeeCord.getInstance().getPlayers().stream().filter(Objects::nonNull).forEach(rs -> l.add(rs.getName()));

        } else if(args.length == 2) {

            if(new PartyProvider(p).getParty() != null) {

                PartyTemplate party = new PartyProvider(p).getParty();

                if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("promote")) {
                    party.getPlayers().forEach((key, value) -> l.add(new PlayerFetcher().getName(key)));

                } else if (args[0].equalsIgnoreCase("follow")) {
                    l.add("on");
                    l.add("off");
                }

            } else if(args[0].equalsIgnoreCase("join")) {

                invites.entrySet().stream().filter(rs -> rs.getValue().contains(p.getUniqueId())).forEach(rs -> {
                    List<String> ls = new ArrayList<>();
                    rs.getValue().forEach(rs2 -> ls.add(new PlayerFetcher().getName(rs2)));
                    ls.remove(p.getName());
                    l.addAll(ls);
                });
            }
        }

        return l;
    }

    public Map<UUID, List<UUID>> getInvites() {
        return invites;
    }

    public void sendHelpMessage(CommandSender p) {

        p.sendMessage("\n§b∙  §eHELP - PARTY §b∙\n\n" +

                "  §6∙ §b/party <player>§r: §eSend a party invite\n\n" +
                "  §6∙ §b/party <remove> <player>§r: §eRemove a player from your party\n\n" +
                "  §6∙ §b/party <join> <player>§r: §eJoin a player's party\n\n" +
                "  §6∙ §b/party <leave>§r: §eQuit the party\n\n" +
                "  §6∙ §b/party <list>§r: §eDisplay the list of all players\n\n" +
                "  §6∙ §b/party <promote> <player>§r: §ePromote a player to leader\n\n" +
                "  §6∙ §b/party <warp>§r: §eWarp every players to your server\n\n" +
                "  §6∙ §b/party <follow> <on/off>§r: §eFollow the party leader\n\n");
    }
}
