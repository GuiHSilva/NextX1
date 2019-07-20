package me.nextx1;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.nextx1.cmd.Listeners;
import me.nextx1.cmd.X1Command;
import me.nextx1.objetos.ArenaManager;
import me.nextx1.objetos.Batalha;
import me.nextx1.objetos.BatalhaManager;
import net.milkbowl.vault.economy.Economy;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class Main extends JavaPlugin{
	public static Main instance;
	private static BatalhaManager batalhaManager;
	private ArenaManager arenaManager;
	public SimpleClans core;
	public static Economy econ = null;
	
	@Override
	public void onEnable() {
		
		instance = this;
		print("Inicializando o x1 by: " + getDescription().getAuthors().toString().replace("[", "").replace("]", "") + " v" + getDescription().getVersion());
		registerCommands();
		hookSimpleClans();
		setupEconomy();
		batalhaManager = new BatalhaManager();
		arenaManager = new ArenaManager(
		/*P1*/	new Location(Bukkit.getWorld("world"), 800.5, 53.6, 390.5, 0, 0),
		/*P2*/	new Location(Bukkit.getWorld("world"), 800.5, 53.6, 410.5, 180, 0), 
	  /*spec*/  new Location(Bukkit.getWorld("world"), 800.5, 57.6, 400.5, 0, 38), 
	/* home*/	new Location(Bukkit.getWorld("world"), 0.5, 115.5, 0.5, 0, 0));
//		arenaManager = new ArenaManager(new Location(Bukkit.getWorld("world"), 77.5, 57.5, 62.5, 0,0),
//				new Location(Bukkit.getWorld("world"), 77.5, 57.5, 72.5, 180, 0), 
//				new Location(Bukkit.getWorld("world"), 77.5, 57.5, 83.5, -180, 0), 
//				new Location(Bukkit.getWorld("world"), 0.5, 52.5, 0.5, 0, 0));
		getServer().getPluginManager().registerEvents(new Listeners(), this);
	}

	private void print(String string) {
		ConsoleCommandSender cd = Bukkit.getConsoleSender();
		cd.sendMessage("§e[NextX1] " + string);
	}
	
	public BatalhaManager getBatalhaManager() {
		return batalhaManager;
	}
	
	public ArenaManager getArenaManager() {
		return arenaManager;
	}

	@Override
	public void onDisable() {
		batalhaManager.cancelAllBattles();
		for (Batalha b1 : BatalhaManager.batalhas) {
			for (Batalha b2 : BatalhaManager.batalhas) {
				b1.getP1().showPlayer(b2.getP1());
				b1.getP1().showPlayer(b2.getP2());
				b1.getP2().showPlayer(b2.getP1());
				b1.getP2().showPlayer(b2.getP2());
				
				b1.getP1().teleport(getArenaManager().getHome());
				b2.getP1().teleport(getArenaManager().getHome());
				b1.getP2().teleport(getArenaManager().getHome());
				b2.getP2().teleport(getArenaManager().getHome());
				
				for (String players : b1.getEspectadores()) {
					if (Bukkit.getPlayer(players) != null) {
						Bukkit.getPlayer(players).showPlayer(b2.getP1());
						Bukkit.getPlayer(players).showPlayer(b2.getP2());
					}
					
					Bukkit.getPlayer(players).teleport(getArenaManager().getHome());
				}
			}
		}
	}
	private boolean hookSimpleClans() {
		try {
			for (Plugin plugin : getServer().getPluginManager().getPlugins()) {
				if (plugin instanceof SimpleClans) {
					core = (SimpleClans) plugin;
					return true;
				}
			}
		} catch (NoClassDefFoundError e) {
			return false;
		}

		return false;
	}

	private void registerCommands() {
		getCommand("x1").setExecutor(new X1Command());
		
	}
	
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
}
