package me.nextx1.cmd;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.nametagedit.plugin.NametagEdit;

import me.nextx1.Main;
import me.nextx1.objetos.Batalha;
import me.nextx1.objetos.BatalhaManager;
import me.nextx1.utils.JSONMessage;
import net.milkbowl.vault.economy.EconomyResponse;

public class X1Command implements CommandExecutor{
	@SuppressWarnings("unused")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§cUse §e/" + label + " ajuda §cpara saber mais!");
			return true;
		}else if ( args.length >= 1 ) {
//			if (!sender.hasPermission("Oi.test.testando.trump.spigot.md5")) {
//				sender.sendMessage("§cCoé tapado, esse comando ainda esta sendo testado disgraça! ");
//				return true;
//			}
			
			if (args[0].equalsIgnoreCase("ajuda") || args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {
				sendHelpMenu(sender, label);
			}if (args[0].equalsIgnoreCase("desafiar")) {
				if (args.length == 1) {
					sender.sendMessage("§cUso correto: /" + label + " " + args[0] + " [jogador]");
					return true;
				}else {
					Player p = (Player)sender;
					if (Main.econ.getBalance(p) < 2500) {
						p.sendMessage("§cVocê precisa de no mínimo 2.500");
						return true;
					}
					BatalhaManager bm = Main.instance.getBatalhaManager();
					if (bm.isInBattleOrPending(p)) {
						if (bm.pendingBattles.containsKey(p.getName())) {
							p.sendMessage("§cVocê já possui um desafio de x1 pendente com " + bm.pendingBattles.get(p.getName()) + "!");
						}else {
							p.sendMessage("§cVocê já está em uma batalha!");
						}
						return true;
					}
					if (bm.inventoryItens.containsKey(p.getName())) {
						p.sendMessage("§cVocê não pode desafiar ninguém enquando possuir drops para coletar, use /x1 drops para coletar!");
						return true;
					}
					if (BatalhaManager.batalhas.size() >= 7) {
						p.sendMessage("§cJá existem 7 batalhas ocorrendo no momento... Tente novamente mais tarde...");
						return true;
					}
					Player target = Bukkit.getPlayer(args[1]);
					if (p.getName().toLowerCase().equalsIgnoreCase(args[1].toLowerCase())) {
						p.sendMessage("§cNao desafie voce mesmo!");
						return true;
					}
					if (Main.econ.getBalance(target) < 2500) {
						p.sendMessage("§cO seu oponente precisa de no mínimo 2.500");
						return true;
					}
					if (bm.isInBattle(target)) {
						sender.sendMessage("§cEste jogador já está em batalha x1!");
						return true;
					}
					if (bm.inventoryItens.containsKey(target.getName())) {
						p.sendMessage("§cEste jogador não pode receber desafios de x1, pois possui drops de x1 para coletar!");
						return true;
					}
					if ( target != null){
						bm.pendingBattles.put(p.getName(), target.getName());
						target.sendMessage(" ");
						target.sendMessage("§eVocê foi desafiado no X1 por " + NametagEdit.getApi().getNametag(p).getPrefix() + p.getName());
						JSONMessage.create("§7Clique").then(" §aAQUI §7").runCommand("/x1 aceitar " + p.getName()).tooltip("§aAceitar X1 de " + p.getName())
							.then("§7para aceitar ou").then("§c AQUI §7").runCommand("/x1 recusar " + p.getName()).tooltip("§cRecusar X1 de " + p.getName())
							.then("§7para negar o desafio.").send(target);;
						target.sendMessage(" ");
						p.sendMessage("§eVocê desafiou o jogador " + BatalhaManager.getColorFromPlayer(target) + target.getName() + "§r§e para o X1.");
						new BukkitRunnable() {
							@Override
							public void run() {
								if (bm.pendingBattles.containsKey(p.getName())) {
									if (bm.pendingBattles.get(p.getName()) == target.getName()) {
										bm.pendingBattles.remove(p.getName());
										if ( p != null) {
											p.sendMessage("§cO seu convite de x1 para " + target.getName() + " expirou!");
										}
										if (target != null) {
											target.sendMessage("§cVocê não aceitou nem recusou o desafio de x1 de " + p.getName() +", pedido expirado!");
										}
									}
								}
							}
						}.runTaskLaterAsynchronously(Main.instance, 20*45);
						
					}else {
						p.sendMessage("§cJogador não encontrado...");
						return true;
					}
				}
			}if (args[0].equalsIgnoreCase("drops")) {
				Player p = (Player)sender;
				BatalhaManager bm = Main.instance.getBatalhaManager();
				if (bm.inventoryItens.containsKey(p.getName())) {
					p.sendMessage("§4 *§c Aviso: Você só pode abrir esse menu §numa§r§c vez! Pegue tudo que quiser agora, pois depois esse menu não estará disponível!");
					p.openInventory(bm.inventoryItens.get(p.getName()));
					bm.inventoryItens.remove(p.getName());
				}else {
					p.sendMessage("§cVocê não possui nenhum drop para coletar!");
				}
			}if (args[0].equalsIgnoreCase("aceitar")) {
				if (args.length == 1) {
					sender.sendMessage("§cUso correto: /" + label + " " + args[0] + " [jogador]");
					return true;
				}else {
					BatalhaManager bm = Main.instance.getBatalhaManager();
					
					Player p = (Player)sender;
					if (Main.instance.getBatalhaManager().inventoryItens.containsKey(p.getName())) {
						p.sendMessage("§cVocê não pode desafiar ninguém enquando possuir drops para coletar, use /x1 drops para coletar!");
						return true;
					}
					Player target = Bukkit.getPlayer(args[1]);
					if (bm.isInBattle(p)) {
						return true;
					}
					if (bm.isInBattle(target)) {
						return true;
					}
					if ( target != null){
						
						EconomyResponse er = Main.econ.withdrawPlayer(target, 2500);
						if (!er.transactionSuccess()) {
							target.sendMessage("§cNão foi possível iniciar a batalha, um dos dois oponentes não possuem 2.500");
							p.sendMessage("§cNão foi possível iniciar a batalha, um dos dois oponentes não possuem 2.500");
							return true;
						}
						EconomyResponse er2 = Main.econ.withdrawPlayer(p, 2500);
						if (!er2.transactionSuccess()) {
							target.sendMessage("§cNão foi possível iniciar a batalha, um dos dois oponentes não possuem 2.500");
							p.sendMessage("§cNão foi possível iniciar a batalha, um dos dois oponentes não possuem 2.500");
							return true;
						}
						if (bm.newBattle(target, p)) {
							Bukkit.broadcastMessage("§c[X1] §f" + BatalhaManager.getColorFromPlayer(p) + p.getName() + "§r§f aceitou o X1 de " + BatalhaManager.getColorFromPlayer(target) + target.getName() + "§r§f.");
							JSONMessage json = JSONMessage.create("§c[X1] §fClique AQUI para assistir.").tooltip("§fCamarote do X1").runCommand("/x1 camarote " + target.getName());
							for (Player pa : Bukkit.getOnlinePlayers()) {
								json.send(pa);
							}
						}
					}else {
						p.sendMessage("§cJogador não encontrado...");
						return true;
					}
				}
			}if (args[0].equalsIgnoreCase("recusar")) {
				if (args.length == 1) {
					sender.sendMessage("§cUso correto: /" + label + " " + args[0] + " [jogador]");
					return true;
				}else {
					Player p = (Player)sender;
					Player target = Bukkit.getPlayer(args[1]);
					
					if (target != null) {
						Main.instance.getBatalhaManager().denyBattle(target, p);
					} else {
						p.sendMessage("§cJogador não encontrado...");
						return true;
					}
				}
			}if (args[0].equalsIgnoreCase("camarote")) {
				
				if (args.length == 2) {
					Player p = Bukkit.getPlayer(args[1]);
					Player p2 = (Player) sender;
					if (Main.instance.getBatalhaManager().isInBattle(p2)) {
						p2.sendMessage("§cVoce nao pode assistir batalhas! Voce está participando de uma.");
						return false;
					}
					if (p != null) {
						Batalha b = BatalhaManager.getBatalha(p);
						Main.instance.getBatalhaManager().removeSpectator(p2);
						Main.instance.getBatalhaManager().addSpectator(p2, b);
						sender.sendMessage("§eVocê está assistindo a batalha x1! Use §f/x1 camarote§e para sair!!");
					} else {
						sender.sendMessage("§cJogador nao encontrado...");
					}
					return false;
				}

				Player p = (Player) sender;
				Inventory inv = Bukkit.createInventory(null, 3*9, "§8Batalhas ocorrendo");
				inv.setItem(0, getFakeItem());
				inv.setItem(1, getFakeItem());
				inv.setItem(2, getFakeItem());
				inv.setItem(3, getFakeItem());
				inv.setItem(4, getFakeItem());
				inv.setItem(5, getFakeItem());
				inv.setItem(6, getFakeItem());
				inv.setItem(7, getFakeItem());
				inv.setItem(8, getFakeItem());
				inv.setItem(9, getFakeItem());
				inv.setItem(17, getFakeItem());
				inv.setItem(18, getFakeItem());
				inv.setItem(19, getFakeItem());
				inv.setItem(20, getFakeItem());
				inv.setItem(21, getFakeItem());
				inv.setItem(22, getFakeItem());
				inv.setItem(23, getFakeItem());
				inv.setItem(24, getFakeItem());
				inv.setItem(25, getFakeItem());
				inv.setItem(26, getFakeItem());
				if (BatalhaManager.batalhas.size() == 0) {
					inv.setItem(13, getNoBattles());
				}else {
					int i = -1;
					for (Batalha b : BatalhaManager.batalhas) {
						i++;
						inv.addItem(getBattleItem(p, i));
					}
				}
				inv.remove(getFakeItem());
				p.openInventory(inv);
			}if (args[0].equalsIgnoreCase("teste")) {
				Main.instance.getArenaManager().clearIceBlocks();
			}
		}
		return false;
	}

	private ItemStack getBattleItem(Player p, int i2) {
		Batalha b = BatalhaManager.batalhas.get(i2);
		ItemStack i = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta m = (SkullMeta) i.getItemMeta();
		m.setDisplayName("§7#" + (i2 + 1));
		ArrayList<String> l = new ArrayList<>();
		l.add(" ");
		l.add(" §7" + NametagEdit.getApi().getNametag(b.getP1()).getPrefix() + b.getP1().getName() + "§c vs. §7" + NametagEdit.getApi().getNametag(b.getP2()).getPrefix() + b.getP2().getName() );
		l.add(" ");
		l.add(" §7Espectadores: §f");
		if (b.getEspectadores().size() <= 0) {
			l.add(" §7- §fNenhum espectador!");
			l.add(" ");
		}else {
			for (String s : b.getEspectadores()) {
				l.add(" §8*§7 " + NametagEdit.getApi().getNametag(Bukkit.getPlayer(s)).getPrefix() + s);
			}
		}
		if (b.getEspectadores().contains(p.getName())) {
			l.add(" §c§lVocê esta aqui! Clique para sair!");
		}else {
			l.add(" §eClique para assistir essa batalha!");
		}
		l.add(" ");
		m.setOwner(b.getP1().getName());
		m.setLore(l);
		i.setItemMeta(m);
		return i;
	}
	private ItemStack getNoBattles() {
		ItemStack i = new ItemStack(Material.STAINED_GLASS_PANE, 0, (short) 14);
		ItemMeta m = i.getItemMeta();
		m.setDisplayName("§cNenhuma batalha x1 ocorrendo");
		i.setItemMeta(m);
		return i;
	}
	private ItemStack getFakeItem() {
		ItemStack i = new ItemStack(Material.BREAD);
		ItemMeta m = i.getItemMeta();
		m.setDisplayName("§aFAKEITEM");
		i.setItemMeta(m);
		return i;
	}

	private void sendHelpMenu(CommandSender sender, String label) {
		sender.sendMessage(" ");
		sender.sendMessage(" §6§lDesafiar para x1 §7Lista de comandos");
		sender.sendMessage(" ");
		sender.sendMessage(" §7/" + label +" desafiar [jogador] §fDesafia um jogador");
		sender.sendMessage(" §7/" + label +" aceitar [jogador] §fAceuta um desafio");
		sender.sendMessage(" §7/" + label +" recusar [jogador] §fRecusa um desafio");
		sender.sendMessage(" §7/" + label +" camarote §fAssiste alguma batalha");
		sender.sendMessage(" §7/" + label +" drops §fColeta os drops de uma batalha");
		sender.sendMessage(" ");
	}
}
