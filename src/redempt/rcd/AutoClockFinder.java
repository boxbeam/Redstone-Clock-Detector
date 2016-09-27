package redempt.rcd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import redempt.rcd.scheduler.Task;

public class AutoClockFinder implements Listener {
	
	BlockTimeIndex index = new BlockTimeIndex();
	List<Location> oldClocks = new ArrayList<>();
	List<Page> pages = new ArrayList<>();
	List<Location> clockList = new ArrayList<>();
	
	public Task task;
	
	public AutoClockFinder() {
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
		task = new Task(new Runnable() {
			
			@Override
			public void run() {
				pages.clear();
				clockList.clear();
				List<Location> clocks = new ArrayList<>();
				Map<Location, short[]> locations = index.getLocations();
				for (Location location : locations.keySet()) {
					short[] times = locations.get(location);
					if (times[2] >= 6) {
						if (!containsNear(clocks, location)) {
							clocks.add(location);
						}
					}
				}
				int pos = 0;
				Page page = new Page();
				pages.clear();
				for (Location clock : clocks) {
					if (oldClocks.contains(clock)) {
						for (Region region : Main.regions) {
							if (region.contains(clock)) {
								continue;
							}
						}
						for (Player player : Bukkit.getOnlinePlayers()) {
							if (player.getLocation().distance(clock) <= 50) {
								continue;
							}
						}
						clockList.add(clock);
						if (!page.add(ChatColor.YELLOW + "" + pos + ": " + ChatColor.GREEN + clock.getBlockX() + ", " + clock.getBlockY() + ", " + clock.getBlockZ() + " in " + clock.getWorld().getName())) {
							pages.add(page);
							page = new Page();
						}
						pos++;
						
					}
				}
				if (!page.isEmpty()) {
					pages.add(page);
				}
				if (pos != 0) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.hasPermission("rcd.viewauto")) {
							player.sendMessage(ChatColor.YELLOW + "" + pos + ChatColor.RED + " clocks found by auto scan.");
						}
					}
				}
				oldClocks.clear();
				oldClocks.addAll(clocks);
				clocks.clear();
				index.clear();
			}
			
		}, true, 60000);
		task.start();
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
	
	private boolean containsNear(List<Location> locations, Location loc) {
		for (Location location : locations) {
			if (location.distance(loc) < 10) {
				return true;
			}
		}
		return false;
	}
	
}
