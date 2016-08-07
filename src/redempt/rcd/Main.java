package redempt.rcd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	
	public static Plugin plugin;
	public static AutoClockFinder finder;
	
	@Override
	public void onEnable() {
		plugin = this;
		finder = new AutoClockFinder();
	}

	@Override
	public void onDisable() {
		
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		return false;
	}
	
}