package network.reborn.core.Util;

import com.google.common.collect.Lists;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChestPopulator {
    private static final List<ChestItem> chestItemList = Lists.newArrayList();
    private static ChestPopulator chest;
    private final Random random = new Random();

    public static void addChestItem(ItemStack itemStack, int chance) {
        chestItemList.add(new ChestItem(itemStack, chance));
    }

    public static ChestPopulator get() {
        if (chest == null) {
            chest = new ChestPopulator();
        }
        return chest;
    }

    @Deprecated
    public void populateChestOld(org.bukkit.block.Chest chest) {
        Inventory inventory = chest.getBlockInventory();
        int added = 0;
        Collections.shuffle(chestItemList);

        chest.getInventory().clear();

        for (ChestItem chestItem : chestItemList) {
            if (random.nextInt(100) + 1 <= chestItem.getChance()) {
                inventory.addItem(chestItem.getItem());
                if (added++ > inventory.getSize())
                    break;
            }
        }
    }

    public void populateDoubleChest(DoubleChest doubleChest) {
        populateInventory(doubleChest.getInventory(), 6, 10);
    }

    public void populateInventory(Inventory inventory) {
        populateInventory(inventory, 3, 5);
    }

    public void populateInventory(Inventory inventory, int maxItemsMin, int maxItemsMax) {
        inventory.clear();
        int added = 0;
        int maxItems = OtherUtil.randInt(maxItemsMin, maxItemsMax);
        Collections.shuffle(chestItemList);

        for (ChestItem chestItem : chestItemList) {
            if (random.nextInt(100) + 1 <= chestItem.getChance()) {
                inventory.setItem(OtherUtil.randInt(0, inventory.getSize() - 1), chestItem.getItem());
                added++;
                if (added > inventory.getSize() || added > maxItems)
                    break;
            }
        }

        boolean hasItem = false;
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null) {
                hasItem = true;
                break;
            }
        }
        if (!hasItem) {
            populateInventory(inventory);
        }
    }

    public void populateChest(org.bukkit.block.Chest chest) {
        populateInventory(chest.getInventory());
    }

    public static class ChestItem {
        private ItemStack item;
        private int chance;

        public ChestItem(ItemStack item, int chance) {
            this.item = item;
            this.chance = chance;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getChance() {
            return chance;
        }
    }

}
