package me.nextx1.objetos;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class Batalha {
	
	private Player p1;
	private Player p2;
	private Player winner;
	private ArrayList<String> espectadores = new ArrayList<>();
	private boolean battle = true;
	
	public Batalha(Player jogador1, Player jogador2) {
		p1 = jogador1;
		p2 = jogador2;
		espectadores = new ArrayList<>();
	}

	public Player getP1() {
		return p1;
	}

	public void setP1(Player p1) {
		this.p1 = p1;
	}

	public Player getP2() {
		return p2;
	}

	public void setP2(Player p2) {
		this.p2 = p2;
	}

	public ArrayList<String> getEspectadores() {
		return espectadores;
	}

	public void setEspectadores(ArrayList<String> espectadores) {
		this.espectadores = espectadores;
	}
	
	public void addSpectator(String player) {
		this.espectadores.add(player);
	}
	
	public boolean isBattle() {
		return battle;
	}
	
	public void setBattle(boolean battle) {
		this.battle = battle;
	}

	public Player getWinner() {
		return winner;
	}

	public void setWinner(Player winner) {
		this.winner = winner;
	}
	
}
