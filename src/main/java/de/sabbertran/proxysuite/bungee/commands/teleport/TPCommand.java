package de.sabbertran.proxysuite.bungee.commands.teleport;

import de.sabbertran.proxysuite.bungee.ProxySuite;
import de.sabbertran.proxysuite.bungee.utils.Location;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TPCommand extends Command {

    private final ProxySuite main;

    public TPCommand(ProxySuite main) {
        super("tp");
        this.main = main;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            switch (args.length) {
                case 1:
                    if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.tp")) {
                        if (sender instanceof ProxiedPlayer) {
                            ProxiedPlayer p = (ProxiedPlayer) sender;
                            int remainingCooldown = main.getTeleportHandler().getRemainingCooldown(p);
                            boolean ignoreCooldown = main.getTeleportHandler().canIgnoreCooldown(sender);
                            if (remainingCooldown == 0 || ignoreCooldown) {
                                ProxiedPlayer to = main.getPlayerHandler().getPlayer(args[0], sender, true);
                                if (to != null) {
                                    main.getTeleportHandler().teleportToPlayer(p, to, ignoreCooldown, true);
                                } else {
                                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                ("command.player.notonline").replace("%player%", args[0]));
                                }
                            } else {
                                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("teleport" +
                                        ".cooldown").replace("%cooldown%", "" + remainingCooldown));
                            }
                        } else {
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
                        }
                    } else {
                        main.getPermissionHandler().sendMissingPermissionInfo(sender);
                    }   break;
                case 2:
                    try {
                        if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.tp.coordinates")) {
                            int x = Integer.parseInt(args[0]);
                            int z = Integer.parseInt(args[1]);
                            if (sender instanceof ProxiedPlayer) {
                                ProxiedPlayer p = (ProxiedPlayer) sender;
                                int remainingCooldown = main.getTeleportHandler().getRemainingCooldown(p);
                                boolean ignoreCooldown = main.getTeleportHandler().canIgnoreCooldown(sender);
                                if (remainingCooldown == 0 || ignoreCooldown) {
                                    Location loc = new Location(p.getServer().getInfo(), "CURRENT", x, Double.MAX_VALUE, z);
                                    main.getTeleportHandler().teleportToLocation(p, loc, ignoreCooldown, false, true);
                                } else {
                                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("teleport" +
                                            ".cooldown").replace("%cooldown%", "" + remainingCooldown));
                                }
                            } else {
                                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
                            }
                        } else {
                            main.getPermissionHandler().sendMissingPermissionInfo(sender);
                        }
                    } catch (NumberFormatException ex) {
                        if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.tp.others")) {
                            ProxiedPlayer p1 = main.getPlayerHandler().getPlayer(args[0], sender, true);
                            ProxiedPlayer p2 = main.getPlayerHandler().getPlayer(args[1], sender, true);
                            if (p1 != null) {
                                if (p2 != null) {
                                    boolean ignoreCooldown = main.getTeleportHandler().canIgnoreCooldown(sender);
                                    main.getTeleportHandler().teleportToPlayer(p1, p2, ignoreCooldown, true);
                                } else {
                                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                                ("command.player.notonline").replace("%player%", args[1]));
                                }
                            } else {
                                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage
                                            ("command.player.notonline").replace("%player%", args[0]));
                            }
                        } else {
                            main.getPermissionHandler().sendMissingPermissionInfo(sender);
                        }
                    }   break;
                case 3:
                    if (main.getPermissionHandler().hasPermission(sender, "proxysuite.commands.tp.coordinates")) {
                        try {
                            int x = Integer.parseInt(args[0]);
                            int y = Integer.parseInt(args[1]);
                            int z = Integer.parseInt(args[2]);
                            if (sender instanceof ProxiedPlayer) {
                                ProxiedPlayer p = (ProxiedPlayer) sender;
                                int remainingCooldown = main.getTeleportHandler().getRemainingCooldown(p);
                                boolean ignoreCooldown = main.getTeleportHandler().canIgnoreCooldown(sender);
                                if (remainingCooldown == 0 || ignoreCooldown) {
                                    Location loc = new Location(p.getServer().getInfo(), "CURRENT", x, y, z);
                                    main.getTeleportHandler().teleportToLocation(p, loc, ignoreCooldown, false, true);
                                } else {
                                    main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("teleport" +
                                            ".cooldown").replace("%cooldown%", "" + remainingCooldown));
                                }
                            } else {
                                main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.noplayer"));
                            }
                        } catch (NumberFormatException ex) {
                            main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("teleport" +
                                    ".coordinates.notvalid"));
                        }
                    } else {
                        main.getPermissionHandler().sendMissingPermissionInfo(sender);
                    }   break;
                default:
                    main.getCommandHandler().sendUsage(sender, this);
                    break;
            }
        });
    }
}
