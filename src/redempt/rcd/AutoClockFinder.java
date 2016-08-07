package redempt.rcd;

import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class AutoClockFinder implements Listener {
	
	BlockTimeIndex index = new BlockTimeIndex();
	
	public AutoClockFinder() {
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			
			@Override
			public void run() {
				Map<Long, List<Location>> locations = index.getLocations();
				for (long key : locations.keySet()) {
					for (Location loc : locations.get(key)) {
						long val = key + 1;
						long interval = 0;
						for (; val < locations.size(); val++) {
							if (locations.get(val) != null && contained(locations.get(val), loc)) {
								interval = val - key;
								if (interval != 0) {
									break;
								}
							}
						}
						if (interval != 0) {
							System.out.println(interval);
							val = key;
							int count = 1;
							while (val - key <= interval * 4) {
								val += interval;
								if (locations.get(val) != null && locations.get(val).contains(loc)) {
									count++;
								}
							}
							if (count > 3) {
								Bukkit.broadcastMessage("Clock found at " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
							}
						}
					}
				}
			}
			
		}, 1200, 1200);
	}
	
	@EventHandler
	public void onRedstoneUpdate(BlockRedstoneEvent e) {
		index.add(e.getBlock().getLocation());
	}
	
	private boolean contained(List<Location> locs, Location loc) {
		for (Location location : locs) {
			if (location.distance(loc) < 1) {
				return true;
			}
		}
		return false;
	}
	
}
