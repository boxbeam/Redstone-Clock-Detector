package redempt.rcd;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import com.nemez.cmdmgr.Command;
import com.nemez.cmdmgr.CommandManager;

public class Main extends JavaPlugin implements Listener {
	
	public static JavaPlugin plugin;
	public static AutoClockFinder finder;
	private static Map<Player, Location[]> unfinished = new HashMap<>();
	public static List<Region> regions = new ArrayList<>();
	public static Map<Player, ClockFinder> players = new HashMap<>();
	public static List<Page> regionPages = new ArrayList<>();
	
	@Override
	public void onEnable() {
		plugin = this;
		finder = new AutoClockFinder();
		try {
			File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			JarFile jar = new JarFile(file);
			CommandManager.registerCommand(jar.getInputStream(jar.getEntry("command.cmd")), this, this);
			jar.close();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		finder.task.stop();
	}
	
	@Command(hook = "region_clear")
	public void regionClear(CommandSender sender) {
		regions.clear();
		sender.sendMessage(ChatColor.GREEN + "All regions have been cleared.");
	}
	
	@Command(hook = "region_pos1")
	public void regionSetFirst(CommandSender sender) {
		Player player = (Player) sender;
		Location[] locs = unfinished.get(player);
		if (locs == null) {
			locs = new Location[2];
		}
		locs[0] = player.getLocation().getBlock().getLocation();
		unfinished.put(player, locs);
		player.sendMessage(ChatColor.GREEN + "First position set!");
	}
	
	@Command(hook = "region_pos2")
	public void regionSetSecond(CommandSender sender) {
		Player player = (Player) sender;
		Location[] locs = unfinished.get(player);
		if (locs == null) {
			locs = new Location[2];
		}
		locs[1] = player.getLocation().getBlock().getLocation();
		unfinished.put(player, locs);
		player.sendMessage(ChatColor.GREEN + "Second position set!");
	}
	
	@Command(hook = "region_define")
	public void regionDefine(CommandSender sender, String name) {
		Player player = (Player) sender;
		Location[] locs = unfinished.get(player);
		if (locs == null) {
			player.sendMessage(ChatColor.RED + "You do not have both points set!");
			return;
		}
		Location first = locs[0];
		Location second = locs[1];
		if (!first.getWorld().equals(second.getWorld())) {
			player.sendMessage(ChatColor.RED + "The selected points are not in the same world!");
			return;
		}
		if (first == null || second == null) {
			player.sendMessage(ChatColor.RED + "You do not have both points set!");
			return;
		}
		for (Region region : regions) {
			if (name.equalsIgnoreCase(region.getName())) {
				player.sendMessage(ChatColor.RED + "A region by that name already exists!");
				return;
			}
		}
		Region region = new Region(first, second, name);
		regions.add(region);
		unfinished.put(player, null);
		player.sendMessage(ChatColor.GREEN + "Region defined!");
	}
	
	@Command(hook = "region_tp")
	public void regionTeleport(CommandSender sender, String name) {
		Player player = (Player) sender;
		for (Region region : regions) {
			if (region.getName().equalsIgnoreCase(name)) {
				player.teleport(region.getCenter());
				return;
			}
		}
		player.sendMessage(ChatColor.RED + "No region exists by that name!");
	}
	
	@Command(hook = "region_del")
	public void regionDelete(CommandSender sender, String name) {
		for (Region region : regions) {
			if (region.getName().equalsIgnoreCase(name)) {
				regions.remove(region);
				sender.sendMessage(ChatColor.GREEN + "Region deleted!");
				return;
			}
		}
		sender.sendMessage(ChatColor.RED + "No region exists by that name!");
	}
	
	private void updateRegionList() {
		regionPages.clear();
		Page page = new Page();
		for (Region region : regions) {
			if (!page.add(ChatColor.YELLOW + region.getName() + ": " + ChatColor.GREEN + "(" + locationToString(region.first) + "), (" + locationToString(region.second) + ") in " + region.first.getWorld().getName())) {
				regionPages.add(page);
				page = new Page();
			}
		}
		if (!page.isEmpty()) {
			regionPages.add(page);
		}
	}
	
	@Command(hook = "region_list")
	public void regionList(CommandSender sender, int page) {
		updateRegionList();
		if (page < 1 || page > regionPages.size()) {
			if (regionPages.isEmpty()) {
				sender.sendMessage(ChatColor.RED + "Invalid page number! (No existing entries)");
			} else {
				sender.sendMessage(ChatColor.RED + "Invalid page number! (Bounds: 1 to " + regionPages.size() + ")");
			}
			return;
		}
		sender.sendMessage(ChatColor.GREEN + "Page: " + page + " / " + regionPages.size());
		for (String message : regionPages.get(page - 1).entries) {
			if (message == null) {
				return;
			}
			sender.sendMessage(message);
		}
	}
	
	@Command(hook = "region_list_1")
	public void regionListFirst(CommandSender sender) {
		regionList(sender, 1);
	}
	
	@Command(hook = "list_auto")
	public void listAuto(CommandSender sender, int page) {
		if (page < 1 || page > finder.pages.size()) {
			if (finder.pages.isEmpty()) {
				sender.sendMessage(ChatColor.RED + "Invalid page number! (No existing entries)");
			} else {
				sender.sendMessage(ChatColor.RED + "Invalid page number! (Bounds: 1 to " + finder.pages.size() + ")");
			}
			return;
		}
		sender.sendMessage(ChatColor.GREEN + "Page: " + page + " / " + finder.pages.size());
		Page p = finder.pages.get(page - 1);
		for (String message : p.entries) {
			if (message == null) {
				return;
			}
			sender.sendMessage(message);
		}
	}
	
	@Command(hook = "list_auto_1")
	public void listAutoFirst(CommandSender sender) {
		listAuto(sender, 1);
	}
	
	@Command(hook = "tp_auto")
	public void tpAuto(CommandSender sender, int num) {
		if (num < 0 || num > finder.clockList.size()) {
			if (finder.pages.isEmpty()) {
				sender.sendMessage(ChatColor.RED + "Invalid entry number! (No existing entries)");
			} else {
				sender.sendMessage(ChatColor.RED + "Invalid entry number! (Bounds: 0 to " + (finder.clockList.size() - 1) + ")");
			}
			return;
		}
		Player player = (Player) sender;
		player.teleport(finder.clockList.get(num));
	}
	
	@Command(hook = "scan")
	public void scan(CommandSender sender, int seconds) {
		Player player = (Player) sender;
		if (seconds < 1) {
			sender.sendMessage(ChatColor.RED + "Invalid amount!");
		}
		if (players.get(player) != null) {
			players.get(player).stop();
		}
		ClockFinder clockFinder = new ClockFinder(seconds, player);
		players.put(player, clockFinder);
		player.sendMessage(ChatColor.GREEN + "Started scan for " + seconds + " seconds");
	}
	
	@Command(hook = "list")
	public void list(CommandSender sender, int page) {
		Player player = (Player) sender;
		ClockFinder finder = players.get(player);
		if (finder == null || !finder.finished) {
			sender.sendMessage(ChatColor.RED + "Invalid page number! (No existing entries)");
			return;
		}
		if (page < 1 || page > finder.pages.size()) {
			if (finder.pages.isEmpty()) {
				sender.sendMessage(ChatColor.RED + "Invalid page number! (No existing entries)");
			} else {
				sender.sendMessage(ChatColor.RED + "Invalid page number! (Bounds: 1 to " + finder.pages.size() + ")");
			}
			return;
		}
		sender.sendMessage(ChatColor.GREEN + "Page: " + page + " / " + finder.pages.size());
		Page p = finder.pages.get(page - 1);
		for (String message : p.entries) {
			if (message == null) {
				return;
			}
			sender.sendMessage(message);
		}
	}
	
	@Command(hook = "list_first")
	public void listFirst(CommandSender sender) {
		list(sender, 1);
	}
	
	@Command(hook = "tp")
	public void tp(CommandSender sender, int num) {
		Player player = (Player) sender;
		ClockFinder finder = players.get(player);
		if (finder == null || !finder.finished) {
			sender.sendMessage(ChatColor.RED + "Invalid entry number! (No existing entries)");
			return;
		}
		if (num < 0 || num > finder.locations.size()) {
			if (finder.pages.isEmpty()) {
				sender.sendMessage(ChatColor.RED + "Invalid entry number! (No existing entries)");
			} else {
				sender.sendMessage(ChatColor.RED + "Invalid entry number! (Bounds: 0 to " + (finder.locations.size() - 1) + ")");
			}
			return;
		}
		player.teleport(finder.locations.get(num));
	}
	
	private String locationToString(Location loc) {
		return loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
	}
	
}