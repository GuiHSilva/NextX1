package me.nextx1.objetos;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.jackproehl.plugins.CombatLog;

import me.nextx1.Main;
import me.nextx1.utils.ClanFF;
import me.nextx1.utils.JSONMessage;
import net.milkbowl.vault.economy.EconomyResponse;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class BatalhaManager {
	
	public static ArrayList<Batalha> batalhas = new ArrayList<>();
	public HashMap<String, String> pendingBattles; // Key = Jogador que desafiou;
	public HashMap<String, Inventory> inventoryItens = new HashMap<>();
	
	public BatalhaManager() {
		pendingBattles = new HashMap<String, String>();
	}
	
	public static Batalha getBatalha(Player p) {
		for (Batalha b : batalhas) {
			if (b.getEspectadores().contains(p.getName())) {
				return b;
			}
			
			if (b.getP1().equals(p) || b.getP2().equals(p)) {
				return b;
			}
		}
		return null;
	}
	
	public boolean isInBattle(Player player) {
		for (Batalha b : batalhas) {
			if (b.getP1().equals(player) || b.getP2().equals(player)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isInBattleOrPending(Player player) {
		if (pendingBattles.get(player.getName()) != null) {
			return true;
		}
		
		if (isInBattle(player)) {
			return true;
		}
		
		return false;
	}
	
	public void denyBattle(Player p1, Player sender) {
		if (pendingBattles.get(p1.getName()) != null) {
			pendingBattles.remove(p1.getName());
			p1.sendMessage(ChatColor.YELLOW + "O jogador " + getColorFromPlayer(sender) + sender.getName() + "§r§e recusou o pedido de Batalha.");
			sender.sendMessage(ChatColor.RED + "Você recusou o pedido de batalha de " + p1.getName() + ".");
			return;
		}
		
		sender.sendMessage(ChatColor.RED + "Você não possui nenhum pedido de batalha.");
	}
	
	/*
	 * Return true if new battle was created
	 */
	public boolean newBattle(Player p1, Player p2) {
		if (pendingBattles.get(p1.getName()) != null) {
			Batalha batalha = new Batalha(p1, p2);
			
			for (Batalha batalhas : batalhas) {
				// Hide players for Player1;
				p1.hidePlayer(batalhas.getP1());
				p1.hidePlayer(batalhas.getP2());
				
				// Hide players for Player2;
				p2.hidePlayer(batalhas.getP1());
				p2.hidePlayer(batalhas.getP2());
				
				batalhas.getP1().hidePlayer(p1);
				batalhas.getP1().hidePlayer(p2);
				batalhas.getP2().hidePlayer(p1);
				batalhas.getP2().hidePlayer(p2);
				for (String spectators : batalhas.getEspectadores()) {
					try {
						Bukkit.getPlayer(spectators).hidePlayer(p1);
						Bukkit.getPlayer(spectators).hidePlayer(p2);
					} catch (NullPointerException ignore) {}
				}
			}
			ClanFF.ativarClanFF(p2);
			ClanFF.ativarClanFF(p1);
			batalhas.add(batalha);
			p1.teleport(Main.instance.getArenaManager().getP1());
			p2.teleport(Main.instance.getArenaManager().getP2());
			p1.setGameMode(GameMode.SURVIVAL);
			p2.setGameMode(GameMode.SURVIVAL);
			
			Main.instance.getArenaManager().clearIceBlocks();
			
			pendingBattles.remove(p1.getName());
			return true;
		}
		
		p2.sendMessage(ChatColor.RED + "Você não possui nenhum pedido de batalha.");
		return false;
	}
	
	public static ChatColor getColorFromPlayer(Player p) {
		PermissionUser user = PermissionsEx.getUser(p);
		@SuppressWarnings("deprecation")
		PermissionGroup group = user.getGroups()[0];
		String prefix = group.getPrefix();
		String chatcolor = ChatColor.getLastColors(prefix.replace("&", "§")).toString().replace("§", "");
		return ChatColor.getByChar(chatcolor);
	}
	
	public void setWinner(Batalha b, Player p) {
		b.setWinner(p);
		CombatLog.taggedPlayers.remove(b.getP1().getName());
		CombatLog.taggedPlayers.remove(b.getP2().getName());
		ClanFF.desativarClanFF(b.getP1());
		ClanFF.desativarClanFF(b.getP2());
		if ( b.getP1() == p) {
			Bukkit.broadcastMessage("§c[X1]§f " + getColorFromPlayer(p) + p.getName() + "§r§f venceu a batalha contra " + getColorFromPlayer(b.getP2())  + b.getP2().getName());
		}else if ( b.getP2() == p){
			Bukkit.broadcastMessage("§c[X1]§f " + getColorFromPlayer(p) + p.getName() + "§r§f venceu a batalha contra " + getColorFromPlayer(b.getP1())  + b.getP1().getName());
		}else {
			Bukkit.broadcastMessage("§c[X1]§f Batalha entre " + getColorFromPlayer(p) + p.getName() + "§r§f e " + getColorFromPlayer(b.getP1()) + b.getP1().getName() + "§r§f empatou!");
		}
		endBattle(p);
	}
	
	public void endBattle(Player player) {
		Batalha batalha = getBatalha(player);
		if (batalha == null) {
			return;
		}
		/*if (batalha.getP1() == player) {
			batalha.setWinner(batalha.getP1());
		}else {
			batalha.setWinner(batalha.getP2());
		} */
		for (Batalha batalhas : batalhas) {
			player.showPlayer(batalhas.getP1());
			player.showPlayer(batalhas.getP2());
			for (String players : batalha.getEspectadores()) {
				if (Bukkit.getPlayer(players) != null) {
					Bukkit.getPlayer(players).showPlayer(batalhas.getP1());
					Bukkit.getPlayer(players).showPlayer(batalhas.getP2());
				}
			}
		}
		batalha.getP2().teleport(Main.instance.getArenaManager().getHome());
		batalha.getP1().teleport(Main.instance.getArenaManager().getHome());
		
		EconomyResponse r = Main.econ.depositPlayer(batalha.getWinner(), 5000);
		if (r.transactionSuccess()) {
			batalha.getWinner().sendMessage(ChatColor.GREEN + "Você recebeu 5.000 por vencer a batalha!");
		}
		
		batalha.getWinner().setHealth(20);
		batalhas.remove(batalha);
		player.sendMessage(" ");
		player.sendMessage("§6§l* §eOs drops da batalha estão disponíveis!");
		JSONMessage json = JSONMessage.create("§6§l* §ePara obter os drops, ").then("§b§lCLIQUE AQUI").runCommand("/x1 drops").then("§r§e!");
		json.send(player);
		player.sendMessage("§4§l* §cAviso: Os drops podem ser acessado apenas §numa§r§c vez!");
		player.sendMessage(" ");
		for (String players : batalha.getEspectadores()) {
			if (Bukkit.getPlayer(players) != null) {
				Bukkit.getPlayer(players).teleport(Main.instance.getArenaManager().getHome());
				Bukkit.getPlayer(players).sendMessage(ChatColor.GREEN + "Essa batalha foi finalizada!");
			}
		}
	}
	
	public void cancelAllBattles() {
		for (Batalha b1 : BatalhaManager.batalhas) {
			for (Batalha b2 : BatalhaManager.batalhas) {
				b1.getP1().showPlayer(b2.getP1());
				b1.getP1().showPlayer(b2.getP2());
				b1.getP2().showPlayer(b2.getP1());
				b1.getP2().showPlayer(b2.getP2());
				for (String players : b1.getEspectadores()) {
					if (Bukkit.getPlayer(players) != null) {
						Bukkit.getPlayer(players).showPlayer(b2.getP1());
						Bukkit.getPlayer(players).showPlayer(b2.getP2());
					}
				}
			}
			
			b1.getP1().teleport(Main.instance.getArenaManager().getHome());
			b1.getP2().teleport(Main.instance.getArenaManager().getHome());
		}
	}
	
	public void addSpectator(Player player, Batalha batalha) {
		if (batalha != null) {
			batalha.addSpectator(player.getName());
			
			for (Batalha b : batalhas) {
				
				if (batalha.getP1().equals(player) || batalha.getP2().equals(player)) {
					return;
				}
				
				player.hidePlayer(b.getP1());
				player.hidePlayer(b.getP2());
			}
			
			player.showPlayer(batalha.getP1());
			player.showPlayer(batalha.getP2());
			
			player.teleport(Main.instance.getArenaManager().getSpectator());
		} else {
			//
		}
	}
	
	public void removeSpectator(Player player) {
		Batalha b = getBatalha(player);
		
		if (b != null) {
			b.getEspectadores().remove(player.getName());
			
			// if (b.isBattle()) {
				// player.showPlayer(b.getP1());
				// player.showPlayer(b.getP2());
				
				for (Batalha batalhas : batalhas) {
					if (b.getP1().equals(player) || b.getP2().equals(player)) {
						return;
					}
					player.showPlayer(batalhas.getP1());
					player.showPlayer(batalhas.getP2());
				}
			//}
			
			player.teleport(Main.instance.getArenaManager().getHome());
		}
	}
}