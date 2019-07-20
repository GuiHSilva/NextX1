package me.nextx1.utils;

import org.bukkit.entity.Player;
import me.nextx1.Main;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
public class ClanFF {
	public static void ativarClanFF(Player p) {
		if (Main.instance.core != null) {
			ClanPlayer cp = Main.instance.core.getClanManager().getClanPlayer(p);
			if (cp != null) {
				Main.instance.core.getClanManager().getClanPlayer(p).setFriendlyFire(true);
			}
		}
	}
	public static void desativarClanFF(Player p) {
		if (Main.instance.core != null) {
			ClanPlayer cp = Main.instance.core.getClanManager().getClanPlayer(p);
			if (cp != null) {
				Main.instance.core.getClanManager().getClanPlayer(p).setFriendlyFire(false);
			}
		}
	}
}
