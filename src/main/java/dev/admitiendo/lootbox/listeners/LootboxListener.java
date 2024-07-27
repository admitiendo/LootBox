package dev.admitiendo.lootbox.listeners;

import net.frozenorb.qlib.util.CentredMessage;
import net.minecraft.util.com.google.common.primitives.Ints;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import dev.admitiendo.lootbox.Lootbox;
import dev.admitiendo.lootbox.utils.Builder;
import dev.admitiendo.lootbox.utils.CC;

import java.util.*;

public class LootboxListener implements Listener {

    public static HashMap<UUID, Integer> OPENED_BOXES = new HashMap<>();
    public static ArrayList<String> REGULAR_ITEMS = new ArrayList<>();
    public static ArrayList<String> FINAL_ITEMS = new ArrayList<>();

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }
        if (event.getItem().getType() == null) {
            return;
        }
        if (event.getItem().getType() != Material.valueOf(Lootbox.getInstance().getConfig().getString("OPTIONS.LOOTBOX-ITEM.MATERIAL"))) {
            return;
        }
        if (event.getItem().getItemMeta() == null) {
            return;
        }
        if (event.getItem().getItemMeta().getDisplayName() == null) {
            return;
        }
        if (!event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(CC.Color(Lootbox.getInstance().getConfig().getString("OPTIONS.LOOTBOX-ITEM.DISPLAY-NAME")))) {
            return;
        }
        event.setCancelled(true); // Prevent placing the block

        setupChances();

        Inventory inv = Bukkit.createInventory(null, 54, CC.Color("&6&lMystery Box"));

        ItemStack chest = Builder.nameItem(Material.CHEST, CC.Color("&6&l???"), (short) 0, 1, Arrays.asList("&7Click here to open the &6Mystery Box&7."));
        ItemStack enderchest = Builder.nameItem(Material.ENDER_CHEST, CC.Color("&6&l"), (short) 0, 1, Arrays.asList("&7Click here to open the &6Mystery Box&7."));
        ItemStack spacer = Builder.nameItem(Material.STAINED_GLASS_PANE, CC.Color(" "), (short) 15, 1, Arrays.asList());

        for (int i = 0; i < 54; i++) {
            inv.setItem(i, spacer);
        }

        inv.setItem(12, chest);
        inv.setItem(13, chest);
        inv.setItem(14, chest);

        inv.setItem(21, chest);
        inv.setItem(22, chest);
        inv.setItem(23, chest);

        inv.setItem(30, chest);
        inv.setItem(31, chest);
        inv.setItem(32, chest);

        inv.setItem(40, enderchest);

        ItemStack box = Builder.nameItem(Material.valueOf(Lootbox.getInstance().getConfig().getString("OPTIONS.LOOTBOX-ITEM.MATERIAL")), Lootbox.getInstance().getConfig().getString("OPTIONS.LOOTBOX-ITEM.DISPLAY-NAME"), (short) Lootbox.getInstance().getConfig().getInt("OPTIONS.LOOTBOX-ITEM.ITEM-META"), 1, Lootbox.getInstance().getConfig().getStringList("OPTIONS.LOOTBOX-ITEM.LORES"));

        event.getPlayer().getInventory().removeItem(box);

        event.getPlayer().openInventory(inv);

    }

    @EventHandler
    public void place(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.ENDER_CHEST && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.getPlayer().sendMessage(CC.Color("&cYou cannot place Enderchests."));
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) {
            return;
        }
        if (!event.getClickedInventory().getName().equalsIgnoreCase(CC.Color("&6&lMystery Box"))) {
            return;
        }
        if (event.getCurrentItem() == null) {
            return;
        }
        if (event.getCurrentItem().getType() == null) {
            return;
        }
        if (event.getCurrentItem().getItemMeta() == null) {
            return;
        }
        if (event.getCurrentItem().getItemMeta().getDisplayName() == null) {
            return;
        }

        if (event.getCurrentItem().getType() == Material.CHEST) {
            event.setCancelled(true);
            Random ran = new Random();
            int rewardCount = REGULAR_ITEMS.size();
            int random = ran.nextInt(rewardCount);

            int chance = Lootbox.getInstance().getConfig().getInt("REWARDS.REGULAR-ITEMS." + REGULAR_ITEMS.get(random) + ".CHANCE");
            String displayName = Lootbox.getInstance().getConfig().getString("REWARDS.REGULAR-ITEMS." + REGULAR_ITEMS.get(random) + ".DISPLAY-NAME");
            Material material = Material.valueOf(Lootbox.getInstance().getConfig().getString("REWARDS.REGULAR-ITEMS." + REGULAR_ITEMS.get(random) + ".MATERIAL"));
            List<String> commands = Lootbox.getInstance().getConfig().getStringList("REWARDS.REGULAR-ITEMS." + REGULAR_ITEMS.get(random) + ".COMMANDS");
            int amount = Lootbox.getInstance().getConfig().getInt("REWARDS.REGULAR-ITEMS." + REGULAR_ITEMS.get(random) + ".AMOUNT");
            short meta = (short) Lootbox.getInstance().getConfig().getInt("REWARDS.REGULAR-ITEMS." + REGULAR_ITEMS.get(random) + ".ITEM-META");
            List<String> enchants = Lootbox.getInstance().getConfig().getStringList("REWARDS.REGULAR-ITEMS." + REGULAR_ITEMS.get(random) + ".ENCHANTS");
            List<String> lores = Lootbox.getInstance().getConfig().getStringList("REWARDS.REGULAR-ITEMS." + REGULAR_ITEMS.get(random) + ".LORES");

            ItemStack reward = Builder.nameItem(material, displayName, meta, amount, lores);

            if (enchants.size() != 0) {
                for (String str : enchants) {
                    Enchantment enchantment = Enchantment.getByName(str.split(":")[0]);
                    int level = Ints.tryParse(str.split(":")[1]);

                    reward.addUnsafeEnchantment(enchantment, level);
                }
            }

            event.getClickedInventory().setItem(event.getSlot(), reward);
            if (!rewardsCommon.contains(reward)) {
                rewardsCommon.add(reward);
            }

            for (String str : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), str.replace("{PLAYER}", event.getWhoClicked().getName()));
            }

            Player player = (Player) event.getWhoClicked();
