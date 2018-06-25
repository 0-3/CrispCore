package network.reborn.core.Module.Hub.Cosmetics;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.OtherUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cosmetics implements Listener {
    private HashMap<String, Gadget> gadgets = new HashMap<>();
    private HashMap<String, Hat> hats = new HashMap<>();
    private HashMap<String, Morph> morphs = new HashMap<>();
    private Inventory coveBox;
    private HashMap<String, String> gadgetsPurchasing = new HashMap<>();

    public Cosmetics() {
        RebornCore.getRebornCore().getServer().getPluginManager().registerEvents(this, RebornCore.getRebornCore());
        Bukkit.getScheduler().runTaskTimer(RebornCore.getRebornCore(), new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, Gadget> entry : gadgets.entrySet()) {
                    for (Map.Entry<String, Integer> entry2 : entry.getValue().cooldownMap.entrySet()) {
                        if (entry2.getValue() == 0) {
                            entry.getValue().cooldownMap.remove(entry2.getKey());
                        } else {
                            entry.getValue().cooldownMap.put(entry2.getKey(), entry2.getValue() - 1);
                        }
                    }
                }
            }
        }, 20L, 20L);
        setupCoveBox();
    }

    private void setupCoveBox() {
        coveBox = Bukkit.createInventory(null, 27, "Fun Box");

        ItemStack gadgets = new ItemStack(Material.FIREWORK);
        ItemMeta gadgetsMeta = gadgets.getItemMeta();
        gadgetsMeta.setDisplayName(ChatColor.GREEN + "Gadgets");
        gadgets.setItemMeta(gadgetsMeta);
        coveBox.setItem(10, gadgets);

        ItemStack hats = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta hatsMeta = (SkullMeta) hats.getItemMeta();
        hatsMeta.setDisplayName(ChatColor.RED + "Hats (Coming Soon)");
        hatsMeta.setOwner("Miner");
        hats.setItemMeta(hatsMeta);
        coveBox.setItem(12, hats);

        ItemStack particles = new ItemStack(Material.NETHER_STAR);
        ItemMeta particlesMeta = particles.getItemMeta();
        particlesMeta.setDisplayName(ChatColor.RED + "Particles (Coming Soon)");
        particles.setItemMeta(particlesMeta);
        coveBox.setItem(14, particles);

        ItemStack morphs = new ItemStack(Material.SKULL_ITEM, 1, (short) 2);
        ItemMeta morphsMeta = morphs.getItemMeta();
        morphsMeta.setDisplayName(ChatColor.RED + "Morphs (Coming Soon)");
        morphs.setItemMeta(morphsMeta);
        coveBox.setItem(16, morphs);
    }

    public Inventory getCoveBox() {
        return coveBox;
    }

    public void addGadget(Gadget gadget) {
        gadgets.put(gadget.getSlug(), gadget);
    }

    public HashMap<String, Gadget> getGadgets() {
        return gadgets;
    }

    public Inventory getGadgetsGUI(Player player) {
        return getGadgetsGUI(player, 1);
    }

    public Inventory getGadgetsGUI(Player player, int page) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);

        int perPage = 21;
        int pages = (int) Math.ceil(gadgets.size() / perPage) + 1;

        Inventory inventory = Bukkit.createInventory(null, 54, "Gadgets (Page " + page + "/" + pages + ")");

        int i = 10;
        int count = 0;
        int countStart = 0;
        int startFrom = 0;
        if (page > 1)
            startFrom = (page - 1) * perPage;
        for (Map.Entry<String, Gadget> entry : gadgets.entrySet()) {
            if (startFrom > countStart) {
                countStart++;
                continue;
            }
            Gadget gadget = entry.getValue();
            ItemStack itemStack = new ItemStack(gadget.getMaterial(), 1, gadget.getData());
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (gadget.isPrivateBeta() && !rebornPlayer.canPlayer(ServerRank.ADMIN))
                continue;

            if (gadget.isPrivateBeta()) {
                itemMeta.setDisplayName(ChatColor.GREEN + gadget.getName() + " (Beta)");
            } else if (gadget.getCost() <= 0) {
                itemMeta.setDisplayName(ChatColor.GREEN + gadget.getName());
            } else if (gadget.playerHas(player)) {
                if (gadget.isPurchasable()) {
                    itemMeta.setDisplayName(ChatColor.GREEN + gadget.getName() + " (Purchased)");
                } else {
                    itemMeta.setDisplayName(ChatColor.GREEN + gadget.getName());
                }
            } else if (gadget.isPurchasable()) {
                itemMeta.setDisplayName(ChatColor.RED + gadget.getName() + " (" + gadget.getCost() + ")");
            } else {
                itemMeta.setDisplayName(ChatColor.RED + gadget.getName());
            }

            if (gadget.getDesc() != null) {
                itemMeta.setLore(OtherUtil.stringToLore(gadget.getDesc(), ChatColor.GRAY));
            }

            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i, itemStack);

            i++;
            count++;

            if (count == perPage)
                break;

            if (i == 17)
                i = 19;

            if (i == 26)
                i = 28;

            if (i == 35)
                i = 37;

            if (i == 44)
                i = 46;
        }

        if (page == 1) {
            ItemStack back = new ItemStack(Material.ARROW);
            ItemMeta backMeta = back.getItemMeta();
            backMeta.setDisplayName(ChatColor.YELLOW + "Go Back");
            back.setItemMeta(backMeta);
            inventory.setItem(48, back);
        } else {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.setDisplayName(ChatColor.YELLOW + "Previous Page");
            prev.setItemMeta(prevMeta);
            inventory.setItem(48, prev);
        }

        if (page < pages) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(ChatColor.YELLOW + "Next Page");
            next.setItemMeta(nextMeta);
            inventory.setItem(50, next);
        }

        return inventory;
    }

    public void openGadgetsGUI(Player player) {
        player.openInventory(getGadgetsGUI(player));
    }

    public void openGadgetsGUI(Player player, int page) {
        player.openInventory(getGadgetsGUI(player, page));
    }

    public void addHat(Hat hat) {
        hats.put(hat.getSlug(), hat);
    }

    public HashMap<String, Hat> getHats() {
        return hats;
    }

    public Inventory getHatsGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Hats");

        int i = 10;
        for (Map.Entry<String, Hat> entry : hats.entrySet()) {
            Hat hat = entry.getValue();
            ItemStack itemStack = hat.getItemStack();
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (hat.getCost() <= 0) {
                itemMeta.setDisplayName(ChatColor.AQUA + hat.getName());
            } else if (hat.playerHas(player)) {
                if (hat.isPurchasable()) {
                    itemMeta.setDisplayName(ChatColor.AQUA + hat.getName() + " (Purchased)");
                } else {
                    itemMeta.setDisplayName(ChatColor.AQUA + hat.getName());
                }
            } else if (hat.playerCanAfford(player) && hat.isPurchasable()) {
                itemMeta.setDisplayName(ChatColor.GREEN + hat.getName() + " (" + hat.getCost() + ")");
            } else {
                if (hat.isPurchasable()) {
                    itemMeta.setDisplayName(ChatColor.RED + hat.getName() + " (" + hat.getCost() + ")");
                } else {
                    itemMeta.setDisplayName(ChatColor.RED + hat.getName());
                }
            }

            if (hat.getDesc() != null) {
                itemMeta.setLore(OtherUtil.stringToLore(hat.getDesc(), ChatColor.GRAY));
            }

            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i, itemStack);

            i++;
            if (i == 17)
                i = 19;

            if (i == 26)
                i = 28;

            if (i == 35)
                i = 37;

            if (i == 44)
                break; // TODO Pagination
        }

        return inventory;
    }

    public void openHatsGUI(Player player) {
        player.openInventory(getHatsGUI(player));
    }

    public Inventory getConfirmPurchaseGadgetGUI(Player player, Gadget gadget) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
        Inventory inventory = Bukkit.createInventory(null, 54, "Confirm Gadget Purchase");

        ItemStack itemStack = new ItemStack(gadget.getMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + gadget.getName());
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(13, itemStack);

        ItemStack confirm = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "Confirm Purchase");
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add("" + ChatColor.GRAY + gadget.getCost() + " Gold will be taken");
        lore.add("" + ChatColor.GRAY + "from your balance.");
        lore.add(" ");
        confirmMeta.setLore(lore);
        confirm.setItemMeta(confirmMeta);
        inventory.setItem(38, confirm);

        ItemStack balance = new ItemStack(Material.DOUBLE_PLANT);
        ItemMeta balanceMeta = balance.getItemMeta();
        balanceMeta.setDisplayName(ChatColor.GOLD + "Gold Balance");
        lore.clear();
        lore.add(" ");
        lore.add("" + ChatColor.GRAY + "Current: " + rebornPlayer.getBalance("Gold"));
        lore.add("" + ChatColor.GRAY + "After Purchase: " + (rebornPlayer.getBalance("Gold") - gadget.getCost()));
        lore.add(" ");
        lore.add("" + ChatColor.GRAY + "Buy more gold at");
        lore.add("" + ChatColor.GRAY + "reborn.network");
        lore.add(" ");
        balanceMeta.setLore(lore);
        balance.setItemMeta(balanceMeta);
        inventory.setItem(31, balance);

        ItemStack cancel = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.RED + "Cancel Purchase");
        lore.clear();
        lore.add(" ");
        lore.add("" + ChatColor.GRAY + "No gold will be taken");
        lore.add("" + ChatColor.GRAY + "from your balance.");
        lore.add(" ");
        cancelMeta.setLore(lore);
        cancel.setItemMeta(cancelMeta);
        inventory.setItem(42, cancel);

        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle() == null || event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta())
            return;

        if (!(event.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) event.getWhoClicked();
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
        event.setCancelled(true);

        if (event.getInventory().getTitle().contains(" Gadget Purchase")) {
            if (!gadgetsPurchasing.containsKey(player.getUniqueId().toString().replaceAll("-", "")))
                return;
            Gadget gadget = gadgets.get(gadgetsPurchasing.get(player.getUniqueId().toString().replaceAll("-", "")));
            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Confirm")) {
                event.setCancelled(true);
                player.closeInventory();
                if (gadget.playerBuy(player)) {
                    player.sendMessage(ChatColor.GREEN + "Successfully purchased " + ChatColor.YELLOW + gadget.getName() + ChatColor.GREEN + " Gadget");
                } else {
                    player.sendMessage(ChatColor.RED + "You can not afford this gadget");
                }
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Cancel")) {
                event.setCancelled(true);
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Gadget purchase canceled");
            } else {
                event.setCancelled(true);
            }
            return;
        }

        switch (event.getInventory().getTitle().toLowerCase().replaceAll("\\((.*)\\)", "").replaceAll(" ", "")) {
            default:
                return;
            case "gadgets":

                if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Go Back")) {
                    return;
                }

                if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Previous Page")) {
                    int currentPage = Integer.parseInt(event.getInventory().getTitle().replaceAll("Gadgets \\(Page ", "").replaceAll("/[0-9]\\)", ""));
                    openGadgetsGUI(player, currentPage - 1);
                    return;
                }

                if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Next Page")) {
                    int currentPage = Integer.parseInt(event.getInventory().getTitle().replaceAll("Gadgets \\(Page ", "").replaceAll("/[0-9]\\)", ""));
                    openGadgetsGUI(player, currentPage + 1);
                    return;
                }

                Gadget finalGadget = null;
                for (Map.Entry<String, Gadget> entry : gadgets.entrySet()) {
                    Gadget gadget = entry.getValue();
                    if (gadget.getName().replaceAll(" ", "").equals(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().replaceAll("\\((.*)\\)", "").replaceAll(" ", "")))) {
                        finalGadget = gadget;
                        break;
                    }
                }

                if (finalGadget != null) {
                    ItemStack itemStack = event.getCurrentItem().clone();
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName(itemMeta.getDisplayName().replaceAll(" \\((.*)\\)", "") + ChatColor.GRAY + " (Right Click)");
                    itemStack.setItemMeta(itemMeta);

                    if (finalGadget.playerHas(player) || finalGadget.isPrivateBeta()) {
                        player.getInventory().setItem(3, itemStack);
                        player.getInventory().setHeldItemSlot(3);
                    } else if (finalGadget.isPurchasable() && finalGadget.playerCanAfford(player)) {
                        gadgetsPurchasing.put(player.getUniqueId().toString().replaceAll("-", ""), finalGadget.getSlug());
                        player.openInventory(getConfirmPurchaseGadgetGUI(player, finalGadget));
                        return;
                    } else if (finalGadget.isPurchasable()) {
                        player.sendMessage(ChatColor.RED + "You can not afford the " + ChatColor.YELLOW + finalGadget.getName() + ChatColor.RED + " Gadget");
                    } else {
                        player.sendMessage(ChatColor.RED + "You have not unlocked the " + ChatColor.YELLOW + finalGadget.getName() + ChatColor.RED + " Gadget");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Unable to find gadget, notify staff of this error with the gadget name");
                    if (rebornPlayer.canPlayer(ServerRank.MODERATOR))
                        player.sendMessage("Failed Name: " + event.getCurrentItem().getItemMeta().getDisplayName().replaceAll("\\((.*)\\)", "").replaceAll(" ", ""));
                }
                break;
            case "hats":
                Hat finalHat = null;
                for (Map.Entry<String, Hat> entry : hats.entrySet()) {
                    Hat hat = entry.getValue();
                    if (hat.getName().replaceAll(" ", "").equals(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().replaceAll("\\((.*)\\)", "").replaceAll(" ", "")))) {
                        finalHat = hat;
                        break;
                    }
                }

                if (finalHat != null) {
                    if (finalHat.playerHas(player)) {
                        finalHat.setHat(player);
                    } else if (finalHat.isPurchasable() && finalHat.playerCanAfford(player)) {
                        player.sendMessage(ChatColor.YELLOW + "// TODO: Purchase hats");
                    } else if (finalHat.isPurchasable()) {
                        player.sendMessage(ChatColor.RED + "You can not afford the " + ChatColor.YELLOW + finalHat.getName() + ChatColor.RED + " Hat");
                    } else {
                        player.getInventory().setItem(3, event.getCurrentItem());
                        player.getInventory().setHeldItemSlot(3);
                        player.sendMessage(ChatColor.RED + "You have not unlocked the " + ChatColor.YELLOW + finalHat.getName() + ChatColor.RED + " Hat");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Unable to find hat, notify staff of this error with the hat name");
                }
                break;
            case "morphs":
                Morph finalMorph = null;
                for (Map.Entry<String, Morph> entry : morphs.entrySet()) {
                    Morph morph = entry.getValue();
                    if (morph.getName().replaceAll(" ", "").equals(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().replaceAll("\\((.*)\\)", "").replaceAll(" ", "")))) {
                        finalMorph = morph;
                        break;
                    }
                }

                if (finalMorph != null) {
                    if (finalMorph.playerHas(player)) {
                        Bukkit.broadcastMessage("Found morph, #doMorph");
                        finalMorph.doMorph(player);
                    } else if (finalMorph.isPurchasable() && finalMorph.playerCanAfford(player)) {
                        player.sendMessage(ChatColor.YELLOW + "// TODO: Purchase hats");
                    } else if (finalMorph.isPurchasable()) {
                        player.sendMessage(ChatColor.RED + "You can not afford the " + ChatColor.YELLOW + finalMorph.getName() + ChatColor.RED + " Hat");
                    } else {
                        player.getInventory().setItem(3, event.getCurrentItem());
                        player.getInventory().setHeldItemSlot(3);
                        player.sendMessage(ChatColor.RED + "You have not unlocked the " + ChatColor.YELLOW + finalMorph.getName() + ChatColor.RED + " Hat");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Unable to find morph, notify staff of this error with the morph you selected");
                }
                break;
        }
        player.closeInventory();

    }

    @EventHandler
    public void onInventoryClick2(InventoryClickEvent event) {
        if (event.getInventory().getTitle() == null || event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        event.setCancelled(true);

        String title = event.getInventory().getTitle();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        if (title.contains("Gadgets") && itemName.contains("Go Back")) {
            event.getWhoClicked().openInventory(getCoveBox());
            return;
        }

        switch (title) {
            default:
                break;
            case "Fun Box":
                switch (ChatColor.stripColor(itemName).replaceAll(" \\((.*)\\)", "")) {
                    default:
                        event.getWhoClicked().closeInventory();
                        event.getWhoClicked().sendMessage(ChatColor.RED + ChatColor.stripColor(itemName).replaceAll(" \\((.*)\\)", "") + " are coming soon!");
                        break;
                    case "Gadgets":
                        event.getWhoClicked().openInventory(getGadgetsGUI((Player) event.getWhoClicked()));
                        break;
                    case "Morphs":
                        if (RebornCore.getCoveAPI().getCovePlayer(event.getWhoClicked().getUniqueId()).canPlayer(ServerRank.OWNER)) {
                            event.getWhoClicked().openInventory(getMorphsGUI((Player) event.getWhoClicked()));
                        } else {
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage(ChatColor.RED + ChatColor.stripColor(itemName).replaceAll(" \\((.*)\\)", "") + " are coming soon!");
                        }
                        break;
                }
                break;
        }
    }

    // Morphs

    public void addMorph(Morph morph) {
        morphs.put(morph.getSlug(), morph);
    }

    public HashMap<String, Morph> getMorphs() {
        return morphs;
    }

    public Inventory getMorphsGUI(Player player) {
        return getMorphsGUI(player, 1);
    }

    public Inventory getMorphsGUI(Player player, int page) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);

        int perPage = 21;
        int pages = (int) Math.ceil(morphs.size() / perPage) + 1;

        Inventory inventory = Bukkit.createInventory(null, 54, "Morphs (Page " + page + "/" + pages + ")");

        int i = 10;
        int count = 0;
        int countStart = 0;
        int startFrom = 0;
        if (page > 1)
            startFrom = (page - 1) * perPage;
        for (Map.Entry<String, Morph> entry : morphs.entrySet()) {
            if (startFrom > countStart) {
                countStart++;
                continue;
            }
            Morph morph = entry.getValue();
            ItemStack itemStack = new ItemStack(morph.getMaterial(), 1, morph.getData());
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (morph.isPrivateBeta() && !rebornPlayer.canPlayer(ServerRank.ADMIN))
                continue;

            if (morph.isPrivateBeta()) {
                itemMeta.setDisplayName(ChatColor.GREEN + morph.getName() + " (Beta)");
            } else if (morph.getCost() <= 0) {
                itemMeta.setDisplayName(ChatColor.GREEN + morph.getName());
            } else if (morph.playerHas(player)) {
                if (morph.isPurchasable()) {
                    itemMeta.setDisplayName(ChatColor.GREEN + morph.getName() + " (Purchased)");
                } else {
                    itemMeta.setDisplayName(ChatColor.GREEN + morph.getName());
                }
            } else if (morph.isPurchasable()) {
                itemMeta.setDisplayName(ChatColor.RED + morph.getName() + " (" + morph.getCost() + ")");
            } else {
                itemMeta.setDisplayName(ChatColor.RED + morph.getName());
            }

            if (morph.getDesc() != null) {
                itemMeta.setLore(OtherUtil.stringToLore(morph.getDesc(), ChatColor.GRAY));
            }

            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i, itemStack);

            i++;
            count++;

            if (count == perPage)
                break;

            if (i == 17)
                i = 19;

            if (i == 26)
                i = 28;

            if (i == 35)
                i = 37;

            if (i == 44)
                i = 46;
        }

        if (page == 1) {
            ItemStack back = new ItemStack(Material.ARROW);
            ItemMeta backMeta = back.getItemMeta();
            backMeta.setDisplayName(ChatColor.YELLOW + "Go Back");
            back.setItemMeta(backMeta);
            inventory.setItem(48, back);
        } else {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.setDisplayName(ChatColor.YELLOW + "Previous Page");
            prev.setItemMeta(prevMeta);
            inventory.setItem(48, prev);
        }

        if (page < pages) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(ChatColor.YELLOW + "Next Page");
            next.setItemMeta(nextMeta);
            inventory.setItem(50, next);
        }

        return inventory;
    }

    public void openMorphsGUI(Player player) {
        player.openInventory(getMorphsGUI(player));
    }

    public void openMorphsGUI(Player player, int page) {
        player.openInventory(getMorphsGUI(player, page));
    }

}
