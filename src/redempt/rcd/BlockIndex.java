package redempt.rcd;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;

public class BlockIndex {

	private Map<Location, Integer> locations = new HashMap<>();

	public BlockIndex() {

	}
	
	public void clear() {
		locations.clear();
	}
	
	public void add(Location loc) {
		for (Location location : locations.keySet()) {
			if (location.distance(loc) < 10) {
				locations.put(location, locations.get(location) + 1);
				return;
			}
		}
		locations.put(loc, 1);
	}
	
	public Map<Location, Integer> getLocations() {
		return locations;
	}
	
	public boolean containsNear(Location loc) {
		for (Location location : locations.keySet()) {
			if (location.distance(loc) < 10) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsExact(Location loc) {
		return locations.keySet().contains(loc);
	}

}
