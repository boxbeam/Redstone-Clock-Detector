package redempt.rcd;

import org.bukkit.Location;

public class Region {

	public Location first;
	public Location second;
	private String name;

	public Region(Location first, Location second, String name) {
		if (!first.getWorld().equals(second.getWorld())) {
			throw new IllegalArgumentException("The corners are not in the same world!");
		}
		this.first = first;
		this.second = second;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Location getCenter() {
		Location center = first.clone().subtract(first.clone().subtract(second.clone()).multiply(0.5));
		return center;
	}

	public boolean contains(Location point) {
		if (!point.getWorld().equals(first.getWorld())) {
			return false;
		}
		double minx = lesser(first.getX(), second.getX());
		double miny = lesser(first.getY(), second.getY());
		double minz = lesser(first.getZ(), second.getZ());
		double maxx = greater(first.getX(), second.getX()) + 1;
		double maxy = greater(first.getY(), second.getY()) + 1;
		double maxz = greater(first.getZ(), second.getZ()) + 1;
		if (point.getX() >= minx && point.getX() <= maxx) {
			if (point.getY() >= miny && point.getY() <= maxy) {
				if (point.getZ() >= minz && point.getZ() <= maxz) {
					return true;
				}
			}
		}
		return false;
	}

	private double greater(double a, double b) {
		if (a > b) {
			return a;
		} else {
			return b;
		}
	}

	private double lesser(double a, double b) {
		if (a < b) {
			return a;
		} else {
			return b;
		}
	}
	
}