//            player.sendMessage(CC.Color(CC.PREFIX + "You have received &6" + displayName + " &efrom a &62020 Lootbox&e."));

            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            if (OPENED_BOXES.containsKey(event.getWhoClicked().getUniqueId())) {
                OPENED_BOXES.put(event.getWhoClicked().getUniqueId(), OPENED_BOXES.get(event.getWhoClicked().getUniqueId()) + 1);
            } else {
                OPENED_BOXES.put(event.getWhoClicked().getUniqueId(), 1);
            }
        } else if (event.getCurrentItem().getType() == Material.ENDER_CHEST) {
            event.setCancelled(true);
            if (OPENED_BOXES.containsKey(event.getWhoClicked().getUniqueId()) && OPENED_BOXES.get(event.getWhoClicked().getUniqueId()) < 9) {
                ((Player) event.getWhoClicked()).sendMessage(CC.Color("&cYou must open the others before opening this one."));
                return;
            }
            Random ran = new Random();
            int rewardCount = FINAL_ITEMS.size();
            int random = ran.nextInt(rewardCount);

            int chance = Lootbox.getInstance().getConfig().getInt("REWARDS.FINAL-ITEMS." + FINAL_ITEMS.get(random) + ".CHANCE");
            String displayName = Lootbox.getInstance().getConfig().getString("REWARDS.FINAL-ITEMS." + FINAL_ITEMS.get(random) + ".DISPLAY-NAME");
            Material material = Material.valueOf(Lootbox.getInstance().getConfig().getString("REWARDS.FINAL-ITEMS." + FINAL_ITEMS.get(random) + ".MATERIAL"));
            List<String> commands = Lootbox.getInstance().getConfig().getStringList("REWARDS.FINAL-ITEMS." + FINAL_ITEMS.get(random) + ".COMMANDS");
            int amount = Lootbox.getInstance().getConfig().getInt("REWARDS.FINAL-ITEMS." + FINAL_ITEMS.get(random) + ".AMOUNT");
            short meta = (short) Lootbox.getInstance().getConfig().getInt("REWARDS.FINAL-ITEMS." + FINAL_ITEMS.get(random) + ".ITEM-META");
            List<String> enchants = Lootbox.getInstance().getConfig().getStringList("REWARDS.FINAL-ITEMS." + FINAL_ITEMS.get(random) + ".ENCHANTS");
            List<String> lores = Lootbox.getInstance().getConfig().getStringList("REWARDS.FINAL-ITEMS." + FINAL_ITEMS.get(random) + ".LORES");

            ItemStack reward = Builder.nameItem(material, displayName, meta, amount, lores);

            if (enchants.size() != 0) {
                for (String str : enchants) {
                    Enchantment enchantment = Enchantment.getByName(str.split(":")[0]);
                    int level = Ints.tryParse(str.split(":")[1]);

                    reward.addUnsafeEnchantment(enchantment, level);
                }
            }

            event.getClickedInventory().setItem(event.getSlot(), reward);
            if (!rewardsExclusive.contains(reward)) {
                rewardsExclusive.add(reward);
            }

            for (String str : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), str.replace("{PLAYER}", event.getWhoClicked().getName()));
            }

            Player player = (Player) event.getWhoClicked();
