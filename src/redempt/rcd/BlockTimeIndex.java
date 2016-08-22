package redempt.rcd;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BlockTimeIndex {
	
	//Index 0 is the last tick in which the redstone was updated
	//Index 1 is the interval
	//Index 2 is the count
	private Map<Location, short[]> locations = new HashMap<>();
	private short tick = 0;
	
	public BlockTimeIndex() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			
			@Override
			public void run() {
				tick++;
			}
			
		}, 1, 1);
	}
	
	public void add(Location loc) {
		short[] times = locations.get(loc);
		if (times == null) {
			times = new short[3];
			times[0] = -1;
			times[1] = 0;
			times[2] = 0;
		}
		if (times[0] == -1) {
			times[0] = getTick();
			times[2]++;
		} else if (times[1] == 0) {
			times[1] = (short) (getTick() - times[0]);
			times[2]++;
			times[0] = getTick();
		} else if (times[0] + times[1] == getTick()) {
			times[2]++;
			times[0] = getTick();
		}
		locations.put(loc, times);
	}
	
	public Map<Location, short[]> getLocations() {
		return locations;
	}
	
	public void clear() {
		locations.clear();
		tick = 0;
	}
	
	public short getTick() {
		return tick;
	}

}
