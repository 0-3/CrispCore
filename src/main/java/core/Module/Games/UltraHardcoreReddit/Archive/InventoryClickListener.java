package network.reborn.core.Module.Games.UltraHardcoreReddit.Archive;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getClickedInventory().getTitle() != null) {
			if (event.getClickedInventory().getTitle()
					.contains("Game Archive")) {
				event.setCancelled(true);
				int page = getPage(event.getClickedInventory().getTitle());
				int slot = event.getSlot();
				if (page == 1) {
					if (slot == 53) {
						event.getWhoClicked().closeInventory();
						GameDataMenu.openInv((Player) event.getWhoClicked(), 2);
					}
				} else if (page == 2) {
					if (slot == 45) {
						event.getWhoClicked().closeInventory();
						GameDataMenu.openInv((Player) event.getWhoClicked(), 1);
					} else if (slot == 53) {
						GameDataMenu.openInv((Player) event.getWhoClicked(), 3);
					}
				} else if (page == 3) {
					if (slot == 45) {
						event.getWhoClicked().closeInventory();
						GameDataMenu.openInv((Player) event.getWhoClicked(), 2);
					}
				}
			}
		}
	}

	int getPage(String title) {
		title = ChatColor.stripColor(title);
		if (title.contains("1")) {
			return 1;
		} else if (title.contains("2")) {
			return 2;
		} else if (title.contains("3")) {
			return 3;
		} else {
			return 0;
		}
	}

}
