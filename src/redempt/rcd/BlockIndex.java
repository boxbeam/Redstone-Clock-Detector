package redempt.rcd;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;

public class BlockIndex {

	private List<Location> locations = new ArrayList<>();

	public BlockIndex() {

	}

	public boolean add(Location loc) {
		for (Location location : locations) {
			if (location.distance(loc) < 10) {
				return false;
			}
		}
		locations.add(loc);
		return true;
	}
	
	public List<Location> getLocations() {
		return locations;
	}

	public boolean removeNear(Location loc) {
		for (Location location : locations) {
			if (location.distance(loc) < 10) {
				locations.remove(location);
				return true;
			}
		}
		return false;
	}

	public boolean removeExact(Location loc) {
		if (locations.contains(loc)) {
			locations.remove(loc);
			return true;
		}
		return false;
	}
	
	public boolean containsNear(Location loc) {
		for (Location location : locations) {
			if (location.distance(loc) < 10) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsExact(Location loc) {
		return locations.contains(loc);
	}

}
