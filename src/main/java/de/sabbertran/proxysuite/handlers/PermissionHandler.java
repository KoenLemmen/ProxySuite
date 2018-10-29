package de.sabbertran.proxysuite.handlers;

import de.sabbertran.proxysuite.ProxySuite;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class PermissionHandler {

    private final ProxySuite main;
    private final List<String> availablePermissions;
    private final HashMap<String, List<String>> permissions;

    public PermissionHandler(ProxySuite main) {
        this.main = main;
        permissions = new HashMap<>();
        availablePermissions = new ArrayList<>();
    }

    public void updatePermissions() {
        main.getProxy().getPlayers().forEach(p -> updatePermissions(p));
    }

    public void updatePermissions(ProxiedPlayer p) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("GetPermissions");
            out.writeUTF(p.getName());
            for (String perm : availablePermissions)
                out.writeUTF(perm);
        } catch (IOException e) {
            main.getLogger().log(Level.SEVERE, null, e);
        }
        p.getServer().sendData("proxysuite:channel", b.toByteArray());
    }

    public void readAvailablePermissionsFromFile() {
        try {
            BufferedReader read = new BufferedReader(new InputStreamReader(main.getClass().getResourceAsStream
                    ("/availablePermissions.yml")));
            String line;
            while ((line = read.readLine()) != null) {
                line = line.trim();
                if (!line.trim().equals("") && !line.startsWith("#"))
                    availablePermissions.add(line.trim());
            }
        } catch (IOException e) {
            main.getLogger().log(Level.SEVERE, null, e);
        }
    }

    public void resetPermissions() {
        main.getProxy().getPlayers().forEach(p -> resetPermissions(p));
    }

    public void resetPermissions(CommandSender player) {
        permissions.remove(player.getName());
    }

    public void resetPermissions(String player) {
        permissions.remove(player);
    }

    public boolean hasPermission(CommandSender sender, String permission) {
        return hasPermission(sender, permission, false);
    }

    public boolean hasPermission(CommandSender sender, String permission, boolean ignoreSternchen) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if (permissions.containsKey(p.getName())) {
                if (permissions.get(p.getName()).contains(permission.toLowerCase()) || (!ignoreSternchen && permissions.get(p.getName()).contains("*")) || (!ignoreSternchen && permissions.get(p.getName()).contains(permission.toLowerCase() + ".*")))
                    return true;
                else if (!ignoreSternchen) {
                    String check = "";
                    for (String s : permission.toLowerCase().split("\\.")) {
                        check = check + s + ".";
                        if (permissions.get(p.getName()).contains(check + "*")) {
                            main.getLogger().log(Level.INFO, "{0} has ''{1}*''", new Object[]{sender.getName(), check});
                            return true;
                        }
                    }
                }
            }
        } else
            return true;
        return false;
    }

    public boolean hasPermission(String player, String permission) {
        if (permissions.containsKey(player)) {
            if (permissions.get(player).contains(permission.toLowerCase()) || permissions.get(player)
                    .contains("*"))
                return true;
            else {
                String check = "";
                for (String s : permission.toLowerCase().split("\\.")) {
                    check = check + s + ".";
                    if (permissions.get(player).contains(check + "*"))
                        return true;
                }
            }
        }
        return false;
    }

    public void sendMissingPermissionInfo(CommandSender sender) {
        main.getMessageHandler().sendMessage(sender, main.getMessageHandler().getMessage("command.nopermission"));
    }

    public HashMap<String, List<String>> getPermissions() {
        return permissions;
    }

    public List<String> getAvailablePermissions() {
        return availablePermissions;
    }
}
