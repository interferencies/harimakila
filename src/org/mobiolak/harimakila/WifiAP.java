package org.mobiolak.harimakila;

import java.util.HashSet;
import java.util.Iterator;

import android.util.Log;

public class WifiAP {
	private String essid;
	private int level; // nivel medio ponderado
	private int counter; // numero de capturas
	//private List<Integer> levels; // lista de niveles de la captura
	private HashSet<Integer> levels;
	
	public WifiAP(String e, int l) {
		this.essid = e;
		this.level = l;
		this.counter = 0;
		this.levels = new HashSet<Integer>();
		this.levels.add(l);
	}
	
	public void updateWifiAP(int l) {
		//hace media con el valor anterior:
		Log.d("HARIMAKILA","Update from :"+this.level+" to "+l+" counter:"+this.counter);
		this.counter++;
		this.levels.add(l);
		this.level = ((this.counter*this.level)+l)/(this.counter+1);
		//this.level = (this.level+l)/2;
	}
	
	public String getEssid() {
		return this.essid;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public int getMinLevel() {
		int min = -100;
		Iterator<Integer> i = this.levels.iterator();
		while (i.hasNext()){
			int l = i.next();
			Log.d("HARIMAKILA","MAX.."+min+" ->"+l);
			if (l>min) min=l;
		}
		return min;
	}
	
	public int getMaxLevel() {
		int max = -10;
		Iterator<Integer> i = this.levels.iterator();
		while (i.hasNext()){
			int l = i.next();
			Log.d("HARIMAKILA","MIN.."+max+" ->"+l);
			if (l<max) max=l;
		}
		return max;
	}
}