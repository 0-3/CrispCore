package network.reborn.core.Module.SMP.Handlers;

import network.reborn.core.RebornCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shop implements Listener {
    private Inventory shop;
    private ArrayList<ShopItem> shopItems = new ArrayList<>();
    private HashMap<ShopItemType, HashMap<Integer, ShopItem>> shopCategoryItems = new HashMap<>();
    private HashMap<ShopItemType, HashMap<Integer, Inventory>> shopCategoryPages = new HashMap<>();

    public Shop() {
        // Setup main GUI
        setupShopGUI();
    }

    public void addShopItem(ShopItem shopItem) {
        shopItems.add(shopItem);
    }

    public void setupShopGUI() {
        shop = Bukkit.createInventory(null, 45, "Shop");

        ItemStack foodItem = new ItemStack(Material.COOKED_BEEF);
        ItemMeta foodMeta = foodItem.getItemMeta();
        foodMeta.setDisplayName(ChatColor.WHITE + "Food Items");
        foodItem.setItemMeta(foodMeta);
        shop.setItem(11, foodItem);

        ItemStack oresItem = new ItemStack(Material.IRON_INGOT);
        ItemMeta oresMeta = oresItem.getItemMeta();
        oresMeta.setDisplayName(ChatColor.WHITE + "Ore & Ingot Items");
        oresItem.setItemMeta(oresMeta);
        shop.setItem(13, oresItem);

        ItemStack armorItem = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta armorMeta = armorItem.getItemMeta();
        armorMeta.setDisplayName(ChatColor.WHITE + "Armor Items");
        armorItem.setItemMeta(armorMeta);
        shop.setItem(15, armorItem);

        ItemStack weaponItem = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta weaponMeta = weaponItem.getItemMeta();
        weaponMeta.setDisplayName(ChatColor.WHITE + "Weapons & Tools");
        weaponItem.setItemMeta(weaponMeta);
        shop.setItem(29, weaponItem);

        ItemStack blockItem = new ItemStack(Material.BRICK);
        ItemMeta blockMeta = blockItem.getItemMeta();
        blockMeta.setDisplayName(ChatColor.WHITE + "Block Items");
        blockItem.setItemMeta(blockMeta);
        shop.setItem(31, blockItem);

        ItemStack redstoneItem = new ItemStack(Material.TNT);
        ItemMeta redstoneMeta = redstoneItem.getItemMeta();
        redstoneMeta.setDisplayName(ChatColor.WHITE + "Redstone Items");
        redstoneItem.setItemMeta(redstoneMeta);
        shop.setItem(33, redstoneItem);
    }

    public void setupShopCategoryGUI(ShopItemType shopItemType) {
        Integer pageI = 0;
        Inventory currentPage = Bukkit.createInventory(null, 54, "Page " + (pageI + 1) + " - " + shopItemType.getNiceName() + " Items - Shop");

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("" + ChatColor.YELLOW + ChatColor.BOLD + "Back");
        back.setItemMeta(backMeta);
        currentPage.setItem(48, back);
        HashMap<Integer, ShopItem> items = new HashMap<>();
        HashMap<Integer, Inventory> pages = new HashMap<>();

        int i = 0;
        for (ShopItem shopItem : shopItems) {
            if (shopItem.getShopItemType() == null || shopItem.getShopItemType() != shopItemType)
                continue;
            int id = i;
            if (pageI > 0) {
                id = ((pageI + 1) * 45) + i;
            }
            items.put(id, shopItem);

            if (i > 44) {

                ItemStack nextPage = new ItemStack(Material.ARROW);
                ItemMeta nextPageMeta = nextPage.getItemMeta();
                nextPageMeta.setDisplayName("" + ChatColor.YELLOW + ChatColor.BOLD + "Next Page");
                nextPage.setItemMeta(nextPageMeta);
                currentPage.setItem(50, nextPage);

                pages.put(pageI, currentPage);
                pageI++;
                i = 0;
                currentPage = Bukkit.createInventory(null, 54, "Page " + (pageI + 1) + " - " + shopItemType.getNiceName() + " Items - Shop");

                ItemStack prevPage = new ItemStack(Material.ARROW);
                ItemMeta prevPageMeta = prevPage.getItemMeta();
                prevPageMeta.setDisplayName("" + ChatColor.YELLOW + ChatColor.BOLD + "Prev Page");
                prevPage.setItemMeta(prevPageMeta);
                currentPage.setItem(48, prevPage);
            }

            currentPage.setItem(i, shopItem.getItemStack(true));

            i++;
        }

        pages.put(pageI, currentPage);
        shopCategoryItems.put(shopItemType, items);
        shopCategoryPages.put(shopItemType, pages);
    }

    public void openShopGUI(Player player) {
        player.openInventory(shop);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player) || event.getCurrentItem() == null || event.getInventory().getTitle() == null || !event.getInventory().getTitle().toLowerCase().contains("shop"))
            return;

        final Player player = (Player) event.getWhoClicked();

        if (event.getInventory().getTitle().contains("Shop") && !event.getInventory().getTitle().contains("Items")) {
            if (!event.getCurrentItem().hasItemMeta() || event.getClickedInventory().getType() == null || event.getClickedInventory().getType() == InventoryType.PLAYER) {
                event.setCancelled(true);
                return;
            }
            switch (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().replaceAll(" items", ""))) {
                default:
                    event.setCancelled(true);
                    break;
                case "food":
                    event.setCancelled(true);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), () -> player.openInventory(shopCategoryPages.get(ShopItemType.FOOD).get(0)), 1L);
                    break;
                case "armor":
                    event.setCancelled(true);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), () -> player.openInventory(shopCategoryPages.get(ShopItemType.ARMOR).get(0)), 1L);
                    break;
                case "weapons & tools":
                    event.setCancelled(true);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), () -> player.openInventory(shopCategoryPages.get(ShopItemType.WEAPON).get(0)), 1L);
                    break;
                case "block":
                    event.setCancelled(true);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RebornCore.getRebornCore(), () -> player.openInventory(shopCategoryPages.get(ShopItemType.BLOCK).get(0)), 1L);
                    break;
            }
        } else if (event.getInventory().getTitle().contains("Items")) {
            // New generic statement to handle purchasing items (Doesn't include food for now!)
            event.setCancelled(true);
            if (!event.getCurrentItem().hasItemMeta() || event.getClickedInventory().getType() == null || event.getClickedInventory().getType() == InventoryType.PLAYER) {
                return;
            }
            Pattern pattern = Pattern.compile("Page (\\d*) - (.*) Items - Shop");
            Matcher matcher = pattern.matcher(event.getInventory().getTitle());
            if (!matcher.find()) {
                return;
            }
            String currentPageStr = matcher.group(1);
            String name = matcher.group(2);
            if (name.toUpperCase().equalsIgnoreCase("Weapons & Tools"))
                name = "WEAPON";
            ShopItemType shopItemType = ShopItemType.valueOf(name.toUpperCase());
            if (shopItemType == null)
                return;
            HashMap<Integer, ShopItem> items = shopCategoryItems.get(shopItemType);
            HashMap<Integer, Inventory> pages = shopCategoryPages.get(shopItemType);
            int currentPageInt = Integer.parseInt(currentPageStr);
            if (!event.getCurrentItem().getItemMeta().hasDisplayName()) {
                // Attempt to "purchase" item
                int id = event.getSlot();
                if (currentPageInt > 1) {
                    id = (currentPageInt * 45) + id;
                }
                ShopItem shopItem = items.get(id);
                if (shopItem == null) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "ITEM NOT FOUND D:");
                    return;
                }
                if (!shopItem.canPlayerAfford(player)) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "Cant afford");
                    return;
                }
                if (shopItem.doPurchase(player)) {
                    //player.sendMessage("Done");
                    event.setCancelled(true);
                    //player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                    player.playSound(player.getLocation(), Sound.ARROW_HIT, 1, 1);
                }
            } else {
                switch (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase())) {
                    default:
                        // Attempt to "purchase" item
                        int id = event.getSlot();
                        if (currentPageInt > 1) {
                            id = (currentPageInt * 45) + id;
                        }
                        ShopItem shopItem = items.get(id);
                        if (shopItem == null) {
                            player.closeInventory();
                            player.sendMessage(ChatColor.RED + "ITEM NOT FOUND D:");
                            return;
                        }
                        if (!shopItem.canPlayerAfford(player)) {
                            player.closeInventory();
                            player.sendMessage(ChatColor.RED + "Cant afford");
                            return;
                        }
                        if (shopItem.doPurchase(player)) {
                            event.setCancelled(true);
                            //player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                            player.playSound(player.getLocation(), Sound.ARROW_HIT, 1, 1);
                        }
                        break;
                    case "prev page":
                        event.setCancelled(true);
                        player.openInventory(pages.get(currentPageInt - 2));
                        break;
                    case "next page":
                        event.setCancelled(true);
                        player.openInventory(pages.get(currentPageInt));
                        break;
                    case "back":
                        event.setCancelled(true);
                        openShopGUI(player);
                        break;
                }
            }
        }
    }

    public enum ShopItemType {
        FOOD, ORE, INGOT, ARMOR, WEAPON, BLOCK, REDSTONE;

        public String getNiceName() {
            switch (this) {
                case FOOD:
                    return "Food";
                case ARMOR:
                    return "Armor";
                case WEAPON:
                    return "Weapons & Tools";
                case BLOCK:
                    return "Block";
            }
            return this.toString();
        }
    }

}
