package redempt.rcd;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class ClockFinder implements Listener {
	
	public BlockIndex index = new BlockIndex();
	public List<Page> pages = new ArrayList<>();
	public List<Location> locations = new ArrayList<>();
	private int task;
	public boolean finished = false;
	
	public ClockFinder(double scanTime, Player player) {
		long time = Math.round(scanTime * 20);
		ClockFinder finder = this;
		task = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			
			@Override
			public void run() {
				Page page = new Page();
				int pos = 0;
				pages.clear();
				for (Location loc : index.getLocations().keySet()) {
					if (!page.add(ChatColor.YELLOW + "" + pos + ": " + ChatColor.GREEN + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + " in " + loc.getWorld().getName() + ChatColor.YELLOW + " (" + index.getLocations().get(loc) + " redstone updates nearby)")) {
						pages.add(page);
						page = new Page();
					}
					pos++;
				}
				if (!page.isEmpty()) {
					pages.add(page);
				}
				HandlerList.unregisterAll(finder);
				locations.clear();
				player.sendMessage(ChatColor.YELLOW + "" + index.getLocations().keySet().size() + ChatColor.RED + " locations found by manual scan.");
				locations.addAll(index.getLocations().keySet());
				finished = true;
			}
			
		}, time);
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
	}
	
	@EventHandler
	public void onRedstoneUpdate(BlockRedstoneEvent e) {
		if (e.getNewCurrent() > e.getOldCurrent()) {
			for (Region region : Main.regions) {
				if (region.contains(e.getBlock().getLocation())) {
					return;
				}
			}
			index.add(e.getBlock().getLocation());
		}
	}
	
	@EventHandler
	public void onInventoryTransfer(InventoryMoveItemEvent e) {
		if (e.getDestination().getHolder() instanceof BlockState && e.getInitiator().getHolder() instanceof BlockState) {
			for (Region region : Main.regions) {
				if (region.contains(((BlockState) e.getDestination().getHolder()).getLocation())) {
					return;
				}
			}
			index.add(((BlockState) e.getDestination().getHolder()).getLocation());
		}
	}
	
	public void stop() {
		Bukkit.getScheduler().cancelTask(task);
	}
	
}
