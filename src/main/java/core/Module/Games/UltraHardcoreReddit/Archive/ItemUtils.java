package network.reborn.core.Module.Games.UltraHardcoreReddit.Archive;

import network.reborn.core.Util.MathUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
	public static ItemStack getBackItem() {
		return getReturnBanner();
	}

	public static ItemStack getPreviousItem() {
		return getLeftBanner();
	}

	public static ItemStack getNextItem() {
		return getRightBanner();
	}

	public static ItemStack getNullTile() {
		return getNull();
	}

	public static ItemStack getNullEnd() {
		return getNullB();
	}

	private static ItemStack getNull() {
		ItemStack n = new ItemStack(Material.STAINED_GLASS_PANE);
		n.setDurability((short) 15);
		ItemMeta nm = n.getItemMeta();
		nm.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				"&" + MathUtils.random(0, 9)) + "");
		n.setItemMeta(nm);
		return n;
	}

	private static ItemStack getNullB() {
		ItemStack n = new ItemStack(Material.BARRIER);
		ItemMeta nm = n.getItemMeta();
		nm.setDisplayName(ChatColor.translateAlternateColorCodes('&',
				"&" + MathUtils.random(0, 9)) + "");
		n.setItemMeta(nm);
		return n;
	}

	private static ItemStack getRightBanner() {
		ItemStack br = new ItemStack(Material.BANNER);
		BannerMeta brm = (BannerMeta) br.getItemMeta();
		brm.setDisplayName(ChatColor.YELLOW + "Next");
		brm.setBaseColor(DyeColor.BLACK);
		brm.addPattern(new Pattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE)); // Lozenge
		brm.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT)); // Per
																				// Pale
		brm.addPattern(
				new Pattern(DyeColor.BLACK, PatternType.SQUARE_BOTTOM_LEFT));
		brm.addPattern(
				new Pattern(DyeColor.BLACK, PatternType.SQUARE_TOP_LEFT));
		brm.addPattern(
				new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_BOTTOM));
		brm.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_TOP));
		brm.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
		brm.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		br.setItemMeta(brm);
		return br;
	}

	private static ItemStack getLeftBanner() {
		ItemStack lr = new ItemStack(Material.BANNER);
		BannerMeta lrm = (BannerMeta) lr.getItemMeta();
		lrm.setDisplayName(ChatColor.YELLOW + "Previous");
		lrm.setBaseColor(DyeColor.BLACK);
		lrm.addPattern(new Pattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE)); // Lozenge
		lrm.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT)); // Per
																				// Pale
		lrm.addPattern(
				new Pattern(DyeColor.BLACK, PatternType.SQUARE_BOTTOM_RIGHT));
		lrm.addPattern(
				new Pattern(DyeColor.BLACK, PatternType.SQUARE_TOP_RIGHT));
		lrm.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_TOP));
		lrm.addPattern(
				new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_BOTTOM));
		lrm.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
		lrm.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		lr.setItemMeta(lrm);
		return lr;
	}

	private static ItemStack getReturnBanner() {
		ItemStack lr = new ItemStack(Material.BANNER);
		BannerMeta lrm = (BannerMeta) lr.getItemMeta();
		lrm.setDisplayName(ChatColor.YELLOW + "Return");
		lrm.setBaseColor(DyeColor.BLACK);
		lrm.addPattern(new Pattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE)); // Lozenge
		lrm.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT)); // Per
																				// Pale
		lrm.addPattern(
				new Pattern(DyeColor.BLACK, PatternType.SQUARE_BOTTOM_RIGHT));
		lrm.addPattern(
				new Pattern(DyeColor.BLACK, PatternType.SQUARE_TOP_RIGHT));
		lrm.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_TOP));
		lrm.addPattern(
				new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_BOTTOM));
		lrm.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
		lrm.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		lr.setItemMeta(lrm);
		return lr;
	}
}
