package dev.admitiendo.lootbox.commands;

import net.minecraft.util.com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import dev.admitiendo.lootbox.Lootbox;
import dev.admitiendo.lootbox.utils.Builder;
import dev.admitiendo.lootbox.utils.CC;
import org.bukkit.ChatColor;

public class LootboxCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if (!sender.hasPermission("lootbox.admin")) {
            sender.sendMessage(CC.Color("&cI'm sorry but you de not have permission to perform this command. Please contact a dev if you believe that this is in error."));
            return true;
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                if (args[1].equalsIgnoreCase("all")) {
                    if (Ints.tryParse(args[2]) == null) {
                        sender.sendMessage(CC.Color("&cThe number you entered is invalid."));
                        return true;
                    }

                    int amount = Ints.tryParse(args[2]);

                    ItemStack box = Builder.nameItem(Material.valueOf(Lootbox.getInstance().getConfig().getString("OPTIONS.LOOTBOX-ITEM.MATERIAL")), Lootbox.getInstance().getConfig().getString("OPTIONS.LOOTBOX-ITEM.DISPLAY-NAME"), (short) Lootbox.getInstance().getConfig().getInt("OPTIONS.LOOTBOX-ITEM.ITEM-META"), amount, Lootbox.getInstance().getConfig().getStringList("OPTIONS.LOOTBOX-ITEM.LORES"));

                    for (Player target : Bukkit.getOnlinePlayers()) {
                        if (target.getInventory().firstEmpty() == -1) {
                            target.getWorld().dropItemNaturally(target.getLocation(), box);
                            Bukkit.broadcast(CC.Color("&6&lLootboxAdmin &7? &c" + target.getName() + " &6obtained a lootbox. He had a full inventory and the lootbox dropped out of his inventory."), "lootboxes.command.lootbox");
                            target.sendMessage(CC.Color("&6&lJade &7? &7" + "You have received &6x" + amount + " Mystery Box" + (amount == 1 ? "" : "es") + "&7. But your inventory was full so, you dropped the Myster Box out of it."));
                        } else {
                            target.getInventory().addItem(box);
                            target.sendMessage(CC.Color("&6&lJade &7? &7" + "You have received &6x" + amount + " Mystery Box" + (amount == 1 ? "" : "es") + "&7."));
                        }
                    }
                } else {
                    if (Bukkit.getPlayer(args[1]) == null) {
                        sender.sendMessage(CC.Color("&6&lJade &7? &7A player with that name was not found."));
                        return true;
                    }

                    if (Ints.tryParse(args[2]) == null) {
                        sender.sendMessage(CC.Color("&cThe number you entered is invalid."));
                        return true;
                    }

                    Player target = Bukkit.getPlayer(args[1]);
                    int amount = Ints.tryParse(args[2]);

                    ItemStack box = Builder.nameItem(Material.valueOf(Lootbox.getInstance().getConfig().getString("OPTIONS.LOOTBOX-ITEM.MATERIAL")), Lootbox.getInstance().getConfig().getString("OPTIONS.LOOTBOX-ITEM.DISPLAY-NAME"), (short) Lootbox.getInstance().getConfig().getInt("OPTIONS.LOOTBOX-ITEM.ITEM-META"), amount, Lootbox.getInstance().getConfig().getStringList("OPTIONS.LOOTBOX-ITEM.LORES"));

                    if (target.getInventory().firstEmpty() == -1) {
                        target.getWorld().dropItemNaturally(target.getLocation(), box);
                        Bukkit.broadcast(CC.Color("&6&lLootboxAdmin &7? &c" + target.getName() + " &6obtained a lootbox. But his inventory was full so he dropped out of it."), "lootbox.admin");
                        target.sendMessage(CC.Color("&6&lJade &7? " + "You have received &6x" + amount + " Mystery Box" + (amount == 1 ? "" : "es") + "&7. But your inventory was full so, you dropped the Myster Box out of it."));
                    } else {
                        target.getInventory().addItem(box);
                        target.sendMessage(CC.Color("&6&lJade &7? " + "You have received &6x" + amount + " Mystery Box" + (amount == 1 ? "" : "es") + "&7."));
                    }
                }
                return true;
            }
        }

        sender.sendMessage(ChatColor.BLUE.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------");
        sender.sendMessage(CC.Color("&c/" + string + " give <player | all> <amount>"));
        sender.sendMessage(ChatColor.BLUE.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------");

        return false;
    }

}
