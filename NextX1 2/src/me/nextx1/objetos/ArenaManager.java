package me.nextx1.objetos;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ArenaManager {
	private Location p1;
	private Location p2;
	private Location spectator;
	private Location home;
	
	public ArenaManager(Location p1, Location p2, Location spectator, Location home) {
		this.p1 = p1;
		this.p2 = p2;
		this.spectator = spectator;
		this.home = home;
	}

	public Location getP1() {
		return p1;
	}
	
	public Location getP2() {
		return p2;
	}

	public Location getSpectator() {
		return spectator;
	}
	
	public Location getHome() {
		return home;
	}
	
	public void clearIceBlocks() {
		
		
		ArrayList<String> regions = getLocRegions(getP1());
		if (regions.size() > 0) {
			ProtectedRegion rg = WGBukkit.getPlugin().getRegionManager(getP1().getWorld()).getRegion(regions.get(0));
			
			Location loc1 = new Location(getP1().getWorld(), rg.getMaximumPoint().getBlockX(), rg.getMaximumPoint().getBlockY(), rg.getMaximumPoint().getZ());
			Location loc2 = new Location(getP1().getWorld(), rg.getMinimumPoint().getBlockX(), rg.getMinimumPoint().getBlockY(), rg.getMinimumPoint().getZ());
			
			
			int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
	        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
	 
	        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
	        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
	 
	        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
	        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

			for (int x = bottomBlockX; x <= topBlockX; x++) {
				for (int z = bottomBlockZ; z <= topBlockZ; z++) {
					for (int y = bottomBlockY; y <= topBlockY; y++) {
						Location loc = new Location(getP1().getWorld(), x, y, z);
						if (loc.getBlock().getType() == Material.ICE) {
							loc.getBlock().setType(Material.AIR);
						}
					}
				}
			}
			
		}
		
	}
	
	
	public static ArrayList<String> getLocRegions(Location loc){
		WorldGuardPlugin worldGuard = WGBukkit.getPlugin();
		RegionManager regionManager = worldGuard.getRegionManager(loc.getWorld());
		ApplicableRegionSet regions = regionManager.getApplicableRegions(loc);
		ArrayList<String> regionsname = new ArrayList<>();
		if (regions.size() >= 1){
			for (ProtectedRegion r : regions){
				regionsname.add(r.getId());
			}
		}
		return regionsname;
	}
	
}