//            player.sendMessage(CC.Color(CC.PREFIX + "You have received &6" + displayName + " &efrom a &62020 Lootbox&e."));
            OPENED_BOXES.remove(player.getUniqueId());
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        } else {
            event.setCancelled(true);
        }

    }

    public ArrayList<ItemStack> rewardsCommon = new ArrayList<>();
    public ArrayList<ItemStack> rewardsExclusive = new ArrayList<>();

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getName().equalsIgnoreCase(CC.Color("&6&lMystery Box"))) {
            Player player = (Player) event.getPlayer();

            ItemStack chest = Builder.nameItem(Material.CHEST, CC.Color("&6&l???"), (short) 0, 1, Arrays.asList("&7Click here to open the &6Mystery Box&7."));
            ItemStack enderchest = Builder.nameItem(Material.ENDER_CHEST, CC.Color("&6&l"), (short) 0, 1, Arrays.asList("&7Click here to open the &6Mystery Box&7."));

            for (ItemStack stack : event.getInventory().getContents().clone()) {
                if (stack.equals(chest) || stack.equals(enderchest)) {
                    Inventory inv = Bukkit.createInventory(null, 54, CC.Color("&6&lMystery Box"));
                    inv.setContents(event.getInventory().getContents());

                    Bukkit.getScheduler().runTaskLater(Lootbox.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            player.openInventory(inv);
                            player.sendMessage(CC.translate("&CYou can't close this menu until you open all the chests."));
                        }
                    },5);
                    return;
                }
            }

            if (OPENED_BOXES.containsKey(event.getPlayer().getUniqueId())) {
                OPENED_BOXES.remove(event.getPlayer().getUniqueId());
            }

            runTasks(player);

            String longBar = "&6&m-----&e&m-----&6&m-----&e&m-----&6&m-----&e&m-----&6&m-----";
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(CC.translate(longBar));               online.sendMessage(CC.translate(CentredMessage.generate("&6&lMISTERY LOOTBOX")));
                online.sendMessage(CC.translate(CentredMessage.generate("&7▶ store.jademc.net ◀")));
                online.sendMessage(CC.translate("&c" + event.getPlayer().getName() + " &7has opened the following rewards!"));
                online.sendMessage(CC.translate(" "));
                online.sendMessage(CC.translate("&a&l&nCOMMON"));
                for (ItemStack stack : rewardsCommon) {
                    online.sendMessage(CC.translate(" &7▶ " + stack.getItemMeta().getDisplayName()));
                }
                online.sendMessage(CC.translate(" "));
                online.sendMessage(CC.translate("&4&l&nEXCLUSIVE"));
                for (ItemStack stack : rewardsExclusive) {
                    online.sendMessage(CC.translate(" &7▶ " + stack.getItemMeta().getDisplayName()));
                }
                online.sendMessage(CC.translate(" "));
                online.sendMessage(CC.translate(longBar));
            }

            Bukkit.getScheduler().runTaskLater(Lootbox.getInstance(), new Runnable() {
                @Override
                public void run() {
                    rewardsCommon.clear();
                    rewardsExclusive.clear();
                }
            }, 1);
        }
    }

    public void runTasks(Player player) {
        Bukkit.getScheduler().runTaskLater(Lootbox.getInstance(), new Runnable() {
            @Override
            public void run() {
                player.getWorld().strikeLightningEffect(player.getLocation().clone().add(0, 0, 2)); // Z
            }
        },3);

        Bukkit.getScheduler().runTaskLater(Lootbox.getInstance(), new Runnable() {
            @Override
            public void run() {
                player.getWorld().strikeLightningEffect(player.getLocation().clone().add(2, 0, 0)); // X
            }
        },6);

        Bukkit.getScheduler().runTaskLater(Lootbox.getInstance(), new Runnable() {
            @Override
            public void run() {
                player.getWorld().strikeLightningEffect(player.getLocation().clone().add(0, 0, -2)); // Z
            }
        },9);

        Bukkit.getScheduler().runTaskLater(Lootbox.getInstance(), new Runnable() {
            @Override
            public void run() {
                player.getWorld().strikeLightningEffect(player.getLocation().clone().add(-2, 0, 0)); // X
            }
        },12);

        Bukkit.getScheduler().runTaskLater(Lootbox.getInstance(), new Runnable() {
            @Override
            public void run() {
                player.getWorld().strikeLightningEffect(player.getLocation().clone().add(-2, 0, -2)); // Z X
            }
        },15);

        Bukkit.getScheduler().runTaskLater(Lootbox.getInstance(), new Runnable() {
            @Override
            public void run() {
                player.getWorld().strikeLightningEffect(player.getLocation().clone().add(2, 0, 2)); // Z X
            }
        },18);

        Bukkit.getScheduler().runTaskLater(Lootbox.getInstance(), new Runnable() {
            @Override
            public void run() {
                player.getWorld().strikeLightningEffect(player.getLocation().clone().add(2, 0, -2)); // Z X
            }
        },21);
    }

    public void setupChances() {
        for (String str : Lootbox.getInstance().getConfig().getConfigurationSection("REWARDS.REGULAR-ITEMS").getKeys(false)) {
            int chance = Lootbox.getInstance().getConfig().getInt("REWARDS.REGULAR-ITEMS." + str + ".CHANCE");

            for (int i = 0; i < chance; i++) {
                REGULAR_ITEMS.add(str);
            }
        }

        for (String str : Lootbox.getInstance().getConfig().getConfigurationSection("REWARDS.FINAL-ITEMS").getKeys(false)) {
            int chance = Lootbox.getInstance().getConfig().getInt("REWARDS.FINAL-ITEMS." + str + ".CHANCE");

            for (int i = 0; i < chance; i++) {
                FINAL_ITEMS.add(str);
            }
        }

    }

}
