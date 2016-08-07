package redempt.rcd;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class ClockFinder implements Listener {
	
	public BlockIndex index = new BlockIndex();
	public List<Page> pages = new ArrayList<>();
	
	public ClockFinder(double scanTime, Player player) {
		long time = Math.round(scanTime * 20);
		ClockFinder finder = this;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			
			@Override
			public void run() {
				Page page = new Page();
				int pos = 0;
				for (Location loc : index.getLocations()) {
					if (!page.add(ChatColor.YELLOW + "" + pos + ": " + ChatColor.GREEN + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ())) {
						pages.add(page);
						page = new Page();
					}
					pos++;
				}
				if (!page.isEmpty()) {
					pages.add(page);
				}
				HandlerList.unregisterAll(finder);
			}
			
		}, time);
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
	}
	
	@EventHandler
	public void onRedstoneUpdate(BlockRedstoneEvent e) {
		index.add(e.getBlock().getLocation());
	}
	
}
