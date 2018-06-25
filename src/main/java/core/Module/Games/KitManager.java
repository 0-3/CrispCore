package network.reborn.core.Module.Games;

import network.reborn.core.API.RebornPlayer;
import network.reborn.core.API.ServerRank;
import network.reborn.core.Module.Games.Events.KitUpdateEvent;
import network.reborn.core.RebornCore;
import network.reborn.core.Util.GUI;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class KitManager implements Listener {
    public static boolean registeredListeners = false;
    protected Game game = null;
    protected ArrayList<String> kitOrder = new ArrayList<>();
    protected HashMap<String, Kit> registeredKits = new HashMap<>();
    protected HashMap<String, String> playersKits = new HashMap<>();
    protected HashMap<String, String> kitPurchasing = new HashMap<>();
    protected String defaultKit = null;

    public KitManager() {
        if (!registeredListeners) {
            Bukkit.getPluginManager().registerEvents(this, RebornCore.getRebornCore());
            registeredListeners = true;
        }
    }

    public KitManager(Game game) {
        this.game = game;
        if (!registeredListeners) {
            Bukkit.getPluginManager().registerEvents(this, RebornCore.getRebornCore());
            registeredListeners = true;
        }
    }

    protected abstract void loadKits();

    public HashMap<String, Kit> getRegisteredKits() {
        return registeredKits;
    }

    protected String getSlug() {
        if (game != null) return game.getSlug();
        return "";
    }

    public void addKit(Kit kit) {
        kitOrder.add(kit.getSlug());
        registeredKits.put(kit.getSlug(), kit);
    }

    public Kit getKit(String slug) {
        if (registeredKits.containsKey(slug))
            return registeredKits.get(slug);
        return null;
    }

    public void setDefaultKit(String defaultKit) {
        if (registeredKits.containsKey(defaultKit))
            this.defaultKit = defaultKit;
    }

    public void setDefaultKit(Kit kit) {
        if (!registeredKits.containsKey(kit.getSlug()))
            registeredKits.put(kit.getSlug(), kit);
        setDefaultKit(kit.getSlug());
    }

    public Kit getPlayersKit(String UUID) {
        UUID = UUID.replaceAll("-", "");
        if (playersKits.containsKey(UUID))
            return getKit(playersKits.get(UUID));
        if (defaultKit != null && getKit(defaultKit) != null)
            return getKit(defaultKit);
        return null;
    }

    public Kit getPlayersKit(Player player) {
        return getPlayersKit(player.getUniqueId().toString().replaceAll("-", ""));
    }

    public void giveAllPlayersKits() {
        for (Player player : Bukkit.getOnlinePlayers())
            givePlayerKit(player);
    }

    public void givePlayerKit(Player player) {
        Kit kit = getPlayersKit(player);
        if (kit == null)
            return;

        for (ItemStack itemStack : kit.getItems()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta.hasDisplayName() && ChatColor.stripColor(itemMeta.getDisplayName()).replaceAll("\\((.*)\\)", "").replaceAll(" ", "").equals(ChatColor.stripColor(kit.getName().replaceAll(" ", ""))))
                itemMeta.setDisplayName(null);

            itemStack.setItemMeta(itemMeta);
            player.getInventory().addItem(itemStack);
        }

        if (kit.getHelmet() != null)
            player.getInventory().setHelmet(kit.getHelmet());

        if (kit.getChestplate() != null)
            player.getInventory().setChestplate(kit.getChestplate());

        if (kit.getLeggings() != null)
            player.getInventory().setLeggings(kit.getLeggings());

        if (kit.getBoots() != null)
            player.getInventory().setBoots(kit.getBoots());
    }

    public Inventory getKitsGUI(Player player) {
        GUI gui = new GUI("Kits");

        for (String kitSlug : kitOrder) {
            if (!registeredKits.containsKey(kitSlug))
                continue;
            Kit kit = registeredKits.get(kitSlug);
            ItemStack view = null;
            if (kit.getItems().size() > 0) {
                view = kit.getItems().get(0);
            } else if (kit.getChestplate() != null) {
                view = kit.getChestplate();
            } else if (kit.getHelmet() != null) {
                view = kit.getHelmet();
            }

            if (view == null)
                continue;

            ItemMeta itemMeta = view.getItemMeta();
            if (kit.playerHas(player)) {
                itemMeta.setDisplayName(ChatColor.AQUA + kit.getName() + " (Purchased)");
            } else if (kit.playerCanAfford(player)) {
                itemMeta.setDisplayName(ChatColor.GREEN + kit.getName() + " (" + kit.getPrice() + ")");
            } else {
                itemMeta.setDisplayName(ChatColor.RED + kit.getName() + " (" + kit.getPrice() + ")");
            }

            view.setItemMeta(itemMeta);
            gui.addItem(view);
        }

        GUI.guis.put(gui.getTitle(), gui);

        return gui.create(0);
    }

    public Inventory getKitConfirmPurchaseGUI(Player player, Kit kit) {
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);
        Inventory inventory = Bukkit.createInventory(null, 54, "Confirm " + kit.getName() + " Kit Purchase");

        ItemStack itemStack = new ItemStack(Material.LEATHER_CHESTPLATE);
        if (!kit.getItems().isEmpty())
            itemStack = kit.getItems().get(0);
        else if (kit.getChestplate() != null)
            itemStack = kit.getChestplate();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + kit.getName());
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(13, itemStack);

        ItemStack confirm = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "Confirm Purchase");
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add("" + ChatColor.GRAY + kit.getPrice() + " Coins will be taken");
        lore.add("" + ChatColor.GRAY + "from your balance.");
        lore.add(" ");
        confirmMeta.setLore(lore);
        confirm.setItemMeta(confirmMeta);
        inventory.setItem(38, confirm);

        ItemStack balance = new ItemStack(Material.DOUBLE_PLANT);
        ItemMeta balanceMeta = balance.getItemMeta();
        balanceMeta.setDisplayName(ChatColor.GOLD + "Coins Balance");
        lore.clear();
        lore.add(" ");
        lore.add("" + ChatColor.GRAY + "Current: " + rebornPlayer.getBalance(getSlug()));
        lore.add("" + ChatColor.GRAY + "After Purchase: " + (rebornPlayer.getBalance(getSlug()) - kit.getPrice()));
        lore.add(" ");
        lore.add("" + ChatColor.GRAY + "Buy more coins at");
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
        lore.add("" + ChatColor.GRAY + "No coins will be taken");
        lore.add("" + ChatColor.GRAY + "from your balance.");
        lore.add(" ");
        cancelMeta.setLore(lore);
        cancel.setItemMeta(cancelMeta);
        inventory.setItem(42, cancel);

        return inventory;
    }

    public void openKitGUI(Player player) {
        player.openInventory(getKitsGUI(player));
    }

    public void openKitConfirmPurchaseGUI(Player player, Kit kit) {
        player.openInventory(getKitConfirmPurchaseGUI(player, kit));
    }

    public void addPlayerKit(String uuid, String kitSlug) {
        playersKits.put(uuid, kitSlug);
    }

    public void addKitPurchasing(String uuid, String kitSlug) {
        kitPurchasing.put(uuid, kitSlug);
    }

    public boolean isKitPurchasing(String uuid) {
        return kitPurchasing.containsKey(uuid);
    }

    public String getKitPurchasing(String uuid) {
        return kitPurchasing.get(uuid);
    }

    @EventHandler
    public void onPlayerClickKitInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) event.getWhoClicked();
        RebornPlayer rebornPlayer = RebornCore.getCoveAPI().getCovePlayer(player);

        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName())
            return;

        if (event.getInventory().getName() != null && event.getInventory().getName().contains("Kits (Page ")) {
            if (event.getCurrentItem().getType() == Material.ARROW)
                return;

            event.setCancelled(true);
            player.closeInventory();

            if (game != null && game.getGameState() != GameState.WAITING)
                return;

            for (java.util.Map.Entry<String, Kit> v : getRegisteredKits().entrySet()) {
                Kit kit = v.getValue();
                if (ChatColor.stripColor(kit.getName()).replaceAll(" ", "").equals(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).replaceAll("\\((.*)\\)", "").replaceAll(" ", ""))) {
                    if (kit.playerHas(player)) {
                        if (game != null) {
                            addPlayerKit(player.getUniqueId().toString().replaceAll("-", ""), kit.getSlug());
                            player.sendMessage(ChatColor.GREEN + "Selected " + ChatColor.YELLOW + kit.getName() + ChatColor.GREEN + " Kit");
                            KitUpdateEvent kitUpdateEvent = new KitUpdateEvent(player, kit);
                            Bukkit.getPluginManager().callEvent(kitUpdateEvent);
                        } else {
                            player.sendMessage(ChatColor.GREEN + "You already own this kit");
                        }
                    } else if (kit.playerCanAfford(player) || rebornPlayer.canPlayer(ServerRank.ADMIN)) {
                        addKitPurchasing(player.getUniqueId().toString().replaceAll("-", ""), kit.getSlug());
                        openKitConfirmPurchaseGUI(player, kit);
                    } else {
                        player.sendMessage(ChatColor.RED + "You can not afford this kit");
                    }
                    return;
                }
            }
            player.sendMessage(ChatColor.RED + "Couldn't Find kit... Please report this to staff");
        } else if (event.getInventory().getName() != null && event.getInventory().getName().contains(" Kit Purchase")) {
            if (!isKitPurchasing(player.getUniqueId().toString().replaceAll("-", "")))
                return;

            if (game != null && game.getGameState() != GameState.WAITING) {
                return;
            }

            Kit kit = getKit(getKitPurchasing(player.getUniqueId().toString().replaceAll("-", "")));
            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Confirm")) {
                event.setCancelled(true);
                player.closeInventory();
                if (kit.playerBuy(player)) {
                    player.sendMessage(ChatColor.GREEN + "Successfully purchased " + ChatColor.YELLOW + kit.getName() + ChatColor.GREEN + " Kit");
                    addPlayerKit(player.getUniqueId().toString().replaceAll("-", ""), kit.getSlug());
                    KitUpdateEvent kitUpdateEvent = new KitUpdateEvent(player, kit);
                    Bukkit.getPluginManager().callEvent(kitUpdateEvent);
                } else {
                    player.sendMessage(ChatColor.RED + "You can not afford this kit");
                }
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Cancel")) {
                event.setCancelled(true);
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Kit purchase canceled");
            } else {
                event.setCancelled(true);
            }
        }
    }

}
