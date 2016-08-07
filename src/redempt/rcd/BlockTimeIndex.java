package redempt.rcd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;

public class BlockTimeIndex {

	private Map<Long, List<Location>> locations = new HashMap<>();

	public BlockTimeIndex() {

	}
	
	public void add(Location loc) {
		List<Location> locs = locations.get(getTick());
		if (locs == null) {
			locs = new ArrayList<>();
		}
		locs.add(loc);
		locations.put(loc.getWorld().getFullTime(), locs);
	}
	
	public Map<Long, List<Location>> getLocations() {
		return locations;
	}
	
	public void clear() {
		locations.clear();
	}
	
	public long getTick() {
		return System.currentTimeMillis() / 50;
	}

}
