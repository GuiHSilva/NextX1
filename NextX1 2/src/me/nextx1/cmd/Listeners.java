package me.nextx1.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.nextx1.Main;
import me.nextx1.objetos.Batalha;
import me.nextx1.objetos.BatalhaManager;

public class Listeners implements Listener {

	@EventHandler
	private void aoClicar(InventoryClickEvent e) {
		if (e.getInventory().getName().equalsIgnoreCase("§8Batalhas ocorrendo")) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null) {
				if (e.getCurrentItem().getType() != null) {
					if (e.getCurrentItem().getType() != Material.AIR) {
						if (e.getCurrentItem().hasItemMeta()) {
							if (e.getCurrentItem().getItemMeta().hasDisplayName()) {
								if (e.getCurrentItem().getItemMeta().getDisplayName()
										.equalsIgnoreCase("§cNenhuma batalha x1 ocorrendo")) {
									return;
								}
								Player p = (Player) e.getWhoClicked();
								int i = Integer.parseInt(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName().replaceAll("#", "")));

								if (BatalhaManager.batalhas.size() <= 0) {
									p.sendMessage("§cNão está ocorrendo nenhuma batalha no momento!");
									return;
								}
								if (BatalhaManager.batalhas.get(i - 1) == null) {
									p.sendMessage(ChatColor.RED + "Essa batalha não existe mais seu cabra!");
									return;
								}
								
								Batalha b = BatalhaManager.batalhas.get((i - 1));

								for (String s : e.getCurrentItem().getItemMeta().getLore()) {
									if (s.contains("Clique para sair")) {
										Main.instance.getBatalhaManager().removeSpectator(p);
									} else if (s.contains("Clique para assistir")) {
										Main.instance.getBatalhaManager().removeSpectator(p);
										Main.instance.getBatalhaManager().addSpectator(p, b);
									}
								}
								
								// }

								if (b.getP1().getName().equals(p.getName()) || b.getP2().getName().equals(p.getName()) || b.getEspectadores().contains(p.getName())) {
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	void teste(PlayerTeleportEvent e) {
		Main.instance.getBatalhaManager();
		for (Batalha s : BatalhaManager.batalhas) {
			if (s.getEspectadores().contains(e.getPlayer().getName())) {
				Main.instance.getBatalhaManager().removeSpectator(e.getPlayer());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDeath(PlayerQuitEvent event) {
		Batalha batalha = BatalhaManager.getBatalha(event.getPlayer());
		if (batalha != null) {
			if (batalha.getP1().getName().equals(event.getPlayer().getName())
					|| batalha.getP2().getName().equals(event.getPlayer().getName())) {
				if (batalha.getP1().equals(event.getPlayer())) {
					Inventory inv = Bukkit.createInventory(batalha.getP2(), 9*6, "X1 - Drop de " + batalha.getP2().getName());
					for (ItemStack i : batalha.getP1().getInventory().getContents()) {
						if (i != null) {
							if (!i.getType().equals(Material.AIR)) {
								inv.addItem(i);
							}
						}
					}
					for (ItemStack i : batalha.getP1().getInventory().getArmorContents()) {
						if (i != null) {
							if (!i.getType().equals(Material.AIR)) {
								inv.addItem(i);
							}
						}
					}
					batalha.getP2().setLevel(batalha.getP1().getLevel() + batalha.getP2().getLevel());
					batalha.getP1().getInventory().clear();
					batalha.getP1().getInventory().setHelmet(new ItemStack(Material.AIR));
					batalha.getP1().getInventory().setChestplate(new ItemStack(Material.AIR));
					batalha.getP1().getInventory().setLeggings(new ItemStack(Material.AIR));
					batalha.getP1().getInventory().setBoots(new ItemStack(Material.AIR));
					Main.instance.getBatalhaManager().inventoryItens.put(batalha.getP2().getName(), inv);
					Main.instance.getBatalhaManager().setWinner(batalha, batalha.getP2());
				} else {
					Inventory inv = Bukkit.createInventory(batalha.getP1(), 9*6, "X1 - Drop de " + batalha.getP1().getName());
					for (ItemStack i : batalha.getP2().getInventory().getContents()) {
						if (i != null) {
							if (!i.getType().equals(Material.AIR)) {
								inv.addItem(i);
							}
						}
					}
					for (ItemStack i : batalha.getP2().getInventory().getArmorContents()) {
						if (i != null) {
							if (!i.getType().equals(Material.AIR)) {
								inv.addItem(i);
							}
						}
					}
					batalha.getP1().setLevel(batalha.getP2().getLevel() + batalha.getP1().getLevel());
					batalha.getP2().getInventory().clear();
					batalha.getP2().getInventory().setHelmet(new ItemStack(Material.AIR));
					batalha.getP2().getInventory().setChestplate(new ItemStack(Material.AIR));
					batalha.getP2().getInventory().setLeggings(new ItemStack(Material.AIR));
					batalha.getP2().getInventory().setBoots(new ItemStack(Material.AIR));
					Main.instance.getBatalhaManager().inventoryItens.put(batalha.getP1().getName(), inv);
					Main.instance.getBatalhaManager().setWinner(batalha, batalha.getP1());
				}
			} else {
				Main.instance.getBatalhaManager().removeSpectator(event.getPlayer());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player && event.getEntity().getKiller() instanceof Player) {
			// Player killed = event.getEntity(); // Jogador que Morreu;
			Player killer = event.getEntity().getKiller(); // Jogador que Matou;
			Player player = event.getEntity(); //Jogador qe morreu;
			
			Batalha batalha = BatalhaManager.getBatalha(killer);

			if (batalha != null) {
				
				Inventory inv = Bukkit.createInventory(killer, 9*6, "X1 - Drop de " + player.getName());
				for (ItemStack i : event.getDrops()) {
					inv.addItem(i);
				}
				killer.setLevel(killer.getLevel() + player.getLevel());
				event.getDrops().clear();
				Main.instance.getBatalhaManager().inventoryItens.put(killer.getName(), inv);
				
				if (batalha.getP1().getName().equals(killer.getName())
						|| batalha.getP2().getName().equals(killer.getName())) {
					Main.instance.getBatalhaManager().setWinner(batalha, killer);
				} else {
					Main.instance.getBatalhaManager().removeSpectator(killer);
				}
			}
		}
	}


}
